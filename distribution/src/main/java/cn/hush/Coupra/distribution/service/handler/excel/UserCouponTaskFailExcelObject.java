package cn.hush.Coupra.distribution.service.handler.excel;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: Coupra
 * @description: 用户优惠券分发失败记录写入 Excel
 * @author: Hush
 * @create: 2025-09-10 19:39
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCouponTaskFailExcelObject {

    @ColumnWidth(20)
    @ExcelProperty("行数")
    private Integer rowNum;

    @ColumnWidth(30)
    @ExcelProperty("错误原因")
    private String cause;
}
