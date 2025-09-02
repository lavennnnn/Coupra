package cn.hush.Coupra.distribution.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @program: Coupra
 * @description: 优惠券推送任务状态枚举
 * @author: Hush
 * @create: 2025-08-27 22:31
 **/
@RequiredArgsConstructor
public enum CouponTaskStatusEnum {

    /**
     * 待执行
     */
    PENDING(0),

    /**
     * 执行中
     */
    IN_PROGRESS(1),

    /**
     * 执行失败
     */
    FAILED(2),

    /**
     * 执行成功
     */
    SUCCESS(3),

    /**
     * 取消
     */
    CANCEL(4);

    @Getter
    private final int status;
}

