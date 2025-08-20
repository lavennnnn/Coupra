package cn.hush.Coupra.engine.common.constant;


/**
 * @program: Coupra
 * @description: 分布式 Redis 缓存引擎层常量类
 * @author: Hush
 * @create: 2025-08-21 01:26
 **/

public final class EngineRedisConstant {

    /**
     * 优惠券模板缓存 Key
     */
    public static final String COUPON_TEMPLATE_KEY = "Coupra_engine:template:%s";

    /**
     * 优惠券模板缓存分布式锁 Key
     */
    public static final String LOCK_COUPON_TEMPLATE_KEY = "Coupra_engine:lock:template:%s";


}
