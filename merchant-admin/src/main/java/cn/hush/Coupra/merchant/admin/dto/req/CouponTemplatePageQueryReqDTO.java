package cn.hush.Coupra.merchant.admin.dto.req;

import cn.hush.Coupra.merchant.admin.dao.entity.CouponTemplateDO;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 优惠券模板分页查询接口请求参数实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "优惠券模板分页查询参数")
public class CouponTemplatePageQueryReqDTO extends Page<CouponTemplateDO> {

    /**
     * 优惠券名称
     */
    @Schema(description = "优惠券名称")
    private String name;

    /**
     * 优惠对象 0：商品专属 1：全店通用
     */
    @Schema(description = "优惠对象 0：商品专属 1：全店通用")
    private Integer target;

    /**
     * 优惠商品编码
     */
    @Schema(description = "优惠商品编码")
    private String goods;

    /**
     * 优惠类型 0：立减券 1：满减券 2：折扣券
     */
    @Schema(description = "优惠类型 0：立减券 1：满减券 2：折扣券")
    private Integer type;


}
