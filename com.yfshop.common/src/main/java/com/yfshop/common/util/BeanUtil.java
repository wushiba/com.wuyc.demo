package com.yfshop.common.util;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.util.ArrayList;
import java.util.List;

public class BeanUtil {

    public static <T> T convert(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        T instantiate = BeanUtils.instantiateClass(clazz);
        copyProperties(source, instantiate);
        return instantiate;
    }

    public static <T> IPage<T> iPageConvert(IPage source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        Page page = new Page();
        copyProperties(source, page);
        page.setRecords(convertList(source.getRecords(), clazz));
        return page;
    }

    /**
     * copy class
     *
     * @param source 源对象
     * @param target 得到值的对象
     * @throws BeansException
     */
    public static void copyProperties(Object source, Object target) throws BeansException {
        BeanUtils.copyProperties(source, target);
    }

    public static <T, K> List<K> convertList(List<T> origList, Class<K> clazz) {
        List<K> destList = new ArrayList<K>();
        if (!CollectionUtil.isEmpty(origList)) {
            try {
                for (T original : origList) {
                    K result = clazz.newInstance();
                    copyProperties(original, result);
                    destList.add(result);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return destList;
    }
}
