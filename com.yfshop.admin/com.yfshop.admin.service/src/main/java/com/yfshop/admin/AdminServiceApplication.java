package com.yfshop.admin;

import cn.hutool.core.collection.IterUtil;
import com.yfshop.common.api.ErrorCode;
import com.yfshop.common.api.ResultCode;
import com.yfshop.common.exception.ApiException;
import org.aopalliance.intercept.MethodInvocation;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

import javax.annotation.Nonnull;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

@MapperScan("com.yfshop")
@SpringBootApplication
public class AdminServiceApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(AdminServiceApplication.class, args);
        String property = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        System.out.println("==============启动了=====com.yfshop.admin==================环境==" + property);
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    public static DefaultPointcutAdvisor methodValidationAdvisor() {
        OptionalValidatorFactoryBean validator = new OptionalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Pointcut pointcut = new AnnotationMatchingPointcut(Validated.class, true);
        MethodValidationInterceptor validationInterceptor = new CustomizeMethodValidationInterceptor(validator);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, validationInterceptor);
        advisor.setOrder(1);
        return advisor;
    }

    private static class CustomizeMethodValidationInterceptor extends MethodValidationInterceptor {

        CustomizeMethodValidationInterceptor(Validator validator) {
            super(validator);
        }

        @Override
        public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
            try {
                return super.invoke(invocation);
            } catch (ConstraintViolationException e) {
                throw wrapperException(e);
            }
        }

        private ApiException wrapperException(ConstraintViolationException e) {
            ConstraintViolation<?> violation = IterUtil.getFirst(e.getConstraintViolations());
            return new ApiException(new ErrorCode(ResultCode.VALIDATE_FAILED.getCode(), violation.getMessage()));
        }
    }

}


