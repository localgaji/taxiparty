package com.localgaji.taxi.__global__.docs;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class LoggingAOP {
    @Pointcut("execution(* com.localgaji.taxi..*Service.*(..))")
    private void service(){}

    @Pointcut("execution(* com.localgaji.taxi..*Controller.*(..))")
    private void controller(){}

    // 컨트롤러 메서드 시작
    @Before("controller()")
    public void beforeController(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        log.trace("================ controller start = {} ================", method.getName());
    }

    // 서비스 메서드 시작
    @Before("service()")
    public void beforeService(JoinPoint joinPoint) {
        // 메서드 정보
        Method method = getMethod(joinPoint);
        log.trace("================== method start = {} ==================", method.getName());

        // 파라미터 정보
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) log.trace("no parameter");
        for (Object arg : args) {
            log.trace("parameter : {}", arg);
        }
    }

    // 서비스 메서드 끝
    @AfterReturning(value = "service()", returning = "returnObj")
    public void afterService(JoinPoint joinPoint, Object returnObj) {
        Method method = getMethod(joinPoint);
        log.trace("return : {}", returnObj);
        log.trace("================== method end === {} ==================", method.getName());
    }

    // 컨트롤러 메서드 끝
    @AfterReturning(value = "controller()", returning = "returnObj")
    public void afterController(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        log.trace("================ controller end === {} ================", method.getName());
    }

    // 메서드 정보 조회
    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
