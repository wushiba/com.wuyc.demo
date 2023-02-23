package com.wuyc.exception;

import com.wuyc.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler implements ResponseBodyAdvice<Result<Object>> {

    private static final Supplier<Date> GMT_NOW = () ->
            new Date(System.currentTimeMillis() - (long) TimeZone.getDefault().getRawOffset());

    @Autowired
    private MessageSource messageSource;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return Result.class.isAssignableFrom(Objects.requireNonNull(returnType.getMethod()).getReturnType());
    }

    @Override
    public Result<Object> beforeBodyWrite(Result<Object> body, MethodParameter returnType, MediaType selectedContentType,
                                          Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                          ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletRequest req = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        if (Objects.nonNull(body) && Objects.nonNull(messageSource)) {
            body.setMessage(messageSource.getMessage(body.getCode(), body.args(), body.getMessage(), req.getLocale()));
        }
        return body;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ConstraintViolationException.class})
    public Result<Object> constraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        List<String> messageList = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        log.error("GlobalExceptionHandler[]param not valid ConstraintViolationException url:{} error:{}", request.getRequestURI(), messageList);
        String message = CollectionUtils.isEmpty(messageList) ? "maskit.failure.general" : messageList.get(0);
        return Result.fail(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException:{}", e.getMessage(), e);
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String code = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst().orElse("maskit.failure.general");
        return Result.fail(code);
    }

    @ExceptionHandler(value = BindException.class)
    public Result<Boolean> errorHandler(BindException ex) {
        log.warn("bindException, e:{}", ex.getMessage(), ex);
        FieldError fieldError = ex.getBindingResult().getFieldError();
        assert fieldError != null;
        return Result.fail(fieldError.getDefaultMessage());
    }

}
