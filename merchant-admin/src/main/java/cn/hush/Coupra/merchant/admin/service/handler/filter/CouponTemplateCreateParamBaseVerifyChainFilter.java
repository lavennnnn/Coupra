package cn.hush.Coupra.merchant.admin.service.handler.filter;


import cn.hush.Coupra.framework.exception.ClientException;
import cn.hush.Coupra.merchant.admin.common.enums.DiscountTargetEnum;
import cn.hush.Coupra.merchant.admin.common.enums.DiscountTypeEnum;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import cn.hush.Coupra.merchant.admin.service.basics.chain.MerchantAdminAbstractChainHandler;
import com.alibaba.fastjson2.JSON;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

import static cn.hush.Coupra.merchant.admin.common.enums.ChainBizMarkNum.MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY;

/**
 * @program: Coupra
 * @description: 验证优惠券创建接口参数是否正确责任链｜验证参数基本数据关系是否正确
 * @author: Hush
 * @create: 2025-07-17 23:27
 **/
@Component
public class CouponTemplateCreateParamBaseVerifyChainFilter implements MerchantAdminAbstractChainHandler<CouponTemplateSaveReqDTO> {

    private final int maxStock = 20000000;

    @Override
    public void handler(CouponTemplateSaveReqDTO requestParam) {
        //判断传参的target是否是合法的优惠对象，否的话报错
        boolean targetAnyMatch = Arrays.stream(DiscountTargetEnum.values())
                .anyMatch(enumConstant -> enumConstant.getType() == requestParam.getTarget());
        if (!targetAnyMatch) {
            throw new ClientException("优惠对象值不存在");
        }
        Integer target = requestParam.getTarget();
        String goods = requestParam.getGoods();
        //如果优惠对象是全店通用优惠且参数中的商品编码不为空，则指明优惠券全店通用不可设置指定商品
        if (target == DiscountTargetEnum.ALL_STORE_GENERAL.getType() && !StringUtils.isEmpty(goods)) {
            throw new ClientException("优惠券全店通用不可设置指定商品");
        }
        //如果优惠对象是商品专属优惠且参数中的商品编码为空，则指明优惠券商品专属优惠未设置指定商品
        if (target == DiscountTargetEnum.PRODUCT_SPECIFIC.getType() && StringUtils.isEmpty(goods)) {
            throw new ClientException("优惠券商品专属优惠未设置指定商品");
        }
        //判断传参的type是否是合法的优惠类型
        boolean typeAnyMatch = Arrays.stream(DiscountTypeEnum.values())
                .anyMatch(enumConstant -> enumConstant.getType() == requestParam.getType());
        if (!typeAnyMatch) {
            throw new ClientException("优惠券优惠类型不存在");
        }
        //判断传参的优惠券有效期开始时间是否先于当前时间，是的话报错
        Date now = new Date();
        if (requestParam.getValidStartTime().before(now)) {
            //throw new ClientException("有效期开始时间不能早于当前时间")
        }
        //判断传参的库存是否小于0或者大于最大库存，是的话报错
        if (0 > requestParam.getStock() || requestParam.getStock() > maxStock) {
            throw new ClientException("库存数量设置异常");
        }
        //判断传参的领取规则是否是合法的JSON，否的话报错
        if (!JSON.isValid(requestParam.getReceiveRule())) {
            throw new ClientException("领取规则格式错误");
        }
        //判断传参的消耗规则是否是合法的JSON，否的话报错
        if (!JSON.isValid(requestParam.getConsumeRule())) {
            // 此处已经基本能判断数据请求属于恶意攻击，可以上报风控中心进行封禁账号
            throw new ClientException("消耗规则格式错误");
        }
    }

    @Override
    public String mark() {
        return MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY.name();
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
