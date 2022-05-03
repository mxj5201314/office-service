package com.zhouzhao.office.online_collaborative_office.common.config.shiro;

import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Pointcut("execution(public * com.zhouzhao.office.online_collaborative_office.controller.*.*(..)))")
    public void aspect() {
    }

    @Around("aspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        BaseResponse baseResponse = (BaseResponse) point.proceed(); //方法执行结果
        String token = threadLocalToken.getToken();
        //如果ThreadLocal中存在Token，说明是更新的Token
        if (token != null) {
            baseResponse.setToken(token);
            threadLocalToken.clearToken();
        }
        return baseResponse;
    }
}