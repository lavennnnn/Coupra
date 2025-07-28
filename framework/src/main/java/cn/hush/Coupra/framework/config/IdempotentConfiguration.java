package cn.hush.Coupra.framework.config;

import cn.hush.Coupra.framework.idempotent.NoDuplicateSubmitAspect;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 幂等组件相关配置类,Starter 组件库里不能通过类上加注解成为 Bean，我们创建幂等自动装配类。
 */
public class IdempotentConfiguration {

    /**
     * 防止用户重复提交表单信息切面控制器
     */
    @Bean
    public NoDuplicateSubmitAspect noDuplicateSubmitAspect(RedissonClient redissonClient) {
        return new NoDuplicateSubmitAspect(redissonClient);
    }

}
