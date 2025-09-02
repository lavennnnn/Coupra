package cn.hush.Coupra.distribution.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @program: Coupra
 * @description: 优惠券使用状态枚举类
 * @author: Hush
 * @create: 2025-08-27 22:30
 **/

@RequiredArgsConstructor
public enum CouponStatusEnum {

    /**
     * 生效中
     */
    EFFECTIVE(0),

    /**
     * 已结束
     */
    ENDED(1);

    @Getter
    private final int type;
}

