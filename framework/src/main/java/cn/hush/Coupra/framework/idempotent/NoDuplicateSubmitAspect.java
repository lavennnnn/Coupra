package cn.hush.Coupra.framework.idempotent;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RedissonClient;

/**
 * 防止用户重复提交表单信息切面控制器
 */
@Aspect
@RequiredArgsConstructor
public class NoDuplicateSubmitAspect {

    private final RedissonClient redissonClient;

    /**
     * 增强方法标记 {@link NoDuplicateSubmit} 注解逻辑
     */


}
