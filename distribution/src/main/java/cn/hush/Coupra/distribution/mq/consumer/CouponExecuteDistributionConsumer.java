package cn.hush.Coupra.distribution.mq.consumer;

import cn.hush.Coupra.distribution.dao.mapper.CouponTaskFailMapper;
import cn.hush.Coupra.distribution.dao.mapper.CouponTaskMapper;
import cn.hush.Coupra.distribution.dao.mapper.CouponTemplateMapper;
import cn.hush.Coupra.distribution.dao.mapper.UserCouponMapper;

import cn.hush.Coupra.distribution.mq.base.MessageWrapper;
import cn.hush.Coupra.distribution.mq.event.CouponTemplateDistributionEvent;
import com.alibaba.fastjson2.JSON;
import com.oracle.truffle.api.library.Message;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;

/**
 * 优惠券执行分发到用户消费者
 */
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "Coupra_distribution-service_coupon-execute-distribution_topic${unique-name:}",
        consumerGroup = "Coupra_distribution-service_coupon-execute-distribution_cg${unique-name:}"
)
@Slf4j(topic = "CouponExecuteDistributionConsumer")
public class CouponExecuteDistributionConsumer implements RocketMQListener<MessageWrapper<CouponTemplateDistributionEvent>> {

    private final UserCouponMapper userCouponMapper;
    private final CouponTemplateMapper couponTemplateMapper;
    private final CouponTaskMapper couponTaskMapper;
    private final CouponTaskFailMapper couponTaskFailMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Lazy
    @Resource
    private CouponExecuteDistributionConsumer couponExecuteDistributionConsumer;

    private final static int BATCH_USER_COUPON_SIZE = 5000;
    private static final String BATCH_SAVE_USER_COUPON_LUA_PATH = "lua/batch_user_coupon_list.lua";
    private final String excelPath = Paths.get("").toAbsolutePath() + "/tmp";


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onMessage(MessageWrapper<CouponTemplateDistributionEvent> messageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] 优惠券任务执行推送@分发到用户账号 - 执行消费逻辑，消息体：{}", JSON.toJSONString(messageWrapper));

        // 当保存用户优惠券集合达到批量保存数量
        CouponTemplateDistributionEvent event = messageWrapper.getMessage();
        if (!event.getDistributionEndFlag() && event.getBatchUserSetSize() % BATCH_USER_COUPON_SIZE == 0) {
            decrementCouponTemplateStockAndSaveUserCouponList(event);
            return;
        }

    }
}
