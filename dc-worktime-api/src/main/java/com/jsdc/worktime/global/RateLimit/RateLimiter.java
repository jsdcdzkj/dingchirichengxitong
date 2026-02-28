package com.jsdc.worktime.global.RateLimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimiter {
    int limit() default 10; // 限流次数
    long expire() default 1; // 过期时间，单位为秒
}

