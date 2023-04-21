package com.wuyc.vo;

import com.wuyc.enums.SexEnum;
import com.wuyc.validator.annotation.CheckEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author sp0313
 * @date 2022年11月25日 10:07:00
 */
@Data
public class StudentVO {

    private Integer weight;

    private Integer height;

    private String name;

    Map<String, Object> properties;

    @NotNull(message = "性别不可以为空")
    @CheckEnum(getter = "getDescription", value = SexEnum.class, message = "错误的性别")
    private Integer sex;

    private List<String> aliasNameList;


    private Integer pageNo;

    private Integer pageSize;

    public StudentVO() {
    }

    public StudentVO(Integer weight, Integer height, String name, Integer sex) {
        this.sex = sex;
        this.name = name;
        this.weight = weight;
        this.height = height;
    }

    public StudentVO(Integer weight, Integer height, String name, Integer sex, List<String> aliasNameList) {
        this.sex = sex;
        this.name = name;
        this.weight = weight;
        this.height = height;
        this.aliasNameList = aliasNameList;
    }
}
