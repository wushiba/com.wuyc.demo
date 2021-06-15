package com.yfshop.common.log;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.yfshop.common.exception.ApiException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Web层的接口访问日志切面
 *
 * @author Xulg
 * Created in 2019-03-25 11:30
 */
@ConditionalOnWebApplication
@Order(1)
@Aspect
public class WebSystemOperateLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(WebSystemOperateLogAspect.class);
    private static final List<Class<?>> IGNORE_VALUE_CLASS_TYPE = new CopyOnWriteArrayList<>(Arrays.asList(
            HttpServletRequest.class, HttpServletResponse.class, MultipartFile.class, Model.class, ModelMap.class
    ));
    private static final Object EMPTY_VALUE = new Object();
    private static final String[] IGNORE_METHOD_NAME_PREFIX = {"find", "get", "query", "pageQuery", "fetch", "list"};
    private static final String ACCEPT_HEADER = "accept";
    private static final String ACCEPT_VALUE = "application/json";
    private static final String X_REQUESTED_WITH_HEADER = "X-Requested-With";
    private static final String X_REQUESTED_WITH_VALUE = "XMLHttpRequest";
    private static final String X_WWW_FORM_URLENCODED = "x-www-form-urlencoded";
    private static final String[] REQUEST_METHOD = {"GET", "HEAD"};
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    @Value("${spring.profiles.active}")
    private String profile;


    @Pointcut("@within(org.springframework.stereotype.Controller)"
            + "||@within(org.springframework.web.bind.annotation.RestController)"
    )
    public void targetPoint() {
    }


    @Around("targetPoint()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method targetMethod = ReflectUtil.getMethodByName(targetClass, joinPoint.getSignature().getName());
        if (this.shouldIgnore(targetClass, targetMethod)) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String requestMethod = request.getMethod();
        String requestUrl = request.getRequestURL().toString();
        String visitorIp = ServletUtil.getClientIP(request);
        boolean isAjax = this.isAjaxRequest(request, targetClass, targetMethod);
        Integer merchantId = this.loginInfo(request);
        Object requestParameter = this.fetchRequestParameter(request, joinPoint, targetMethod);

        VisitInfo visitInfo = new VisitInfo();
        visitInfo.setRequestMethod(requestMethod);
        visitInfo.setMerchantId(merchantId);
        visitInfo.setUserId(null);
        visitInfo.setRequestUrl(requestUrl);
        visitInfo.setVisitorClientIp(visitorIp);
        visitInfo.setRequestParameter(requestParameter);
        visitInfo.setMethodInfo(joinPoint.toLongString());
        visitInfo.setStartTimestamp(System.currentTimeMillis());
        visitInfo.setIsAjax(isAjax);
        visitInfo.setCookies(readCookies(request));
        visitInfo.setHeaders(ServletUtil.getHeaderMap(request));
        try {
            // execute the handler method
            Object result = joinPoint.proceed();

            // log the result
            visitInfo.setEndTimestamp(System.currentTimeMillis());
            visitInfo.setTimeConsume(visitInfo.getEndTimestamp() - visitInfo.getStartTimestamp());
            visitInfo.setIsSuccess(true);
            if (result instanceof ModelAndView) {
                ViewResult viewResult = new ViewResult();
                viewResult.setViewName(((ModelAndView) result).getViewName());
                viewResult.setAttributes(((ModelAndView) result).getModelMap());
                visitInfo.setReturnResult(viewResult);
            } else if (result instanceof String && !visitInfo.getIsAjax()) {
                ViewResult viewResult = new ViewResult();
                viewResult.setViewName(result);
                viewResult.setAttributes(this.fetchAttributes(joinPoint));
                visitInfo.setReturnResult(viewResult);
            } else {
                visitInfo.setReturnResult(result);
            }
            if (!"/".equals(request.getServletPath())) {
                logger.info("*******************************\r\nWebSystemLogAspect接口访问信息: \n"
                        + JSON.toJSONString(visitInfo, true) + "\r\n*********************************");
                saveLog(visitInfo);
            }

            return result;
        } catch (Throwable e) {
            // log the error msg
            visitInfo.setEndTimestamp(System.currentTimeMillis());
            visitInfo.setTimeConsume(visitInfo.getEndTimestamp() - visitInfo.getStartTimestamp());
            visitInfo.setIsSuccess(false);
            if (e instanceof ApiException) {
                visitInfo.setErrorMsg(((ApiException) e).getErrorCode() + ":" + e.getMessage());
            } else {
                visitInfo.setErrorMsg(e.getMessage());
            }
            logger.info("*******************************\r\nWebSystemLogAspect接口访问信息: \n"
                    + JSON.toJSONString(visitInfo, true) + "\r\n*********************************");
            saveLog(visitInfo);
            throw e;
        }
    }

    protected boolean shouldIgnore(Class<?> targetClass, Method targetMethod) {
        return false;
    }

    private boolean isAjaxRequest(HttpServletRequest request, Class<?> targetClass, Method targetMethod) {
        if (StringUtils.containsIgnoreCase(request.getHeader(ACCEPT_HEADER), ACCEPT_VALUE)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(request.getHeader(X_REQUESTED_WITH_HEADER), X_REQUESTED_WITH_VALUE)) {
            return true;
        }
        if (targetMethod != null && targetMethod.isAnnotationPresent(ResponseBody.class)) {
            return true;
        }
        if (targetClass != null) {
            if (targetClass.isAnnotationPresent(RestController.class)) {
                return true;
            }
            if (targetClass.isAnnotationPresent(ResponseBody.class)) {
                return true;
            }
        }
        return false;
    }

    private Integer loginInfo(HttpServletRequest request) {
        if (StpUtil.isLogin()) {
            return StpUtil.getLoginIdAsInt();
        }
        return null;
    }

    private Object fetchRequestParameter(HttpServletRequest request, JoinPoint point, Method targetMethod) {
        Object requestParam;
        String requestMethod = request.getMethod();
        if (StringUtils.equalsAnyIgnoreCase(requestMethod, REQUEST_METHOD)
                || StringUtils.containsIgnoreCase(request.getContentType(), X_WWW_FORM_URLENCODED)) {
            requestParam = ServletUtil.getParamMap(request);
        } else {
            requestParam = JSON.parse(this.parseMethodParameters(point, targetMethod));
        }
        return requestParam;
    }

    private String parseMethodParameters(JoinPoint point, Method targetMethod) {
        Object[] args = point.getArgs();
        String[] parameterNames = this.resolveParameterNames(point, targetMethod);
        Parameter[] parameters = targetMethod.getParameters();
        if (parameterNames == null || args == null || parameters == null
                || parameterNames.length != args.length || parameters.length != args.length) {
            return this.parseArgs2jsonArray(args);
        }
        Map<String, Object> parameterIndexMap = new LinkedHashMap<>(parameterNames.length * 4 / 3 + 1);
        for (int i = 0; i < parameterNames.length; i++) {
            Parameter parameter = parameters[i];
            Object value = args[i];
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                return JSON.toJSONString(value, SerializerFeature.WriteMapNullValue);
            }
            String name;
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                name = StringUtils.isNotBlank(annotation.name()) ? annotation.name() : parameterNames[i];
            } else {
                name = parameterNames[i];
            }
            if (value == null) {
                parameterIndexMap.put(name, null);
                continue;
            }
            if (!Serializable.class.isAssignableFrom(value.getClass())) {
                parameterIndexMap.put(name, EMPTY_VALUE);
                continue;
            }
            if (IGNORE_VALUE_CLASS_TYPE.stream().anyMatch(
                    clazz -> clazz.isAssignableFrom(value.getClass()))) {
                parameterIndexMap.put(name, EMPTY_VALUE);
                continue;
            }
            parameterIndexMap.put(name, value);
        }
        try {
            return JSON.toJSONString(parameterIndexMap, SerializerFeature.WriteMapNullValue);
        } catch (RuntimeException e) {
            logger.error(this.getClass().getName() + "#parseMethodParameters()错误", e);
        }
        return null;
    }

    private String[] resolveParameterNames(JoinPoint point, Method targetMethod) {
        String[] parameterNames;
        if (point.getSignature() instanceof MethodSignature) {
            parameterNames = ((MethodSignature) point.getSignature()).getParameterNames();
        } else {
            parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(targetMethod);
        }
        return parameterNames;
    }

    private String parseArgs2jsonArray(Object[] args) {
        List<Object> values = Arrays.stream(args)
                .filter(Objects::nonNull)
                .filter(value -> Serializable.class.isAssignableFrom(value.getClass()))
                .filter(value -> IGNORE_VALUE_CLASS_TYPE.stream()
                        .noneMatch(clazz -> clazz.isAssignableFrom(value.getClass())))
                .collect(Collectors.toList());
        try {
            return JSON.toJSONString(values, SerializerFeature.WriteMapNullValue);
        } catch (RuntimeException e) {
            logger.error(this.getClass().getName() + "#parseArgs2jsonArray()错误", e);
        }
        return null;
    }

    private Map<String, Object> fetchAttributes(JoinPoint point) {
        Map<String, Object> attributes = new HashMap<>(16);
        for (Object arg : point.getArgs()) {
            if (arg == null) {
                continue;
            }
            if (Model.class.isAssignableFrom(arg.getClass())) {
                attributes.putAll(((Model) arg).asMap());
            } else if (ModelMap.class.isAssignableFrom(arg.getClass())) {
                attributes.putAll((ModelMap) arg);
            } else if (HttpServletRequest.class.isAssignableFrom(arg.getClass())) {
                HttpServletRequest request = (HttpServletRequest) arg;
                Map<String, Object> attrs = CollectionUtil.newArrayList(request.getAttributeNames()).stream()
                        .filter(name -> !StringUtils.contains(name, "org.springframework"))
                        .filter(name -> !StringUtils.contains(name, ".FILTERED"))
                        .collect(Collectors.toMap(name -> name, request::getAttribute));
                attributes.putAll(attrs);
            }
        }
        return attributes;
    }

    private Map<String, Map<String, Object>> readCookies(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = ServletUtil.readCookieMap(request);
        Map<String, Map<String, Object>> cookieIndexMap = Maps.newHashMapWithExpectedSize(cookieMap.size());
        for (Entry<String, Cookie> entry : cookieMap.entrySet()) {
            String cookieName = entry.getKey();
            Cookie cookie = entry.getValue();
            Map<String, Object> cookieInfo = Maps.newHashMapWithExpectedSize(7);
            cookieInfo.put("value", cookie.getValue());
            cookieInfo.put("comment", cookie.getComment());
            cookieInfo.put("domain", cookie.getDomain());
            cookieInfo.put("maxAge", cookie.getMaxAge());
            cookieInfo.put("path", cookie.getPath());
            cookieInfo.put("secure", cookie.getSecure());
            cookieInfo.put("version", cookie.getVersion());
            cookieIndexMap.put(cookieName, cookieInfo);
        }
        return cookieIndexMap;
    }

    private boolean isProductProfile() {
        return "pro".equalsIgnoreCase(profile);
    }

    protected void saveLog(VisitInfo visitInfo) {

    }

    @Data
    protected static class VisitInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 请求方式
         */
        private String requestMethod;

        /**
         * 请求商户id信息
         */
        private Integer merchantId;

        /**
         * 用户id
         */
        private Integer userId;

        /**
         * 请求接口url
         */
        private String requestUrl;

        /**
         * 客户端ip
         */
        private String visitorClientIp;

        /**
         * 请求参数
         */
        private Object requestParameter;

        /**
         * 拦截接口的方法信息
         */
        private String methodInfo;

        /**
         * 接口开始执行的时间戳
         */
        private long startTimestamp;

        /**
         * 接口结束执行的时间戳
         */
        private long endTimestamp;

        /**
         * 接口执行消耗的毫秒数
         */
        private long timeConsume;

        /**
         * 是否是ajax请求
         */
        private Boolean isAjax;

        /**
         * 接口返回结果
         */
        private Object returnResult;

        /**
         * 请求是否成功
         */
        private Boolean isSuccess;

        /**
         * 错误信息
         */
        private String errorMsg;

        /**
         * cookie信息
         */
        private Map<String, Map<String, Object>> cookies;

        private Map<String, String> headers;
    }

    @Data
    private static class ViewResult implements Serializable {
        private static final long serialVersionUID = 1L;
        private Object viewName;
        private Map<String, Object> attributes;
    }
}