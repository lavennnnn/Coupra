package cn.hush.Coupra.framework.config;


import cn.hush.Coupra.framework.web.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;

/**
 * @program: Coupra
 * @description: Web 组件自动装配
 * @author: Hush
 * @create: 2025-06-27 00:35
 **/

public class WebAutoConfiguration {

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

}
