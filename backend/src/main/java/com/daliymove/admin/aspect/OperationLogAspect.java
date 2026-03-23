package com.daliymove.admin.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.daliymove.admin.annotation.Log;
import com.daliymove.admin.entity.OperationLog;
import com.daliymove.admin.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    @Around("@annotation(logAnnotation)")
    public Object around(ProceedingJoinPoint point, Log logAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();

        OperationLog log = new OperationLog();
        log.setCreateTime(LocalDateTime.now());

        try {
            if (StpUtil.isLogin()) {
                log.setUserId(StpUtil.getLoginIdAsLong());
            }
        } catch (Exception ignored) {
        }

        String operation = logAnnotation.value();
        if (StrUtil.isEmpty(operation)) {
            MethodSignature signature = (MethodSignature) point.getSignature();
            operation = signature.getMethod().getName();
        }
        log.setOperation(operation);

        MethodSignature signature = (MethodSignature) point.getSignature();
        log.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());

        try {
            String params = JSONUtil.toJsonStr(point.getArgs());
            if (params.length() > 2000) {
                params = params.substring(0, 2000);
            }
            log.setParams(params);
        } catch (Exception ignored) {
        }

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                log.setIp(getClientIp(request));
            }
        } catch (Exception ignored) {
        }

        Object result;
        try {
            result = point.proceed();
            log.setStatus(1);
        } catch (Throwable e) {
            log.setStatus(0);
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > 2000) {
                errorMsg = errorMsg.substring(0, 2000);
            }
            log.setErrorMsg(errorMsg);
            throw e;
        } finally {
            log.setExecuteTime((int) (System.currentTimeMillis() - startTime));
            operationLogService.saveLog(log);
        }

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}