package cn.hush.Coupra.engine.service;


import cn.hush.Coupra.engine.dao.CouponTemplateDO;
import cn.hush.Coupra.engine.dto.req.CouponTemplateQueryReqDTO;
import cn.hush.Coupra.engine.dto.resp.CouponTemplateQueryRespDTO;
import com.mybatisflex.core.service.IService;

/**
 * @program: Coupra
 * @description: 优惠券模板业务逻辑层
 * @author: Hush
 * @create: 2025-08-21 01:52
 **/

public interface CouponTemplateService extends IService<CouponTemplateDO> {

    /**
     * 查询优惠券模板
     *
     * @param requestParam 请求参数
     * @return 优惠券模板信息
     */
    CouponTemplateQueryRespDTO findCouponTemplate(CouponTemplateQueryReqDTO requestParam);
}
