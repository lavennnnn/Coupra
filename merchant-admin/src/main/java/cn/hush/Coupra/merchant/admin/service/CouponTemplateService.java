package cn.hush.Coupra.merchant.admin.service;


import cn.hush.Coupra.merchant.admin.dao.entity.CouponTemplateDO;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplateNumberReqDTO;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplatePageQueryReqDTO;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import cn.hush.Coupra.merchant.admin.dto.resp.CouponTemplatePageQueryRespDTO;
import cn.hush.Coupra.merchant.admin.dto.resp.CouponTemplateQueryRespDTO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

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

    /**
     * 分页查询商家优惠券模板
     *
     * @param requestParam 请求参数
     * @return 商家优惠券模板分页数据
     */
    Page<CouponTemplatePageQueryRespDTO> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam);

    /**
     * 查询优惠券模板详情
     * 后管接口并不存在并发，直接查询数据库即可
     *
     * @param couponTemplateId 优惠券模板 ID
     * @return 优惠券模板详情
     */
    CouponTemplateQueryRespDTO findCouponTemplateById(String couponTemplateId);

    /**
     * 增加优惠券模板发行量
     *
     * @param requestParam 请求参数
     */

    void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam);

    /**
     * 结束优惠券模板
     *
     * @param couponTemplateId 优惠券模板 ID
     */
    void terminateCouponTemplate(String couponTemplateId);
}
