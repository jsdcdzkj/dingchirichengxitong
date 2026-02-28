package com.jsdc.worktime.global.RateLimit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimiterAspect {

    private final RedisTemplate<String, String> redisTemplate;

    public RateLimiterAspect(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(rateLimiter)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String key = generateKey(attributes.getRequest());
        int limit = rateLimiter.limit();
        long expire = rateLimiter.expire();
        String redisKey = "rate_limiter:" + key;
        long count = redisTemplate.opsForValue().increment(redisKey, 1);
        redisTemplate.expire(redisKey, expire, TimeUnit.SECONDS);
        if (count > limit) {
            throw new RuntimeException("接口访问太频繁");
        }
        return joinPoint.proceed();
    }


    public String generateKey(HttpServletRequest request) {
        String ip = getIpAddress(request);
        String path = request.getRequestURI();
        return "rate_limiter:" + ip + ":" + path;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

}

