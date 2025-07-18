package cn.hush.Coupra.merchant.admin.service.basics.chain;

import org.springframework.core.Ordered;

public interface MerchantAdminAbstractChainHandler<T> extends Ordered {

    /**
     * 执行责任链逻辑
     *
     * @param requestParam 责任链执行入参
     */
    void handler(T requestParam);

    /**
     * @return 责任链组件标识
     */
    String mark();

}
