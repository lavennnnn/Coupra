package cn.hush.Coupra.merchant.admin.controller;


import cn.hush.Coupra.framework.idempotent.NoDuplicateSubmit;
import cn.hush.Coupra.framework.result.Result;

import cn.hush.Coupra.framework.web.Results;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplateNumberReqDTO;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplatePageQueryReqDTO;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import cn.hush.Coupra.merchant.admin.dto.resp.CouponTemplatePageQueryRespDTO;
import cn.hush.Coupra.merchant.admin.dto.resp.CouponTemplateQueryRespDTO;
import cn.hush.Coupra.merchant.admin.service.CouponTemplateService;
import com.mybatisflex.core.service.IService;
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

    @NoDuplicateSubmit
    @Operation(summary = "商家创建优惠券模板")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public Result<Void> createCouponTemplate(@RequestBody CouponTemplateSaveReqDTO requestParam) {
        couponTemplateService.createCouponTemplate(requestParam);
        return Results.success();
    }

    @Operation(summary = "分页查询优惠券模板")
    @RequestMapping(value = "page", method = RequestMethod.GET)
    public Result<IService<CouponTemplatePageQueryRespDTO>> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam) {
        return Results.success(couponTemplateService.pageQueryCouponTemplate(requestParam));
    }

    @Operation(summary = "查询优惠券模板详情")
    @RequestMapping(value = "find", method = RequestMethod.GET)
    public Result<CouponTemplateQueryRespDTO> findCouponTemplate(String couponTemplateId) {
        return Results.success(couponTemplateService.findCouponTemplateById(couponTemplateId));
    }

    @Operation(summary = "增加优惠券模板发行量")
    @NoDuplicateSubmit(message = "请勿短时间内重复增加优惠券发行量")
    @RequestMapping(value = "increase-number", method = RequestMethod.POST)
    public Result<Void> increaseNumberCouponTemplate(@RequestBody CouponTemplateNumberReqDTO requestParam) {
        couponTemplateService.increaseNumberCouponTemplate(requestParam);
        return Results.success();
    }

    @Operation(summary = "结束优惠券模板")
    @RequestMapping(value = "terminate", method = RequestMethod.POST)
    public Result<Void> terminateCouponTemplate(String couponTemplateId) {
        couponTemplateService.terminateCouponTemplate(couponTemplateId);
        return Results.success();
    }


}
