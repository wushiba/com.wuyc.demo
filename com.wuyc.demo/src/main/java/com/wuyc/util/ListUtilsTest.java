package com.wuyc.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wuyc.vo.StudentDTO;
import com.wuyc.vo.StudentVO;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wuyc
 * @date 2022年11月25日 13:22:00
 */
public class ListUtilsTest {
    public static void main(String[] args) {
//        List<StudentVO> dataList = initSmallStudentList();
//        System.out.println(dataList.toString());


//        listMap();

//        mapToInt();

//        filterMapToInt();

        // 过滤list中数据，返回第一条数据
//        listFilterFirst();

        // 过滤list中数据，返回list
//        listFilterList();

        // 获取list的某列集合
        listColumnList();

        // 获取list去重后的某列集合
//        listColumnDistinctList();

        // 将list转换为V是本身map
//        listToMap();

        // 将list转换为V是特定属性的map
//        listToMap2();


        // 获取Collection下集合合并成一个list集合
//        flatMapList();

        // 获取将Collection下集合合并成一个list集合，并且取某个字段的数据
//        flatMapColumnList();

        // 获取Collection下集合合并成一个map
//        flatMapToMap();
//        flatMapToColumnMap();
//        filterFlatMapToMap();

        //
//        flatMapToMap();

        // 根据list对象中某个属性分组
//        listGroup();

        // 条件过滤list后，根据对象中某个属性分组
//        listFilterGroup();

        // 字符串转换List
//        stringToList();

        // list转字符串
//        listToString();

//        List<StudentVO> dataList = initSmallStudentList();
//        System.out.println(JSON.toJSONString(dataList));
//        updateStudent(dataList);
//        System.out.println(JSON.toJSONString(dataList));
    }

    public static void listFilterFirst() {
        List<StudentVO> dataList = initSmallStudentList();
        StudentVO studentVO = ListUtils.filterFirst(dataList,
                (data) -> data.getHeight() > 1000 && data.getSex() == 1);
        System.out.println(JSON.toJSONString(studentVO));
    }

    public static void listFilterList() {
        List<StudentVO> dataList = initSmallStudentList();
        List<StudentVO> resultList = ListUtils.filterToList(dataList,
                (data) -> data.getHeight() > 100 && data.getSex() == 1);
        System.out.println(JSON.toJSONString(resultList, true));
    }

    public static void listColumnList() {
        List<StudentVO> dataList = initSmallStudentList();
        List<String> nameList = ListUtils.columnToList(dataList, StudentVO::getName);
        System.out.println(JSON.toJSONString(nameList, true));
    }

    public static void listColumnDistinctList() {
        List<StudentVO> dataList = initSmallStudentList();
        Function<StudentVO, String> function = StudentVO::getName;
        List<String> nameList = ListUtils.distinctColumnToList(dataList, StudentVO::getName);
        System.out.println(JSON.toJSONString(nameList, true));
    }

    public static void listMap() {
        List<StudentVO> dataList = initSmallStudentList();
        List<StudentDTO> resultList = ListUtils.map(dataList, data -> {
            StudentDTO studentDTO = new StudentDTO();
            BeanUtils.copyProperties(data, studentDTO);
            return studentDTO;
        });
        System.out.println(JSON.toJSONString(resultList, true));
    }

    public static void listToMap() {
        List<StudentVO> dataList = initSmallStudentList();
        Map<String, StudentVO> dataMap = ListUtils.toMap(dataList, StudentVO::getName);
        System.out.println(JSON.toJSONString(dataMap, true));

        Map<String, Integer> dataMap2 = ListUtils.toMap(dataList,
                StudentVO::getName, StudentVO::getHeight);
        System.out.println(JSON.toJSONString(dataMap2, true));
    }

    public static void listFilterToMap() {
        List<StudentVO> dataList = initSmallStudentList();
        Map<String, StudentVO> dataMap = ListUtils.filterToMap(dataList,
                StudentVO::getName, (data) -> data.getHeight() > 100);
        System.out.println(JSON.toJSONString(dataMap, true));

        Map<String, Integer> dataMap2 = ListUtils.filterToMap(dataList,
                StudentVO::getName, StudentVO::getHeight, (data) -> data.getHeight() > 100);
        System.out.println(JSON.toJSONString(dataMap2, true));
    }

