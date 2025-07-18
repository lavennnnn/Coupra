package cn.hush.Coupra.merchant.admin.controller;


import cn.hush.Coupra.framework.result.Result;

import cn.hush.Coupra.framework.web.Results;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import cn.hush.Coupra.merchant.admin.service.CouponTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: Coupra
 * @description: 优惠券模板控制层
 * @author: Hush
 * @create: 2025-07-17 01:19
 **/
@RestController
@RequiredArgsConstructor
@Tag(name = "优惠券模板管理")
@RequestMapping("/api/merchant-admin/coupon-template")
public class CouponTemplateController {

    private final CouponTemplateService couponTemplateService;

    @Operation(summary = "商家创建优惠券模板")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public Result<Void> createCouponTemplate(@RequestBody CouponTemplateSaveReqDTO requestParam) {
        couponTemplateService.createCouponTemplate(requestParam);
        return Results.success();
    }

}
