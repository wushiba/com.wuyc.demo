package com.wuyc.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.wuyc.vo.StudentDTO;
import com.wuyc.vo.StudentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wuyc
 * @date 2023年02月28日 11:06:00
 */
public class ConvertUtils {

    public static void main(String[] args) {
        StudentVO studentVO = new StudentVO(50, 160, "王五", 1);
        StudentDTO studentDTO = ConvertUtils.convert(studentVO, StudentDTO.class);
        System.out.println(JSON.toJSONString(studentDTO));

        StudentVO studentVO2 = new StudentVO(60, 166, "老六", 0);
        List<StudentVO> dataList = Stream.of(studentVO, studentVO2).collect(Collectors.toList());
        List<StudentDTO> targetList = ConvertUtils.convertList(dataList, StudentDTO.class);
        System.out.println(JSON.toJSONString(targetList));
    }

    /**
     * @param source 源对象
     * @param clazz  目标class
     * @return T - 目标对象
     */
    public static <T> T convert(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        T instantiate = BeanUtils.instantiateClass(clazz);
        copyProperties(source, instantiate);
        return instantiate;
    }

    /**
     * copy class
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) throws BeansException {
        BeanUtils.copyProperties(source, target);
    }

    /**
     * @param origList 源数据集合
     * @param clazz    目标class
     * @return List<K> 目标对象集合
     */
    public static <T, K> List<K> convertList(List<T> origList, Class<K> clazz) {
        List<K> destList = new ArrayList<K>();
        if (!CollectionUtil.isEmpty(origList)) {
            try {
                for (T original : origList) {
                    K result = clazz.newInstance();
                    copyProperties(original, result);
                    destList.add(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return destList;
    }
}
