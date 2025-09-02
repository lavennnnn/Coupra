package cn.hush.Coupra.distribution.dao.entity;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: Coupra
 * @description: 优惠券模板操作日志数据库持久层实体
 * @author: Hush
 * @create: 2025-07-26 19:14
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("t_user_coupon")
public class UserCouponDO {

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 优惠券模板id
     */
    private Long couponTemplateId;

    /**
     * 领取时间
     */
    private Date receiveTime;

    /**
     * 领取次数
     */
    private Integer receiveCount;

    /**
     * 有效期开始时间
     */
    private Date validStartTime;

    /**
     * 有效期结束时间
     */
    private Date validEndTime;

    /**
     * 使用时间
     */
    private Date useTime;

    /**
     * 券来源 0：领券中心 1：平台发放 2：店铺领取
     */
    private Integer source;

    /**
     * 状态 0：未使用 1：锁定 2：已使用 3：已过期 4：已撤回
     */
    private Integer status;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(onUpdateValue = "now()", onInsertValue = "now()")
    private Date updateTime;

    /**
     * 删除标识 0：未删除 1：已删除
     */
    @Column(onInsertValue = "0")
    private Integer delFlag;
}