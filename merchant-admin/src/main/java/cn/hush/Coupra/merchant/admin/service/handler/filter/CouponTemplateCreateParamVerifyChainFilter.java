package cn.hush.Coupra.merchant.admin.service.handler.filter;


import cn.hush.Coupra.merchant.admin.common.enums.DiscountTargetEnum;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import cn.hush.Coupra.merchant.admin.service.basics.chain.MerchantAdminAbstractChainHandler;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Component;

import static cn.hush.Coupra.merchant.admin.common.enums.ChainBizMarkNum.MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY;

/**
 * @program: Coupra
 * @description: 验证优惠券创建接口参数是否正确责任链｜验证参数数据是否正确
 * @author: Hush
 * @create: 2025-07-18 00:21
 **/
@Component
public class CouponTemplateCreateParamVerifyChainFilter implements MerchantAdminAbstractChainHandler<CouponTemplateSaveReqDTO> {


    @Override
    public void handler(CouponTemplateSaveReqDTO requestParam) {
        if (ObjectUtil.equal(requestParam.getTarget(), DiscountTargetEnum.PRODUCT_SPECIFIC)) {
            // 调用商品中台验证商品是否存在，如果不存在抛出异常
            // ......
        }
    }

    @Override
    public String mark() {
        return MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY.name();
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
