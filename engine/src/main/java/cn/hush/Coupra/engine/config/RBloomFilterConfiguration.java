package cn.hush.Coupra.engine.config;


import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: Coupra
 * @description: 布隆过滤器配置类
 * @author: Hush
 * @create: 2025-08-27 01:06
 **/
@Configuration
public class RBloomFilterConfiguration {

    @Bean
    public RBloomFilter<String> couponTemplateQueryBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("couponTemplateQueryBloomFilter");
        bloomFilter.tryInit(640L, 0.001);
        return bloomFilter;

    }

}
