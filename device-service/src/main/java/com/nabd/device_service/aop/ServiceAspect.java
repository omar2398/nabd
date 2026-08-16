package com.nabd.device_service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ServiceAspect {
  @Pointcut("execution(* com.nabd.device_service.service.*.*(..))")
  public void serviceMethods() {}
  ;

  @Around("serviceMethods()")
  public Object serviceExecutionLoggingAspect(ProceedingJoinPoint join) throws Throwable {
    log.info(
        "Start executing {} method, with arguments {}",
        join.getSignature().getName(),
        Arrays.toString(join.getArgs()));
    var start = System.nanoTime();
    var result = join.proceed();
    var end = System.nanoTime();
    log.info(
        "The method {} returned response {}, and took around {}ms",
        join.getSignature().getName(),
        result,
        (end - start)/1000000);

    return result;
  }
}
