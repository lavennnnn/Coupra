package cn.hush.Coupra.merchant.admin.service.impl;


import cn.hush.Coupra.framework.exception.ClientException;
import cn.hush.Coupra.framework.exception.ServiceException;
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
import cn.hush.Coupra.merchant.admin.mq.event.CouponTemplateDelayEvent;
import cn.hush.Coupra.merchant.admin.mq.producer.CouponTemplateDelayExecuteStatusProducer;
import cn.hush.Coupra.merchant.admin.service.CouponTemplateService;
import cn.hush.Coupra.merchant.admin.service.basics.chain.MerchantAdminChainContext;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
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
    private final CouponTemplateDelayExecuteStatusProducer couponTemplateDelayExecuteStatusProducer;

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

        // 发送延时消息事件，优惠券活动到期修改优惠券模板状态
        CouponTemplateDelayEvent templateDelayEvent = CouponTemplateDelayEvent.builder()
                .shopNumber(UserContext.getShopNumber())
                .couponTemplateId(couponTemplateDO.getId())
                .delayTime(couponTemplateDO.getValidEndTime().getTime())
                .build();

        couponTemplateDelayExecuteStatusProducer.sendMessage(templateDelayEvent);

    }

    @Override
    public Page<CouponTemplatePageQueryRespDTO> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam) {
        //构建分页查询模板
        QueryWrapper wrapper = QueryWrapper.create()
                .eq(CouponTemplateDO::getShopNumber, UserContext.getShopNumber())
                .like(CouponTemplateDO::getName, requestParam.getName(), StrUtil.isNotBlank(requestParam.getName()))
                .like(CouponTemplateDO::getGoods, requestParam.getGoods(), StrUtil.isNotBlank(requestParam.getGoods()))
                .eq(CouponTemplateDO::getType, requestParam.getType(), Objects.nonNull(requestParam.getType()));

        Page<CouponTemplateDO> pages = couponTemplateMapper.paginate(requestParam, wrapper);

        List<CouponTemplatePageQueryRespDTO> list = pages.getRecords()
                .stream()
                .map(each -> BeanUtil.toBean(each, CouponTemplatePageQueryRespDTO.class))
                .toList();

        Page<CouponTemplatePageQueryRespDTO> finalPage = new Page<>();
        finalPage.setRecords(list);
        return finalPage;
    }

    @Override
    public CouponTemplateQueryRespDTO findCouponTemplateById(String couponTemplateId) {
        return null;
    }

    @LogRecord(
            success = "增加发行量：{{#requestParam.number}}",
            type = "CouponTemplate",
            bizNo = "{{#requestParam.couponTemplateId}}"
    )
    @Override
    public void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam) {

        // 验证是否存在数据横向越权
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(CouponTemplateDO::getShopNumber, UserContext.getShopNumber())
                .eq(CouponTemplateDO::getId, requestParam.getCouponTemplateId());
        CouponTemplateDO couponTemplateDO = couponTemplateMapper.selectOneByQuery(queryWrapper);
        if (couponTemplateDO == null) {
            // 一旦查询优惠券不存在，基本可判定横向越权，可上报该异常行为，次数多了后执行封号等处理
            throw new ClientException("优惠券模板异常，请检查操作是否正确...");
        }

        // 验证优惠券模板是否正常
        if (ObjectUtil.notEqual(couponTemplateDO.getStatus(), CouponTemplateStatusEnum.ACTIVE.getStatus())) {
            throw new ClientException("优惠券模板已结束");
        }

        // 记录优惠券模板修改前数据
        LogRecordContext.putVariable("originalData", JSON.toJSONString(couponTemplateDO));

        // 设置数据库优惠券模板增加库存发行量
        int increased = couponTemplateMapper.increaseNumberCouponTemplate(UserContext.getShopNumber(), requestParam.getCouponTemplateId(), requestParam.getNumber());
        if (increased <= 0) {
            throw new ServiceException("优惠券模板增加发行量失败");
        }

        // 增加优惠券模板缓存库存发行量
        String couponTemplateCacheKey = String.format(MerchantAdminRedisConstant.COUPON_TEMPLATE_KEY, requestParam.getCouponTemplateId());
        stringRedisTemplate.opsForHash().increment(couponTemplateCacheKey, "stock", requestParam.getNumber());

    }

    @LogRecord(
            success = "结束优惠券",
            type = "CouponTemplate",
            bizNo = "{{#couponTemplateId}}"
    )
    @Override
    public void terminateCouponTemplate(String couponTemplateId) {

        //验证是否存在数据横向越权
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(CouponTemplateDO::getShopNumber, UserContext.getShopNumber())
                .eq(CouponTemplateDO::getId, couponTemplateId);

        CouponTemplateDO couponTemplateDO = couponTemplateMapper.selectOneByQuery(queryWrapper);

        if (couponTemplateDO == null) {
            // 一旦查询优惠券不存在，基本可判定横向越权，可上报该异常行为，次数多了后执行封号等处理
            throw new ClientException("优惠券模板异常，请检查操作是否正确...");
        }

        // 验证优惠券模板是否正常
        if (ObjectUtil.notEqual(couponTemplateDO.getStatus(), CouponTemplateStatusEnum.ACTIVE.getStatus())) {
            throw new ClientException("优惠券模板已结束");
        }

        // 修改优惠券模板为结束状态
        CouponTemplateDO updateCouponTemplateDO = CouponTemplateDO.builder()
                .status(CouponTemplateStatusEnum.ENDED.getStatus())
                .build();

        QueryWrapper updateWrapper = QueryWrapper.create()
                .eq(CouponTemplateDO::getId, couponTemplateDO.getId())
                .eq(CouponTemplateDO::getShopNumber, UserContext.getShopNumber());

        couponTemplateMapper.updateByQuery(updateCouponTemplateDO, updateWrapper);

    }


}
