package cn.hush.Coupra.merchant.admin;


import com.mzt.logapi.starter.annotation.EnableLogRecord;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: Coupra
 * @description: 启动类
 * @author: Hush
 * @create: 2025-06-26 23:46
 **/
@SpringBootApplication
@MapperScan("cn.hush.Coupra.merchant.admin.dao.mapper")
@EnableLogRecord(tenant = "MerchantAdmin")
public class MerchantAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantAdminApplication.class, args);
    }

}
