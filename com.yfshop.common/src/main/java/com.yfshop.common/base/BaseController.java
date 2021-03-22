package com.yfshop.common.base;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.yfshop.common.api.ResultCode;
import com.yfshop.common.exception.ApiException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Xulg
 * Created in 2021-03-22 11:23
 */
public interface BaseController {

    /**
     * 获取当前登录用户的主键信息
     *
     * @return the id
     */
    default Long getCurrentAdminUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 获取cookie的值
     *
     * @param cookieName the cookie name
     * @return the cookie value or null
     */
    default String getCookieValue(String cookieName) {
        Cookie cookie = ServletUtil.getCookie(getCurrentRequest(), cookieName);
        return cookie == null ? null : cookie.getValue();
    }

    /**
     * 删除cookie
     *
     * @param cookieName the cookie name
     */
    default void deleteCookie(String cookieName) {
        ServletUtil.addCookie(getCurrentResponse(), cookieName, null, 0);
    }

    /**
     * 写入cookie
     *
     * @param key    the cookie key
     * @param value  the cookie value
     * @param expire 过期时间(秒)
     */
    default void addCookie(String key, String value, int expire) {
        ServletUtil.addCookie(getCurrentResponse(), key, value, expire);
    }

    /**
     * 从request获取请求参数
     *
     * @param name the parameter name
     * @return the parameter value
     */
    default String getParameter(String name) {
        return getCurrentRequest().getParameter(name);
    }

    /**
     * 从request获取请求参数
     *
     * @param name the parameter name
     * @return the parameter values
     */
    default String[] getParameterValues(String name) {
        return getCurrentRequest().getParameterValues(name);
    }

    /**
     * 获取当前的HttpServletResponse对象
     *
     * @return the http servlet response
     */
    default HttpServletResponse getCurrentResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new ApiException(ResultCode.FAILED);
        }
        return attributes.getResponse();
    }

    /**
     * 获取当前的HttpServletRequest对象
     *
     * @return the http servlet request
     */
    default HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new ApiException(ResultCode.FAILED);
        }
        return attributes.getRequest();
    }

    /**
     * 获取当前的会话
     *
     * @return the http session
     */
    default HttpSession getCurrentSession() {
        return getCurrentRequest().getSession();
    }

}