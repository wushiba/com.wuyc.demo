package com.yfshop.common.config;

import cn.hutool.core.collection.IterUtil;
import com.yfshop.common.api.ErrorCode;
import com.yfshop.common.api.ResultCode;
import com.yfshop.common.exception.ApiException;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

import javax.annotation.Nonnull;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

/**
 * @author Xulg
 * Created in 2021-03-24 15:46
 */
public abstract class BaseValidationConfig {
    private static final Logger logger = LoggerFactory.getLogger(BaseValidationConfig.class);

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    public DefaultPointcutAdvisor methodValidationAdvisor() {
        OptionalValidatorFactoryBean validator = new FailFastOptionalValidatorFactoryBean();
        validator.afterPropertiesSet();
        Pointcut pointcut = new AnnotationMatchingPointcut(Validated.class, true);
        MethodValidationInterceptor validationInterceptor = new CustomizeMethodValidationInterceptor(validator);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, validationInterceptor);
        advisor.setOrder(1);
        logger.info("*****************************创建DefaultPointcutAdvisor***********************************");
        return advisor;
    }

    private static class FailFastOptionalValidatorFactoryBean extends OptionalValidatorFactoryBean {
        @Override
        protected void postProcessConfiguration(@Nonnull javax.validation.Configuration<?> configuration) {
            ((HibernateValidatorConfiguration) configuration).failFast(true);
        }
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
