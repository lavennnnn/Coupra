package cn.hush.Coupra.merchant.admin.mq.consumer;



import cn.hush.Coupra.merchant.admin.common.enums.CouponTemplateStatusEnum;
import cn.hush.Coupra.merchant.admin.dao.entity.CouponTemplateDO;
import cn.hush.Coupra.merchant.admin.mq.base.MessageWrapper;
import cn.hush.Coupra.merchant.admin.mq.event.CouponTemplateDelayEvent;
import cn.hush.Coupra.merchant.admin.service.CouponTemplateService;
import com.alibaba.fastjson.JSON;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @program: Coupra
 * @description: 优惠券推送延迟执行-变更记录发送状态消费者
 * @author: Hush
 * @create: 2025-08-05 01:47
 **/
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "Coupra_merchant-admin-service_coupon-template-delay_topic${unique-name:}",
        consumerGroup = "Coupra_merchant-admin-service_coupon-template-delay-status_cg${unique-name:}"
)
@Slf4j(topic = "CouponTemplateDelayExecuteStatusConsumer")
public class CouponTemplateDelayExecuteStatusConsumer implements RocketMQListener<MessageWrapper<CouponTemplateDelayEvent>> {

    private final CouponTemplateService couponTemplateService;

    @Override
    public void onMessage(MessageWrapper<CouponTemplateDelayEvent> messageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] 优惠券模板定时执行@变更模板表状态 - 执行消费逻辑，消息体：{}", JSON.toJSONString(messageWrapper));

        // 修改指定优惠券模板状态为已结束
        CouponTemplateDelayEvent message = messageWrapper.getMessage();
        UpdateChain.of(CouponTemplateDO.class)
                .set(CouponTemplateDO::getStatus, CouponTemplateStatusEnum.ENDED.getStatus())
                .where(CouponTemplateDO::getShopNumber).eq(message.getShopNumber())
                .and(CouponTemplateDO::getId).eq(message.getCouponTemplateId())
                .update();
    }


}

