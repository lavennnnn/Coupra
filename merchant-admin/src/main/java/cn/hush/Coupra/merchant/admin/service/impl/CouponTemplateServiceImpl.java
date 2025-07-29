package cn.hush.Coupra.merchant.admin.service.impl;


import cn.hush.Coupra.merchant.admin.common.constant.MerchantAdminRedisConstant;
import cn.hush.Coupra.merchant.admin.common.context.UserContext;
import cn.hush.Coupra.merchant.admin.common.enums.CouponTemplateStatusEnum;
import cn.hush.Coupra.merchant.admin.dao.entity.CouponTemplateDO;
import cn.hush.Coupra.merchant.admin.dao.mapper.CouponTemplateMapper;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplateNumberReqDTO;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplatePageQueryReqDTO;
import cn.hush.Coupra.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import cn.hush.Coupra.merchant.admin.dto.resp.CouponTemplatePageQueryRespDTO;
import cn.hush.Coupra.merchant.admin.dto.resp.CouponTemplateQueryRespDTO;
import cn.hush.Coupra.merchant.admin.service.CouponTemplateService;
import cn.hush.Coupra.merchant.admin.service.basics.chain.MerchantAdminChainContext;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
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

    @LogRecord(
            success = """
                    创建优惠券：{{#requestParam.name}}， \
                    优惠对象：{COMMON_ENUM_PARSE{'DiscountTargetEnum' + '_' + #requestParam.target}}， \
                    优惠类型：{COMMON_ENUM_PARSE{'DiscountTypeEnum' + '_' + #requestParam.type}}， \
                    库存数量：{{#requestParam.stock}}， \
                    优惠商品编码：{{#requestParam.goods}}， \
                    有效期开始时间：{{#requestParam.validStartTime}}， \
                    有效期结束时间：{{#requestParam.validEndTime}}， \
                    领取规则：{{#requestParam.receiveRule}}， \
                    消耗规则：{{#requestParam.consumeRule}};
                    """,
            type = "CouponTemplate",
            bizNo = "{{#bizNo}}",
            extra = "{{#requestParam.toString()}}"
    )
    @Override
    public void createCouponTemplate(CouponTemplateSaveReqDTO requestParam) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 通过责任链验证请求参数是否正确
        merchantAdminChainContext.handler(MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY.name(), requestParam);
        // 新增优惠券模板信息到数据库
        CouponTemplateDO couponTemplateDO = BeanUtil.toBean(requestParam, CouponTemplateDO.class);
        couponTemplateDO.setStatus(CouponTemplateStatusEnum.ACTIVE.getStatus());
        couponTemplateDO.setShopNumber(UserContext.getShopNumber());
        couponTemplateMapper.insert(couponTemplateDO);

        // 因为模板 ID 是运行中生成的，@LogRecord 默认拿不到，所以我们需要手动设置
        LogRecordContext.putVariable("bizNo", couponTemplateDO.getId());

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

    @Override
    public IService<CouponTemplatePageQueryRespDTO> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam) {
        //构建分页查询模板


    }

    @Override
    public CouponTemplateQueryRespDTO findCouponTemplateById(String couponTemplateId) {
        return null;
    }

    @Override
    public void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam) {

    }

    @Override
    public void terminateCouponTemplate(String couponTemplateId) {

    }


}
