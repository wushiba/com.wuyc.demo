package com.wuyc.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wuyc.vo.SortVO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author sp0313
 * @date 2023年04月24日 11:29:00
 */
public class ListSort {

    private static final Integer GROUP_DETAIL_TOP_MAX_SIZE = 5;

    public static void main(String[] args) throws Exception {
        List<SortVO> sortList = sortList(5555555L, 4, SortVO.initSortList());
        System.out.println(JSON.toJSONString(sortList, true));
    }

    /**
     * 根据传入的外部id、排序号以及排序列表排序
     *
     * @param valueId  外部id
     * @param sortNum  排序号
     * @param sortList 排序列表
     * @return 排好序的数组
     */
    private static List<SortVO> sortList(Long valueId, Integer sortNum, List<SortVO> sortList) {
        // 移除数据里的排序
        if (Objects.nonNull(valueId) && Objects.isNull(sortNum)) {
            return removeSortList(valueId, sortList);
        }
        // 数据库没有的话初始化一条排序
        if (CollectionUtils.isEmpty(sortList)) {
            return Stream.of(new SortVO(valueId, sortNum)).collect(Collectors.toList());
        }
        return fillDataSortList(valueId, sortNum, sortList);
    }

    private static List<SortVO> fillDataSortList(Long valueId, Integer sortNum, List<SortVO> sortList) {

        Map<Integer, Long> sortMap = ListUtils.toMap(sortList, SortVO::getSortNum, SortVO::getValueId);
        List<SortVO> initList = new ArrayList<>();
        for (int i = 1; i <= GROUP_DETAIL_TOP_MAX_SIZE; i++) {
            initList.add(new SortVO(sortMap.getOrDefault(i, null), i));
        }
        if (!sortMap.containsKey(sortNum)) {
            // 如果当前下标不存在直接顶替，且valueId存在存在的话就删掉原来的数据
            if (sortMap.containsValue(valueId)) {
                removeExistMapData(valueId, initList);
            }
            initList.set(sortNum - 1, new SortVO(valueId, sortNum));
            return ListUtils.filterList(initList, data -> Objects.nonNull(data.getValueId()));
        }

        // 当前下标存在且下标对应的valueId没有变,返回原来的集合
        if (sortMap.get(sortNum) == valueId.intValue()) {
            return sortList;
        }

        // 当前下标存在且valueId存在其它下标中,将两个排序号对应的值颠倒
        if (sortMap.containsKey(sortNum) && sortMap.containsValue(valueId)) {
            int oldSortNum = 1;
            long oldValue = sortMap.get(sortNum);
            for (Integer key : sortMap.keySet()) {
                if (valueId.equals(sortMap.get(key))) {
                    oldSortNum = key;
                }
            }
            for (SortVO data : sortList) {
                if (data.getSortNum().intValue() == sortNum) {
                    data.setValueId(valueId);
                } else if (data.getSortNum() == oldSortNum) {
                    data.setValueId(oldValue);
                }
            }
            return sortList;
        }

        // 当前下标存在value不存在其它下标中
        return sortExistNodeDataList(valueId, sortNum, initList);
    }

    private static void removeExistMapData(Long valueId, List<SortVO> initList) {
        for (SortVO sortVO : initList) {
            if (valueId.equals(sortVO.getValueId())) {
                sortVO.setValueId(null);
            }
        }
    }

    /**
     * 排序节点已经存在的数据
     *
     * @param valueId  valueId
     * @param sortNum  排序号
     * @param initList 初始化好的数据
     * @return 排好序的数组
     */
    private static List<SortVO> sortExistNodeDataList(Long valueId, Integer sortNum, List<SortVO> initList) {
        List<SortVO> resultList = new ArrayList<>(initList.subList(0, sortNum - 1));
        List<SortVO> contents = initList.subList(sortNum - 1, GROUP_DETAIL_TOP_MAX_SIZE);
        resultList.add(new SortVO(valueId, sortNum));
        for (int i = 0; i < contents.size(); i++) {
            if (Objects.nonNull(contents.get(i).getValueId())) {
                resultList.add(new SortVO(contents.get(i).getValueId(), sortNum + 1));
            } else {
                resultList.addAll(contents.subList(i + 1, contents.size()));
                break;
            }
        }

        // 设置排序号
        for (int i = 0; i < resultList.size(); i++) {
            resultList.get(i).setSortNum(i + 1);
        }
        return ListUtils.filterList(resultList,
                data -> Objects.nonNull(data.getValueId()) && data.getSortNum() <= GROUP_DETAIL_TOP_MAX_SIZE);
    }

    /**
     * 删除列表里的数据且返回列表
     *
     * @param valueId  valueId
     * @param sortList 排序集合
     * @return 删除后的排序列表
     */
    private static List<SortVO> removeSortList(Long valueId, List<SortVO> sortList) {
        if (CollectionUtils.isEmpty(sortList)) {
            return Lists.newArrayList();
        }
        for (SortVO sortVO : sortList) {
            if (sortVO.getValueId().equals(valueId)) {
                sortList.remove(sortVO);
                return sortList;
            }
        }
        return sortList;
    }

}
