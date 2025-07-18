package cn.hush.Coupra.settlement;


/**
 * @program: Coupra
 * @description: 结算服务｜负责用户下单时订单金额计算功能，因和订单相关联，该服务流量较大
 * @author: Hush
 * @create: 2025-07-18 16:28
 **/

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SettlementApplication {
    public static void main(String[] args) {
        SpringApplication.run(SettlementApplication.class, args);
    }
}
