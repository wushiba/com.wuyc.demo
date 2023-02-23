package com.wuyc.vo;

import com.wuyc.enums.SexEnum;
import com.wuyc.validator.annotation.CheckEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author sp0313
 * @date 2022年11月25日 10:07:00
 */
@Data
public class StudentDTO {

    private Integer weight;

    private Integer height;

    private String name;

    @NotNull(message = "性别不可以为空")
    @CheckEnum(getter = "getDescription", value = SexEnum.class, message = "错误的性别")
    private Integer sex;

    private String hobby;

    public StudentDTO() {
    }

    public StudentDTO(Integer weight, Integer height, String name, Integer sex) {
        this.sex = sex;
        this.name = name;
        this.weight = weight;
        this.height = height;
    }
}
