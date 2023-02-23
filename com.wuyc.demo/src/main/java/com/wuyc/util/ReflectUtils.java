package com.wuyc.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wuyc.vo.StudentVO;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author sp0313
 * @date 2023年02月22日 17:46:00
 */
public class ReflectUtils {

    public static void main(String[] args) {

        StudentVO studentVO = new StudentVO();
        studentVO.setWeight(20);
        studentVO.setHeight(30);
        studentVO.setName("老吴");
        studentVO.setProperties(Maps.newHashMap());
        studentVO.setSex(18);


        Set<String> setList = new HashSet<>();
        setList.add("name");
        Map<String, String> hashmap = new HashMap<>();
        hashmap.put("name", "老吴1");
        Map<String, Object> activitySensitiveInfoMap = checkFilesIsEqual(studentVO, hashmap);
    }

    public static <T> Map<String, Object> checkFilesIsEqual(T object, Map<String, String> hashmap) {
        if (MapUtils.isEmpty(hashmap)) {
            return Maps.newHashMap();
        }

        Set<String> fieldNameList = hashmap.keySet();
        HashMap<String, Object> hashMap = Maps.newHashMap();
        for (String filedName : fieldNameList) {
            try {
                Field field = object.getClass().getDeclaredField(filedName);
                field.setAccessible(true);
                Object fieldValue = field.get(object);
                if (Objects.nonNull(fieldValue) && !fieldValue.toString().equals(hashmap.get(filedName))) {
                    hashMap.put(filedName, fieldValue.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }

}
