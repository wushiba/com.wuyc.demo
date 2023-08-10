package com.wuyc.util.analyze;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 驼峰转换工具类
 *
 * @author sp0313
 * @date 2023年08月10日 11:00:00
 */
@Slf4j
public class HumpConverterUtil {

    public static void main(String[] args) throws Exception {
        AnalyzeDTO analyzeDTO = new AnalyzeDTO("老吴", "吴十八", 1, true);
        AnalyzeVO analyzeVO = buildTargetObject(analyzeDTO, AnalyzeVO.class);
        log.info("analyzeVO: {}", JSON.toJSONString(analyzeVO));
    }

    public static <T> T buildTargetObject(Object sourceObject, Class<T> targetClazz) throws Exception {
        Map<String, Object> sourceFieldsMap = initSourceFieldsMap(sourceObject);
        return buildTargetObject(sourceFieldsMap, targetClazz);
    }

    public static <T> T buildTargetObject(Map<String, Object> sourceFieldsMap, Class<T> targetClazz) throws Exception {
        T targetObject = targetClazz.newInstance();
        Field[] targetFields = targetClazz.getDeclaredFields();
        for (Field targetField : targetFields) {
            targetField.setAccessible(true);
            if (targetField.isAnnotationPresent(HumpConverter.class)) {
                HumpConverter humpConverterAnnotation = targetField.getAnnotation(HumpConverter.class);
                targetField.set(targetObject, sourceFieldsMap.get(humpConverterAnnotation.converterFiled()));
            }
        }
        return targetObject;
    }

    private static Map<String, Object> initSourceFieldsMap(Object sourceObject) throws Exception {
        Class<?> sourceClazz = sourceObject.getClass();
        Field[] sourceFields = sourceClazz.getDeclaredFields();
        HashMap<String, Object> hashMap = Maps.newHashMap();
        for (Field sourceField : sourceFields) {
            sourceField.setAccessible(true);
            hashMap.put(sourceField.getName(), sourceField.get(sourceObject));
        }
        return hashMap;
    }
}
