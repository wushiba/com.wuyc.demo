package com.yfshop.common.accesslimit;

import cn.hutool.extra.servlet.ServletUtil;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * IP限流访问
 *
 * @author Xulg
 * Created in 2020-12-04 9:49
 */
public class IpVisitLimitInterceptor implements HandlerInterceptor {

    @Resource
    private RedisIpVisitLimitCheckStrategy visitLimitCheckStrategy;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ApiException {
        String clientIp = ServletUtil.getClientIP(request);
        if (handler == null) {
            return true;
        }
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 先找方法级别的，没有找类级别的
        IpAccessLimit accessLimit = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), IpAccessLimit.class);
        if (accessLimit == null) {
            // 找class类上的注解
            accessLimit = AnnotationUtils.findAnnotation(handlerMethod.getBean().getClass(), IpAccessLimit.class);
        }
        if (accessLimit == null || accessLimit.second() <= 0) {
            // ignore
            return true;
        }
        boolean ipAccessLegal = visitLimitCheckStrategy.isIpAccessLegal(clientIp, accessLimit.second(), accessLimit.limit());
        Asserts.assertTrue(ipAccessLegal, 500, "访问频率超过限制");
        return true;
    }

}
