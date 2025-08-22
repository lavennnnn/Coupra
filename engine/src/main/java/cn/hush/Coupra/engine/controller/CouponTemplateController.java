package cn.hush.Coupra.engine.controller;


import cn.hush.Coupra.engine.dto.req.CouponTemplateQueryReqDTO;
import cn.hush.Coupra.engine.dto.resp.CouponTemplateQueryRespDTO;
import cn.hush.Coupra.engine.service.CouponTemplateService;
import cn.hush.Coupra.framework.result.Result;
import cn.hush.Coupra.framework.web.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: Coupra
 * @description: 优惠券模板控制层
 * @author: Hush
 * @create: 2025-08-21 01:40
 **/
@RestController
@RequiredArgsConstructor
@Tag(name = "优惠券模板管理")
public class CouponTemplateController {

    private final CouponTemplateService couponTemplateService;

    @Operation(summary = "查询优惠券模板")
    @GetMapping("/api/engine/coupon-template/query")
    public Result<CouponTemplateQueryRespDTO> findCouponTemplate(CouponTemplateQueryReqDTO requestParam) {
        return Results.success(couponTemplateService.findCouponTemplate(requestParam));
    }
}

