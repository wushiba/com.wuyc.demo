package com.yfshop.common.accesslimit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * IP限流注解
 *
 * @author Xulg
 * Created in 2020-12-04 9:49
 */
@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IpAccessLimit {

    /**
     * 指定时间内，API最多的请求次数
     */
    int limit() default Integer.MAX_VALUE;

    /**
     * 指定时间second，redis数据过期时间
     */
    int second() default 0;

}