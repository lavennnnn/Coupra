package cn.hush.Coupra.merchant.admin.job;


import cn.hush.Coupra.merchant.admin.common.enums.CouponTaskStatusEnum;
import cn.hush.Coupra.merchant.admin.dao.entity.CouponTaskDO;
import cn.hush.Coupra.merchant.admin.dao.mapper.CouponTaskMapper;
import cn.hush.Coupra.merchant.admin.mq.event.CouponTaskExecuteEvent;
import cn.hush.Coupra.merchant.admin.mq.producer.CouponTaskActualExecuteProducer;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.db.DaoTemplate;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @program: Coupra
 * @description: 优惠券推送任务扫描定时发送记录 XXL-Job 处理器
 * @author: Hush
 * @create: 2025-08-19 20:29
 **/
@Component
@RequiredArgsConstructor
public class CouponTaskJobHandler extends IJobHandler {

    private final CouponTaskMapper couponTaskMapper;
    private final CouponTaskActualExecuteProducer couponTaskActualExecuteProducer;

    private static final int MAX_LIMIT = 100;

    @XxlJob(value = "couponTemplateTask")
    @Override
    public void execute() throws Exception {
        long initId = 0;
        Date now = new Date();

        while (true) {
            // 获取已到执行时间待执行的优惠券定时分发任务
            List<CouponTaskDO> couponTaskDOList = fetchPendingTasks(initId, now);

            if (CollUtil.isEmpty(couponTaskDOList)) {
                break;
            }

            // 调用分发服务对用户发送优惠券
            for (CouponTaskDO each : couponTaskDOList) {
                distributeCoupon(each);
            }

            // 查询出来的数据如果小于 MAX_LIMIT 意味着后面将不再有数据，返回即可
            if (couponTaskDOList.size() < MAX_LIMIT) {
                break;
            }

            // 更新 initId 为当前列表中最大 ID
            initId = couponTaskDOList.stream()
                    .mapToLong(CouponTaskDO::getId)
                    .max()
                    .orElse(initId);
        }

    }

    private List<CouponTaskDO> fetchPendingTasks(long initId, Date now) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(CouponTaskDO::getStatus, CouponTaskStatusEnum.PENDING.getStatus())
                .le(CouponTaskDO::getSendTime, now)
                .gt(CouponTaskDO::getId, initId)
                .limit(MAX_LIMIT);

        return couponTaskMapper.selectListByQuery(queryWrapper);
    }

    private void distributeCoupon(CouponTaskDO couponTask) {

        // 修改延时执行推送任务任务状态为执行中
        CouponTaskDO couponTaskDO = CouponTaskDO.builder()
                .id(couponTask.getId())
                .status(CouponTaskStatusEnum.IN_PROGRESS.getStatus())
                .build();
        couponTaskMapper.update(couponTaskDO);
        // 通过消息队列发送消息，由分发服务消费者消费该消息
        CouponTaskExecuteEvent couponTaskExecuteEvent = CouponTaskExecuteEvent.builder()
                .couponTaskId(couponTask.getId())
                .build();
        couponTaskActualExecuteProducer.sendMessage(couponTaskExecuteEvent);


    }

}
