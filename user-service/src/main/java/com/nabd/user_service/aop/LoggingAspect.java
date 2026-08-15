package com.nabd.user_service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Component
@Aspect // Don't forget this again :D
public class LoggingAspect {
  @Pointcut("execution(* com.nabd.user_service.service.*.*(..))")
  public void serviceMethods() {}
  ;

  @Around("serviceMethods()")
  public Object serviceMethodAspects(ProceedingJoinPoint join) throws Throwable {
    log.info(
        "The method: {} is called with arguments: {}",
        join.getSignature().getName(),
        Arrays.toString(join.getArgs()));
    var start = LocalDateTime.now();
    var result = join.proceed();
    var end = LocalDateTime.now();
    log.info(
        "The method: {} has finished and returned: {}, and took {}ms",
        join.getSignature().getName(),
        result,
        Duration.between(end, start));
    return result;
  }
}
