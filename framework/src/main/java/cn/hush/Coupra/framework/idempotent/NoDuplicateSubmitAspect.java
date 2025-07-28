package cn.hush.Coupra.framework.idempotent;

import cn.hush.Coupra.framework.exception.ClientException;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 防止用户重复提交表单信息切面控制器
 */
@Aspect
@RequiredArgsConstructor
public final class NoDuplicateSubmitAspect {

    private final RedissonClient redissonClient;

    /**
     * 增强方法标记 {@link NoDuplicateSubmit} 注解逻辑
     */
    @Around("@annotation(cn.hush.Coupra.framework.idempotent.NoDuplicateSubmit)")
    public Object NoDuplicateSubmit(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取自定义防重复提交注解
        NoDuplicateSubmit noDuplicateSubmit = getNoDuplicateSubmitAnnotation(joinPoint);
        //获取分布式锁标识：no-duplicate+path+currentUserId+md5
        String lockKey = String.format("no-duplicate-submit:path:%s:currentUserId:%s:md5:%s",getServletPath(),getCurrentUserId(),calcArgsMD5(joinPoint));
        //尝试获取锁，获取锁失败就意味着已经重复提交，直接抛出ClientException
        RLock lock = redissonClient.getLock(lockKey);
        if (!lock.tryLock()) {
            throw new ClientException(noDuplicateSubmit.message());
        }
        Object result;
        //try catch执行原逻辑，finally解锁
        try {
            result = joinPoint.proceed();
        }finally {
            lock.unlock();
        }
        //返回结果
        return result;
    }

    /**
     * @return 返回自定义防重复提交注解
     */
    public static NoDuplicateSubmit getNoDuplicateSubmitAnnotation(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        //获得切点的方法签名
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        //获得目标方法
        Method method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getMethod().getParameterTypes());
        //返回目标方法上的注解
        return method.getAnnotation(NoDuplicateSubmit.class);
    }

    /**
     * @return 获取当前线程上下文 ServletPath
     */
    private String getServletPath(){
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return sra.getRequest().getServletPath();
    }

    /**
     * @return 当前操作用户 ID
     */
    private String getCurrentUserId() {
        // 用户属于非核心功能，这里先通过模拟的形式代替。后续如果需要后管展示，会重构该代码
        return "1810518709471555585";
    }

    /**
     * @return joinPoint md5
     */
    private String calcArgsMD5(ProceedingJoinPoint joinPoint) {
        return DigestUtil.md5Hex(JSON.toJSONBytes(joinPoint.getArgs()));
    }
}
