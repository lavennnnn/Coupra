package cn.hush.Coupra.distribution.dao.mapper;

import cn.hush.Coupra.distribution.dao.entity.CouponTemplateDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 优惠券模板数据库持久层
 */
public interface CouponTemplateMapper extends BaseMapper<CouponTemplateDO> {

    /**
     * 自减优惠券模板库存
     *
     * @param couponTemplateId 优惠券模板 ID
     * @return 是否发生记录变更
     */
    int decrementCouponTemplateStock(@Param("shopNumber") Long shopNumber, @Param("couponTemplateId") Long couponTemplateId, @Param("decrementStock") Integer decrementStock);

}
