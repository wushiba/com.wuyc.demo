package com.wuyc.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author sp0313
 * @date 2023年09月13日 09:49:00
 */
@Slf4j
@Component
public class MyListenerProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
            if (requestMapping != null && requestMapping.value().length > 0) {
                //插入到数据中
                List<String> dataList = CollectionUtils.arrayToList(requestMapping.value());
                log.info(JSON.toJSONString(dataList));
                log.info(JSON.toJSONString(requestMapping.value()));
            }
        }
        return bean;
    }
}