    public static void flatMapList() {
        List<List<StudentVO>> dataList = initCollectionList();
        List<StudentVO> studentList = ListUtils.flatMapToList(dataList, Collection::stream);
        System.out.println(JSON.toJSONString(studentList, true));
    }

    public static void flatMapColumnList() {
        List<List<StudentVO>> dataList = initCollectionList();
        List<String> nameList = ListUtils.flatMapColumnToList(dataList, StudentVO::getName, Collection::stream);
        System.out.println(JSON.toJSONString(nameList, true));
    }

    public static void flatMapToMap() {
        List<List<StudentVO>> dataList = initCollectionList();
        Map<String, StudentVO> dataMap = ListUtils.flatMapToMap(dataList, StudentVO::getName, Collection::stream);
        System.out.println(JSON.toJSONString(dataMap, true));
    }

    public static void filterFlatMapToMap() {
        List<List<StudentVO>> dataList = initCollectionList();
        Map<String, StudentVO> dataMap = ListUtils.filterFlatMapToMap(dataList, StudentVO::getName,
                Collection::stream, data -> data.getHeight() > 100);
        System.out.println(JSON.toJSONString(dataMap, true));
    }

    public static void flatMapToColumnMap() {
        List<List<StudentVO>> dataList = initCollectionList();
        Map<String, Integer> dataMap = ListUtils.flatMapToMap(dataList,
                StudentVO::getName, StudentVO::getHeight, Collection::stream);
        System.out.println(JSON.toJSONString(dataMap, true));
    }

    public static void listGroup() {
        List<StudentVO> dataList = initSmallStudentList();
        Map<String, List<StudentVO>> dataGroup = ListUtils.group(dataList, StudentVO::getName);
        System.out.println(JSON.toJSONString(dataGroup, true));
    }

    public static void listFilterGroup() {
        List<StudentVO> dataList = initSmallStudentList();
        Map<String, List<StudentVO>> dataGroup = ListUtils.filterGroup(dataList,
                (data) -> data.getHeight() > 100, StudentVO::getName);
        System.out.println(JSON.toJSONString(dataGroup, true));
    }

    public static void mapToInt() {
        List<StudentVO> dataList = initSmallStudentList();
        int count = ListUtils.mapToInt(dataList, StudentVO::getSex);
        System.out.println(count * -1);
    }

    public static void filterMapToInt() {
        List<StudentVO> dataList = initSmallStudentList();
        int count = ListUtils.filterMapToInt(dataList, StudentVO::getSex, (data) -> data.getHeight() > 2220);
        System.out.println(count * -1);
    }

    public static void stringToList() {
        String str = "1,2,3";
        List<Integer> integerList = ListUtils.stringToList(str, ",", Integer::valueOf);
        System.out.println(integerList);
        List<Long> longList = ListUtils.stringToList(str, ",", Long::valueOf);
        System.out.println(longList);
        List<String> strList = ListUtils.stringToList(str, ",", String::valueOf);
        System.out.println(strList);
    }

    public static void listToString() {
        List<Integer> integerList = Lists.newArrayList(1, 2, 3);
        String integerStr = ListUtils.listToString(integerList, ",");
        System.out.println(integerStr);

        List<Long> longList = Lists.newArrayList(100L, 200L, 300L);
        String longStr = ListUtils.listToString(longList, ",");
        System.out.println(longStr);

        List<String> strList = Lists.newArrayList("a", "b", "c");
        String str = ListUtils.listToString(integerList, ",");
        System.out.println(str);
    }

    public static List<StudentVO> initSmallStudentList() {
        return Stream.of(
                new StudentVO(20, 60, "张三", 1),
                new StudentVO(60, 170, "李四", 1),
                new StudentVO(45, 140, "王五", 1),
                new StudentVO(40, 130, "杨迪", 2),
                new StudentVO(55, 120, "杨迪", 2))
                .collect(Collectors.toList());
    }

    public static List<StudentVO> initMiddleStudentList() {
        return Stream.of(
                new StudentVO(55, 165, "梦莹", 2),
                new StudentVO(55, 165, "梦莹", 2),
                new StudentVO(60, 170, "梦莹", 2))
                .collect(Collectors.toList());
    }

    public static List<List<StudentVO>> initCollectionList() {
        return Lists.newArrayList(initSmallStudentList(), initMiddleStudentList());
    }

}
