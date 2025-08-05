package cn.hush.Coupra.merchant.admin.service;

import cn.hush.Coupra.merchant.admin.dto.req.CouponTaskCreateReqDTO;

/**
 * 优惠券推送业务逻辑层
 */
public interface CouponTaskService {

    /**
    * 商家创建优惠券推送任务
    *
    * @param requestParam 请求参数
    */
    void createCouponTask(CouponTaskCreateReqDTO requestParam);
}
