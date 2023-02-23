package com.wuyc.validator.annotation;


import com.wuyc.validator.validator.CandidateValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 自定义校验Validation中的校验注解
 * 检查值是否在给定的候选值中
 *
 * @author sp0313
 */
@Documented
@Target({FIELD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
@Constraint(validatedBy = {CandidateValueValidator.class})
public @interface CandidateValue {

    String message() default "invalid value.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // 候选值
    String[] candidateValue() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        CandidateValue[] value();
    }
}