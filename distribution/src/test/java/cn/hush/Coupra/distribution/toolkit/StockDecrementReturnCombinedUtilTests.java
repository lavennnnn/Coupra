package java.cn.hush.Coupra.distribution.toolkit;


import cn.hush.Coupra.distribution.toolkit.StockDecrementReturnCombinedUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @program: Coupra
 * @description: 测试位移和字符串 split 速度，位移完胜
 * @author: Hush
 * @create: 2025-09-10 19:58
 **/
@Slf4j
public final class StockDecrementReturnCombinedUtilTests {

    @Test
    public void stockDecrementReturnCombinedUtilTest() {
        boolean firstField = true;
        int secondField = 5000;

        int combined = StockDecrementReturnCombinedUtil.combineFields(firstField, secondField);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            StockDecrementReturnCombinedUtil.extractFirstField(combined);
            StockDecrementReturnCombinedUtil.extractSecondField(combined);
        }
        long endTime = System.currentTimeMillis();


        long startTime2 = System.currentTimeMillis();
        String str = "1,1234";
        for (int i = 0; i < 100000; i++) {
            StrUtil.split(str, ",");
        }
        long endTime2 = System.currentTimeMillis();

        log.info("位移程序执行时间：{}", endTime - startTime);
        log.info("split程序执行时间：{}", endTime2 - startTime2);
        /**
         * 位移程序执行时间：2
         * split程序执行时间：40
         */
    }
}
