package cn.hush.Coupra.merchant.admin.controller;


import cn.hush.Coupra.framework.idempotent.NoDuplicateSubmit;
import cn.hush.Coupra.framework.result.Result;
import cn.hush.Coupra.framework.web.Results;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTaskCreateReqDTO;
import cn.hush.Coupra.merchant.admin.service.CouponTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @program: Coupra
 * @description: 优惠券推送任务控制层
 * @author: Hush
 * @create: 2025-08-06 01:54
 **/
@RestController
@RequiredArgsConstructor
@Tag(name = "优惠券推送任务管理")
@RequestMapping("/api/merchant-admin/coupon-task/")
public class CouponTaskController {

    private final CouponTaskService couponTaskService;

    @Operation(summary = "创建优惠券推送任务")
    @NoDuplicateSubmit(message = "请勿短时间内重复提交优惠券推送任务")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public Result<Void> createCouponTask(@RequestBody CouponTaskCreateReqDTO requestParam) {
        couponTaskService.createCouponTask(requestParam);
        return Results.success();
    }
}