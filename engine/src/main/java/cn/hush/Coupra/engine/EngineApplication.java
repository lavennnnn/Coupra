package cn.hush.Coupra.engine;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: Coupra
 * @description:
 * @author: Hush
 * @create: 2025-06-27 00:28
 **/
@SpringBootApplication
@MapperScan("cn.hush.Coupra.engine.dao.mapper")
public class EngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(EngineApplication.class, args);
    }

}
