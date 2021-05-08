package com.yfshop.common.exception;

import cn.dev33.satoken.cookie.SaTokenCookieUtil;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.api.ErrorCode;
import com.yfshop.common.api.IErrorCode;
import com.yfshop.common.api.ResultCode;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * 基于HandlerExceptionResolver的
 * 全局异常处理器
 *
 * @author xulg
 */
@ConditionalOnWebApplication
public class CustomGlobalExceptionResolver implements HandlerExceptionResolver, PriorityOrdered, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(CustomGlobalExceptionResolver.class);

    private static final int ERROR_CODE = ResultCode.FAILED.getCode();

    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception e) {
        logger.error("全局异常处理器===>" + String.valueOf(handler) + "捕获异常：", e);
        if (handler == null) {
            // 说明没有找到执行的handler方法
            this.writeFailedJsonResult(e, response);
            return new ModelAndView();
        }
        if (handler instanceof ResourceHttpRequestHandler) {
            ModelAndView mv = new ModelAndView();
            mv.setViewName("error");
            return mv;
        }
        if (!HandlerMethod.class.isAssignableFrom(handler.getClass())) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "handler can not cast to {0}", HandlerMethod.class.getName()));
        }
        if (e instanceof NotLoginException) {
            StpUtil.logout();
        }

        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            if ((apiException.getErrorCode().getCode() == 605 && apiException.getMessage().contains("微信"))) {
                SaTokenCookieUtil.delCookie(request, response, "yfopen");
            }

        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // Ajax请求
        if (this.isAjaxRequest(request, response, handlerMethod)) {
            // 返回json格式的错误信息
            this.writeFailedJsonResult(e, response);
            return new ModelAndView();
        } else {
            // 页面路由
            ModelAndView mv = new ModelAndView("500");
            mv.addObject("errMsg", this.fetchErrorInfoByProfile(e).getMessage());
            return mv;
        }
    }

    @Override
    public int getOrder() {
        // 设置HandlerExceptionResolver的优先级为最高
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    /**
     * is ajax request
     *
     * @param request       the request
     * @param response      the response
     * @param handlerMethod the handler method
     * @return is ajax request
     */
    private boolean isAjaxRequest(HttpServletRequest request,
                                  HttpServletResponse response,
                                  HandlerMethod handlerMethod) {
        String accept = request.getHeader("accept");
        String xRequestedWith = request.getHeader("X-Requested-With");
        String contentType = response.getContentType();
        Method method = handlerMethod.getMethod();
        Class<?> clazz = method.getDeclaringClass();
        return StringUtils.containsIgnoreCase(accept, "application/json")
                || StringUtils.containsIgnoreCase(contentType, "application/json")
                || StringUtils.containsIgnoreCase(xRequestedWith, "XMLHttpRequest")
                || method.isAnnotationPresent(ResponseBody.class)
                || clazz.isAnnotationPresent(ResponseBody.class)
                || clazz.isAnnotationPresent(RestController.class);
    }

    /**
     * write request failed result
     *
     * @param e        the exception
     * @param response the response
     */
    private void writeFailedJsonResult(Exception e, HttpServletResponse response) {
        CodeAndMessage codeAndMessage = this.fetchErrorInfoByProfile(e);
        CommonResult<Object> result = CommonResult.failed(new ErrorCode(codeAndMessage.code, codeAndMessage.message));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        try {
            if (!response.isCommitted()) {
                response.getWriter().write(JSON.toJSONString(result));
            }
        } catch (Exception e1) {
            throw new IllegalStateException(e1);
        }
    }

    private CodeAndMessage fetchErrorInfoByProfile(Throwable t) {
        // 开发环境和预发环境直接返回错误信息
        if ("dev".equalsIgnoreCase(profile) || "uat".equalsIgnoreCase(profile)) {
            int code = t instanceof ApiException ?
                    Optional.ofNullable(((ApiException) t).getErrorCode()).map(IErrorCode::getCode).orElse(500)
                    : 500;
            return new CodeAndMessage(code, t.getMessage());
        } else {
            return this.fetchErrorCodeAndMessageByException(t);
        }
    }

    private CodeAndMessage fetchErrorCodeAndMessageByException(Throwable t) {
        if (true) {
            if (t instanceof ApiException) {
                ApiException apiException = (ApiException) t;
                CodeAndMessage codeAndMessage = new CodeAndMessage();
                codeAndMessage.setCode(apiException.getErrorCode().getCode());
                codeAndMessage.setMessage(apiException.getErrorCode().getMessage());
                return codeAndMessage;

            }
            if (t instanceof NotLoginException) {
                return new CodeAndMessage(605, "当前状态未登录！");
            } else {
                return new CodeAndMessage(500, "您当前的网络不稳定，请稍后再试！");
            }
        }
        CodeAndMessage codeAndMessage = new CodeAndMessage();
        // 参数校验异常的处理
        if (t instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException) t;
            FieldError fieldError = validException.getBindingResult().getFieldError();
            codeAndMessage.setCode(ERROR_CODE);
            if (fieldError != null) {
                codeAndMessage.setMessage(fieldError.getDefaultMessage());
            } else {
                codeAndMessage.setMessage("参数错误");
            }
            return codeAndMessage;
        }
        // 参数校验异常的处理
        if (t instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) t).getConstraintViolations();
            String message = new ArrayList<>(violations).get(0).getMessage();
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage(message);
            return codeAndMessage;
        }
        // 参数校验异常的处理
        if (t instanceof BindException) {
            List<ObjectError> allErrors = ((BindException) t).getAllErrors();
            String message = allErrors.get(0).getDefaultMessage();
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage(message);
            return codeAndMessage;
        }
        // 参数缺失
        if (t instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException exception = (MissingServletRequestParameterException) t;
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage("参数" + exception.getParameterName() + "缺失或类型错误");
            return codeAndMessage;
        }
        // 参数类型异常
        if (t instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException mismatchException = (MethodArgumentTypeMismatchException) t;
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage("参数" + mismatchException.getName() + "类型转换失败");
            return codeAndMessage;
        }
        // 文件上传异常
        if (t instanceof MultipartException) {
            codeAndMessage.setCode(ERROR_CODE);
            String message;
            if (t instanceof MaxUploadSizeExceededException) {
                long maxUploadSize = ((MaxUploadSizeExceededException) t)
                        .getMaxUploadSize();
                if (maxUploadSize < 0) {
                    message = "上传文件超过上限";
                } else {
                    message = "上传文件最大不能超过" + (maxUploadSize / 1024) + "KB";
                }
            } else {
                message = "文件上传异常";
            }
            codeAndMessage.setMessage(message);
            return codeAndMessage;
        }
        // 请求方法不支持
        if (t instanceof HttpRequestMethodNotSupportedException) {
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage("不支持的请求方式"
                    + ((HttpRequestMethodNotSupportedException) t).getMethod());
            return codeAndMessage;
        }
        // 请求体缺失
        if (t instanceof HttpMessageNotReadableException) {
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage("请求体解析异常");
            return codeAndMessage;
        }
        // ContentType错误
        if (t instanceof HttpMediaTypeNotSupportedException) {
            HttpMediaTypeNotSupportedException exception = (HttpMediaTypeNotSupportedException) t;
            MediaType unsupportedContentType = exception.getContentType();
            codeAndMessage.setCode(ERROR_CODE);
            if (unsupportedContentType != null) {
                codeAndMessage.setMessage("不支持的ContentType: " + unsupportedContentType.getType()
                        + "/" + unsupportedContentType.getSubtype());
            } else {
                codeAndMessage.setMessage("不支持的ContentType");
            }
            return codeAndMessage;
        }
        // 业务异常
        if (t instanceof ApiException) {
            ApiException apiException = (ApiException) t;
            codeAndMessage.setCode(apiException.getErrorCode().getCode());
            codeAndMessage.setMessage(apiException.getErrorCode().getMessage());
            return codeAndMessage;
        }
        // dubbo服务调用异常
        if ("org.apache.dubbo.rpc.RpcException".equals(t.getClass().getName())) {
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage("服务调用失败");
            return codeAndMessage;
        }
        if ("org.apache.dubbo.remoting.TimeoutException".equals(t.getClass().getName())) {
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage("服务调用超时");
            return codeAndMessage;
        }
        // 登陆权限验证异常
        if (t instanceof SaTokenException) {
            String message;
            if (t instanceof NotLoginException) {
                // 如果是未登录异常
                NotLoginException notLoginException = (NotLoginException) t;
                switch (notLoginException.getType()) {
                    case NotLoginException.NOT_TOKEN:
                        message = "未提供token";
                        break;
                    case NotLoginException.INVALID_TOKEN:
                        message = "token无效";
                        break;
                    case NotLoginException.TOKEN_TIMEOUT:
                        message = "token已过期";
                        break;
                    case NotLoginException.BE_REPLACED:
                        message = "token已被顶下线";
                        break;
                    case NotLoginException.KICK_OUT:
                        message = "token已被踢下线";
                        break;
                    default:
                        message = "当前会话未登录";
                        break;
                }
                codeAndMessage.setCode(605);
                codeAndMessage.setMessage("当前状态未登录");
                return codeAndMessage;
            } else if (t instanceof NotRoleException) {
                // 如果是角色异常
                NotRoleException notRoleException = (NotRoleException) t;
                message = "无此角色：" + notRoleException.getRole();
            } else if (t instanceof NotPermissionException) {
                // 如果是权限异常
                NotPermissionException notPermissionException = (NotPermissionException) t;
                message = "无此权限：" + notPermissionException.getCode();
            } else {
                SaTokenException saTokenException = (SaTokenException) t;
                message = saTokenException.getMessage();
            }
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage(message);
            return codeAndMessage;
        }
        if (t instanceof TimeoutException) {
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage("访问超时");
            return codeAndMessage;
        }
        if (t instanceof RuntimeException) {
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage("系统执行失败");
            return codeAndMessage;
        }
        // Exception
        if (t instanceof Exception) {
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage("系统异常");
            return codeAndMessage;
        }
        // Error
        if (t instanceof Error) {
            codeAndMessage.setCode(ERROR_CODE);
            codeAndMessage.setMessage("系统错误");
            return codeAndMessage;
        }
        // Throwable
        codeAndMessage.setCode(ERROR_CODE);
        codeAndMessage.setMessage("系统异常错误");
        return codeAndMessage;
    }

    @Override
    public void afterPropertiesSet() {
        logger.info(this.getClass().getName() + "全局异常拦截器初始化完成..."
                + "当前的profile: " + this.profile);
    }

    private static class CodeAndMessage {
        private Integer code;
        private String message;

        private CodeAndMessage() {
        }

        private CodeAndMessage(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
