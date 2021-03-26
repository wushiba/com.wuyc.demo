package com.yfshop.common.config;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import cn.hutool.core.date.DateUtil;
import com.yfshop.common.exception.CustomGlobalExceptionResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * MVC公共配置信息
 *
 * @author Xulg
 * Created in 2021-03-26 14:55
 */
public abstract class BaseWebMvcConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active}")
    protected String profile;

    /**
     * 404页面配置
     * 500页面配置
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
            @Override
            public void customize(ConfigurableWebServerFactory factory) {
                // 走的是页面路由接口，而非页面模板
                ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
                ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
                factory.addErrorPages(error404Page, error500Page);
            }
        };
    }

    @Bean
    public CustomGlobalExceptionResolver customGlobalExceptionResolver() {
        return new CustomGlobalExceptionResolver();
    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(@Nonnull InterceptorRegistry registry) {
        // dev环境关闭验证
        if (!StringUtils.equals("dev", profile)) {
            registry.addInterceptor(new SaAnnotationInterceptor())
                    .addPathPatterns("/**")
                    .excludePathPatterns("");
        }
    }

    /**
     * 字符串转日期
     */
    @Bean
    public Converter<String, Date> dateConverter() {
        return new StringDateConverter();
    }

    /**
     * 字符串转日期
     */
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new StringLocalDateTimeConverter();
    }

    private static class StringDateConverter implements Converter<String, Date> {
        @Override
        public Date convert(@Nullable String dateString) {
            if (StringUtils.isBlank(dateString)) {
                return null;
            } else if (NumberUtils.isParsable(dateString) && dateString.length() == 13) {
                // 时间戳
                return new Date(Long.parseLong(dateString));
            } else {
                // yyyy-MM-dd HH:mm:ss
                // yyyy-MM-dd
                // ......
                return DateUtil.parse(dateString).toJdkDate();
            }
        }
    }

    private static class StringLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(@Nullable String dateString) {
            if (StringUtils.isBlank(dateString)) {
                return null;
            } else if (NumberUtils.isParsable(dateString) && dateString.length() == 13) {
                // 时间戳
                return new Date(Long.parseLong(dateString))
                        .toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            } else {
                // yyyy-MM-dd HH:mm:ss
                // yyyy-MM-dd
                // ......
                return DateUtil.parse(dateString).toJdkDate()
                        .toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
        }
    }

}
