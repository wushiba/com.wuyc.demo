package com.wuyc.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wuyc
 * @date 2022年11月25日 10:11:00
 */
public class ListUtils {

    private static final int ZERO = 0;

    /**
     * 根据条件获取集合里符合条件第一条数据
     *
     * @param dataList  入参集合
     * @param predicate 条件
     * @return T
     */
    public static <T> T filterFirst(List<T> dataList,
                                    Predicate<T> predicate) {
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }

        return dataList.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据条件过滤集合，返回新的集合
     *
     * @param dataList  入参集合
     * @param predicate 条件
     * @return List<T></>
     */
    public static <T> List<T> filterList(List<T> dataList,
                                         Predicate<T> predicate) {
        return filterMapToList(dataList, Function.identity(), predicate);
    }

    /**
     * 将集合转换为map, key为对象指定列 value为当前对象
     *
     * @param dataList 入参集合
     * @param function 转为map - key属性的function函数
     * @return Map<R, T></R>
     */
    public static <T, R> List<R> mapToList(List<T> dataList,
                                           Function<T, R> function) {
        return filterMapToList(dataList, function, data -> true);
    }

    /**
     * 条件过滤且获取集合里某一列的值
     *
     * @param dataList  入参集合
     * @param function  获取的某一列
     * @param condition 条件
     * @return List<R></R>
     */
    public static <T, R> List<R> filterMapToList(List<T> dataList,
                                                 Function<T, R> function,
                                                 Predicate<T> condition) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Lists.newArrayList();
        }

        return dataList.stream()
                .filter(condition)
                .map(function)
                .collect(Collectors.toList());
    }

    /**
     * 获取集合里某一列的值且去重
     *
     * @param dataList 入参集合
     * @param function 获取的某一列
     * @return List<R></R>
     */
    public static <T, R> List<R> distinctMapToList(List<T> dataList,
                                                   Function<T, R> function) {
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }

        return dataList.stream()
                .map(function)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 将集合转换为map, key为对象指定列 value为当前对象
     *
     * @param dataList 入参集合
     * @param function 转为map - key属性的function函数
     * @return Map<R, T></R>
     */
    public static <T, R> Map<R, T> toMap(List<T> dataList,
                                         Function<T, R> function) {
        return toMap(dataList, function, Function.identity());
    }

    /**
     * 将集合转换为map, key和value均为对象指定列
     *
     * @param dataList      入参集合
     * @param functionKey   转为map - key属性的function函数
     * @param functionValue 转为map - value属性的function函数
     * @return Map<R, T></R>
     */
    public static <T, R, U> Map<R, U> toMap(List<T> dataList,
                                            Function<T, R> functionKey,
                                            Function<T, U> functionValue) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Maps.newHashMap();
        }

        return dataList.stream()
                .collect(Collectors.toMap(functionKey, functionValue, (v1, v2) -> v1));
    }

    /**
     * 根据条件过滤集合后，将集合转换为map, key为对象指定列 value为当前对象
     *
     * @param dataList        集合数据
     * @param function        转为map - key属性的function函数
     * @param filterCondition 过滤条件
     * @return Map<R, T></R>
     */
    public static <T, R> Map<R, T> filterToMap(List<T> dataList,
                                               Function<T, R> function,
                                               Predicate<T> filterCondition) {
        return filterToMap(dataList, function, Function.identity(), filterCondition);
    }

    /**
     * 根据条件过滤集合后，将集合转换为map, key和value均为对象指定列
     *
     * @param dataList        集合数据
     * @param functionKey     转为map - key属性的function函数
     * @param functionValue   转为map - value属性的function函数
     * @param filterCondition 过滤条件
     * @return Map<R, T></R>
     */
    public static <T, R, U> Map<R, U> filterToMap(List<T> dataList,
                                                  Function<T, R> functionKey,
                                                  Function<T, U> functionValue,
                                                  Predicate<T> filterCondition) {
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }

        return dataList.stream()
                .filter(filterCondition)
                .collect(Collectors.toMap(functionKey, functionValue, (v1, v2) -> v1));
    }

    /**
     * 获取集合和集合里的集合合并成一个集合
     *
     * @param dataList 集合数据
     * @param function flatMap入参函数
     * @return 去重后的List集合
     */
    public static <T> List<T> flatMapToList(Collection<List<T>> dataList,
                                            Function<List<T>, Stream<T>> function) {
        return flatMapColumnToList(dataList, Function.identity(), function);
    }

    /**
     * 获取集合和集合里的集合合并成一个集合, 并且获取某一列
     *
     * @param dataList        集合数据
     * @param mapFunction     转换为列的map函数
     * @param flatMapFunction flatMap入参函数
     * @return 去重后某一列的List集合
     */
    public static <T, R> List<R> flatMapColumnToList(Collection<List<T>> dataList,
                                                     Function<T, R> mapFunction,
                                                     Function<List<T>, Stream<T>> flatMapFunction) {
        return filterFlatMapColumnToList(dataList, mapFunction, flatMapFunction, data -> true);
    }

    /**
     * 获取集合和集合里的集合合并成一个集合
     *
     * @param dataList 集合数据
     * @param function flatMap入参函数
     * @return 去重后的List集合
     */
    public static <T> List<T> filterFlatMapToList(Collection<List<T>> dataList,
                                                  Function<List<T>, Stream<T>> function,
                                                  Predicate<T> filterCondition) {
        return filterFlatMapColumnToList(dataList, Function.identity(), function, filterCondition);
    }

    /**
     * 获取集合和集合里的集合合并成一个集合, 并且获取某一列
     *
     * @param dataList        集合数据
     * @param mapFunction     转换为列的map函数
     * @param flatMapFunction flatMap入参函数
     * @return 去重后某一列的List集合
     */
    public static <T, R> List<R> filterFlatMapColumnToList(Collection<List<T>> dataList,
                                                           Function<T, R> mapFunction,
                                                           Function<List<T>, Stream<T>> flatMapFunction,
                                                           Predicate<T> filterCondition) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Lists.newArrayList();
        }

        return dataList.stream()
                .flatMap(flatMapFunction)
                .filter(filterCondition)
                .map(mapFunction)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 将集合和集合里的集合转化成map， key为对象指定列， value为对象本身
     *
     * @param dataList        集合数据
     * @param keyFunction     转为map - key属性的function函数
     * @param flatMapFunction flatMap入参函数
     * @return Map<R, T></R>
     */
    public static <T, R> Map<R, T> flatMapToMap(Collection<List<T>> dataList,
                                                Function<T, R> keyFunction,
                                                Function<List<T>, Stream<T>> flatMapFunction) {
        return filterFlatMapToMap(dataList, keyFunction, flatMapFunction, data -> true);
    }

    /**
     * 将集合和集合里的集合转化成map， key、value均为对象指定列
     *
     * @param dataList        集合数据
     * @param keyFunction     转为map - key属性的function函数
     * @param valueFunction   转为map - value属性的function函数
     * @param flatMapFunction flatMap入参函数
     * @return Map<R, T></R>
     */
    public static <T, R, U> Map<R, U> flatMapToMap(List<List<T>> dataList,
                                                   Function<T, R> keyFunction,
                                                   Function<T, U> valueFunction,
                                                   Function<List<T>, Stream<T>> flatMapFunction) {
        return filterFlatMapToMap(dataList, keyFunction, valueFunction, flatMapFunction, data -> true);
    }

    /**
     * 将集合和集合里的集合转化成map， key为对象指定列， value为对象本身
     *
     * @param dataList        集合数据
     * @param keyFunction     转为map - key属性的function函数
     * @param flatMapFunction flatMap入参函数
     * @param filterCondition 过滤条件
     * @return Map<R, T></R>
     */
    public static <T, R> Map<R, T> filterFlatMapToMap(Collection<List<T>> dataList,
                                                      Function<T, R> keyFunction,
                                                      Function<List<T>, Stream<T>> flatMapFunction,
                                                      Predicate<T> filterCondition) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Maps.newHashMap();
        }

        return dataList.stream()
                .flatMap(flatMapFunction)
                .filter(filterCondition)
                .distinct()
                .collect(Collectors.toMap(keyFunction, Function.identity(), (v1, v2) -> v1));
    }

    /**
     * 将集合和集合里的集合转化成map， key为对象指定列， value为对象本身
     *
     * @param dataList        集合数据
     * @param keyFunction     转为map - key属性的function函数
     * @param valueFunction   转为map - value属性的function函数
     * @param flatMapFunction flatMap入参函数
     * @return Map<R, T></R>
     */
    public static <T, R, U> Map<R, U> filterFlatMapToMap(List<List<T>> dataList,
                                                         Function<T, R> keyFunction,
                                                         Function<T, U> valueFunction,
                                                         Function<List<T>, Stream<T>> flatMapFunction,
                                                         Predicate<T> filterCondition) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Maps.newHashMap();
        }

        return dataList.stream()
                .flatMap(flatMapFunction)
                .filter(filterCondition)
                .distinct()
                .collect(Collectors.toMap(keyFunction, valueFunction, (v1, v2) -> v1));
    }

    /**
     * 根据对象属性进行分组
     *
     * @param dataList      入参数据
     * @param groupFunction 进行分组的属性
     * @return Map<R, List < T>>
     */
    public static <T, R> Map<R, List<T>> group(List<T> dataList,
                                               Function<T, R> groupFunction) {
        return filterGroup(dataList, Objects::nonNull, groupFunction);
    }

    /**
     * 根据条件过滤后，根据对象属性进行分组
     *
     * @param dataList        入参数据
     * @param filterPredicate 过滤条件
     * @param groupFunction   对象熟悉
     * @return Map<R, List < T>>
     */
    public static <T, R> Map<R, List<T>> filterGroup(List<T> dataList,
                                                     Predicate<T> filterPredicate,
                                                     Function<T, R> groupFunction) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Maps.newHashMap();
        }
        return dataList.stream()
                .filter(filterPredicate)
                .collect(Collectors.groupingBy(groupFunction));
    }

    /**
     * 获取List集合里某一列的总和
     *
     * @param dataList 集合参数
     * @param function 求和的属性值
     * @return int
     */
    public static <T> int mapToInt(List<T> dataList,
                                   ToIntFunction<T> function) {
        return filterMapToInt(dataList, function, Objects::nonNull);
    }

    /**
     * 获取List集合里某一列的总和
     *
     * @param dataList        集合参数
     * @param function        求和的属性值
     * @param filterPredicate 过滤条件
     * @return int
     */
    public static <T> int filterMapToInt(List<T> dataList,
                                         ToIntFunction<T> function,
                                         Predicate<T> filterPredicate) {
        if (CollectionUtils.isEmpty(dataList)) {
            return ZERO;
        }
        return dataList.stream()
                .filter(filterPredicate)
                .mapToInt(function).sum();
    }

    /**
     * 获取List集合里某一列的总和
     *
     * @param dataList 集合参数
     * @param function 求和的属性值
     * @return long
     */
    public static <T> long mapToLong(List<T> dataList,
                                     ToLongFunction<T> function) {
        return filterMapToLong(dataList, function, Objects::nonNull);
    }

    /**
     * 获取List集合里某一列的总和
     *
     * @param dataList        集合参数
     * @param function        求和的属性值
     * @param filterPredicate 过滤条件
     * @return long
     */
    public static <T> long filterMapToLong(List<T> dataList,
                                           ToLongFunction<T> function,
                                           Predicate<T> filterPredicate) {
        if (CollectionUtils.isEmpty(dataList)) {
            return ZERO;
        }
        return dataList.stream()
                .filter(filterPredicate)
                .mapToLong(function).sum();
    }

    /**
     * 将字符串根据特定字符分割后，转换成特定类型集合
     *
     * @param dataStr     字符串入参
     * @param regex       分割符号
     * @param mapFunction 转换后类型函数
     * @return List<R>
     */
    public static <R> List<R> stringToList(String dataStr,
                                           String regex,
                                           Function<String, R> mapFunction) {
        if (Objects.isNull(dataStr)) {
            return Lists.newArrayList();
        }
        return Arrays.stream(dataStr.split(regex))
                .filter(Objects::nonNull)
                .map(mapFunction)
                .collect(Collectors.toList());
    }

    /**
     * 将集合转换成字符串
     *
     * @param dataList 集合入参
     * @param regex    分割符号
     * @return String
     */
    public static <R> String listToString(List<R> dataList,
                                          String regex) {
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        return dataList.stream().map(String::valueOf)
                .collect(Collectors.joining(regex));
    }

}
