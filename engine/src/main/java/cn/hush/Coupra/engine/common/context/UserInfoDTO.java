package cn.hush.Coupra.engine.common.context;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: Coupra
 * @description: 登录用户信息实体
 * @author: Hush
 * @create: 2025-08-21 01:30
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 店铺编号
     */
    private Long shopNumber;

}
