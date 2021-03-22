package com.yfshop.admin.config;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * WebMVC的配置
 *
 * @author Xulg
 * Created in 2021-03-22 9:44
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

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

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        new SaRouteInterceptor();
        registry.addInterceptor(new SaAnnotationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("");
    }

    /**
     * 静态资源访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    /**
     * 字符串转日期
     */
    @Bean
    public Converter<String, Date> dateConverter() {
        return new Converter<String, Date>() {
            @Override
            public Date convert(@Nullable String dateString) {
                if (StringUtils.isBlank(dateString)) {
                    return null;
                } else if (NumberUtils.isParsable(dateString)) {
                    // 时间戳
                    return new Date(Long.parseLong(dateString));
                } else {
                    // yyyy-MM-dd HH:mm:ss
                    // yyyy-MM-dd
                    // ......
                    return DateUtil.parse(dateString).toJdkDate();
                }
            }
        };
    }

}
