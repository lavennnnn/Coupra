package cn.hush.Coupra.merchant.admin.service.handler.excel;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Getter;

/**
 * @program: Coupra
 * @description: Excel 行数统计监听器
 * @author: Hush
 * @create: 2025-08-06 02:02
 **/

public class RowCountListener extends AnalysisEventListener<Object> {


    @Getter
    private int rowCount = 0;

    @Override
    public void invoke(Object data, AnalysisContext context) {
        rowCount++;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // No additional actions needed after all data is analyzed
    }

}
