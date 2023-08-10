package com.wuyc.util.analyze;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * 自定义注解
 *
 * @author sp0313
 * @date 2023年08月10日 10:06:00
 */
@Documented
@Target({FIELD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HumpConverter {

//    boolean isConverterHump() default true;

    public String converterFiled() default "";
}
