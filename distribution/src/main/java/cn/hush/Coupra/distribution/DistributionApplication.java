package cn.hush.Coupra.distribution;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: Coupra
 * @description:
 * @author: Hush
 * @create: 2025-06-27 00:26
 **/
@SpringBootApplication
@MapperScan("cn.hush.Coupra.distribution.dao.mapper")
public class DistributionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributionApplication.class, args);
    }

}
