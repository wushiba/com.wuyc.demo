package com.wuyc.demo;

import lombok.Data;

import java.util.List;

/**
 * @author sp0313
 * @date 2023年02月06日 15:23:00
 */
@Data
public class Model {

    private String medicineName;

    private String medicineStandard;

    private Integer medicineNum;

    private List<String> traceCodeList;

    public Model() {
    }

    public Model(String medicineName, String medicineStandard, Integer medicineNum, List<String> traceCodeList) {
        this.medicineName = medicineName;
        this.medicineStandard = medicineStandard;
        this.medicineNum = medicineNum;
        this.traceCodeList = traceCodeList;
    }
}
