package cn.hush.Coupra.merchant.admin.service;


import cn.hush.Coupra.merchant.admin.dao.entity.CouponTemplateDO;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import com.mybatisflex.core.service.IService;

/**
 * @program: Coupra
 * @description: 优惠券模板业务逻辑层
 * @author: Hush
 * @create: 2025-07-18 00:25
 **/

public interface CouponTemplateService extends IService<CouponTemplateDO> {

    /**
     * 创建商家优惠券模板
     *
     * @param requestParam 请求参数
     */
    void createCouponTemplate(CouponTemplateSaveReqDTO requestParam);
}
