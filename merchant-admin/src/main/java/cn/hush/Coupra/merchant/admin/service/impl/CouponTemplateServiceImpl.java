package cn.hush.Coupra.merchant.admin.service.impl;


import cn.hush.Coupra.merchant.admin.common.constant.MerchantAdminRedisConstant;
import cn.hush.Coupra.merchant.admin.common.context.UserContext;
import cn.hush.Coupra.merchant.admin.common.enums.CouponTemplateStatusEnum;
import cn.hush.Coupra.merchant.admin.dao.entity.CouponTemplateDO;
import cn.hush.Coupra.merchant.admin.dao.mapper.CouponTemplateMapper;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import cn.hush.Coupra.merchant.admin.dto.resp.CouponTemplateQueryRespDTO;
import cn.hush.Coupra.merchant.admin.service.CouponTemplateService;
import cn.hush.Coupra.merchant.admin.service.basics.chain.MerchantAdminChainContext;
import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hush.Coupra.merchant.admin.common.enums.ChainBizMarkNum.MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY;

/**
 * @program: Coupra
 * @description: 优惠券模板业务逻辑实现层
 * @author: Hush
 * @create: 2025-07-18 00:25
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponTemplateServiceImpl extends ServiceImpl<CouponTemplateMapper, CouponTemplateDO> implements CouponTemplateService {

    private final CouponTemplateMapper couponTemplateMapper;
    private final MerchantAdminChainContext merchantAdminChainContext;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void createCouponTemplate(CouponTemplateSaveReqDTO requestParam) {
        // 通过责任链验证请求参数是否正确
        merchantAdminChainContext.handler(MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY.name(), requestParam);
        // 新增优惠券模板信息到数据库
        CouponTemplateDO couponTemplateDO = BeanUtil.toBean(requestParam, CouponTemplateDO.class);
        couponTemplateDO.setStatus(CouponTemplateStatusEnum.ACTIVE.getStatus());
        couponTemplateDO.setShopNumber(UserContext.getShopNumber());
        couponTemplateMapper.insert(couponTemplateDO);

        // 缓存预热：通过将数据库的记录序列化成 JSON 字符串放入 Redis 缓存
        CouponTemplateQueryRespDTO actualRespDTO = BeanUtil.
                toBean(couponTemplateDO, CouponTemplateQueryRespDTO.class);

        Map<String, Object> cacheTargetMap = BeanUtil.
                beanToMap(actualRespDTO, false, true);

        Map<String, String> actualCacheTargetMap = cacheTargetMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, entry -> entry.getValue() != null ? entry.
                                getValue().toString() : ""));

        String couponTemplateCacheKey = String.
                format(MerchantAdminRedisConstant.COUPON_TEMPLATE_KEY, couponTemplateDO.getId());

        // 通过 LUA 脚本执行设置 Hash 数据以及设置过期时间
        String luaScript = "redis.call('HMSET', KEYS[1], unpack(ARGV, 1, #ARGV - 1)) " +
                "redis.call('EXPIREAT', KEYS[1], ARGV[#ARGV])";

        List<String> keys = Collections.singletonList(couponTemplateCacheKey);

        List<String> args = new ArrayList<>(actualCacheTargetMap.size() * 2 + 1);

        actualCacheTargetMap.forEach((key, value) -> {
            args.add(key);
            args.add(value);
        });

        // 优惠券活动过期时间转换为秒级别的 Unix 时间戳
        args.add(String.valueOf(couponTemplateDO.getValidEndTime().getTime() / 1000));

        // ✅ 打印日志
        log.info("[缓存预热] Redis Key: {}", couponTemplateCacheKey);
        log.info("[缓存预热] Redis ARGV 参数: {}", args);
        log.info("[缓存预热] 执行 Lua 脚本: {}", luaScript);

        // 执行 LUA 脚本
        try {
            Long result = stringRedisTemplate.execute(
                    new DefaultRedisScript<>(luaScript, Long.class),
                    keys,
                    args.toArray()
            );
            // ✅ 打印执行结果
            log.info("[缓存预热] Lua 脚本执行结果: {}", result);
        } catch (Exception e) {
            log.error("[缓存预热] Redis 缓存设置失败！", e);
        }

    }


}
