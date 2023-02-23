package com.wuyc.demo;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sp0313
 * @date 2023年02月06日 15:23:00
 */
public class Demo {

    public static void main(String[] args) {
        mergeData();
    }

    public static void mergeData() {
        List<Model> modelList = initData();
        Map<Object, List<Model>> dataMap = modelList.stream()
                .collect(Collectors.groupingBy(Demo::buildMapKey));

        List<Model> newDataList = new ArrayList<>();
        dataMap.forEach((key, value) -> {
            Model model = new Model();
            model.setMedicineName(value.get(0).getMedicineName());
            model.setMedicineStandard(value.get(0).getMedicineStandard());
            List<String> codeList = value.stream()
                    .map(Model::getTraceCodeList)
                    .flatMap(Collection::stream).collect(Collectors.toList());
            model.setMedicineNum(codeList.size());
            model.setTraceCodeList(codeList);
            newDataList.add(model);
        });
        System.out.println(JSON.toJSONString(newDataList, true));
    }

    private static Object buildMapKey(Model data) {
        return data.getMedicineName() + "-" + data.getMedicineStandard();
    }

    public static List<Model> initData() {
        return Lists.newArrayList(
                new Model("氟比洛芬凝胶帖膏", "3帖/盒", 1, Lists.newArrayList("11111111")),
                new Model("氟比洛芬凝胶帖膏", "6帖/盒", 1, Lists.newArrayList("22222222")),
                new Model("氟比洛芬凝胶帖膏", "6帖/盒", 1, Lists.newArrayList("33333333")),
                new Model("氟比洛芬凝胶帖膏", "9帖/盒", 1, Lists.newArrayList("44444444")),
                new Model("氟比洛芬凝胶帖膏", "9帖/盒", 2, Lists.newArrayList("55555555", "66666666"))
        );
    }


}
