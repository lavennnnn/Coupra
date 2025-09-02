package cn.hush.Coupra.distribution.service.handler.excel;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @program: Coupra
 * @description: 优惠券推送任务 Excel 元数据实体
 * @author: Hush
 * @create: 2025-08-27 23:05
 **/
@Data
public class CouponTaskExcelObject {

    @ExcelProperty("用户ID")
    private String userId;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("邮箱")
    private String mail;

}
