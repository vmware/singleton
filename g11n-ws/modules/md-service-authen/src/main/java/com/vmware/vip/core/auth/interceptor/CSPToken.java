package com.vmware.vip.core.auth.interceptor;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class CSPToken implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {

    }
}
