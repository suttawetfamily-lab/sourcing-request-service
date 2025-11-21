package com.pantavanij.sourcingreq.services.interceptor;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = Logger.getLogger(String.valueOf(LogAspect.class));

    private final HttpServletRequest request;

    @Around("@within(controllerExecuteTime) || @annotation(controllerExecuteTime)")
    public Object controllerExecutionTime(ProceedingJoinPoint joinPoint, ControllerExecuteTime controllerExecuteTime)
            throws Throwable {
        try {
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Object object = joinPoint.proceed();
            stopWatch.stop();
            logger.info("URI: "+ request.getRequestURI() + ", Execute time: " + stopWatch.getTotalTimeMillis() + " ms");
            return object;
        } catch (Exception ex) {
//            logger.severe("Exception at :" + joinPoint.getTarget().getClass() + " : " + ex);
            throw ex;
        }
    }

    @Around("@within(methodExecuteTime) || @annotation(methodExecuteTime)")
    public Object methodExecutionTime(ProceedingJoinPoint joinPoint, MethodExecuteTime methodExecuteTime)
            throws Throwable {
        try {
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Object object = joinPoint.proceed();
            stopWatch.stop();
            logger.info("Method name: "+ joinPoint.getSignature().getName() + ", Execute time: " + stopWatch.getTotalTimeMillis() + " ms");
            return object;
        } catch (Exception ex) {
//            logger.severe("Exception at :" + joinPoint.getTarget().getClass() + " : " + ex);
            throw ex;
        }
    }
}
