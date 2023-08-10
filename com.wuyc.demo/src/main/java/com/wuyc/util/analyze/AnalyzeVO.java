package com.wuyc.util.analyze;

import lombok.Data;

/**
 * @author sp0313
 * @date 2023年08月10日 09:50:00
 */
@Data
public class AnalyzeVO {

    @HumpConverter(converterFiled = "user_name")
    private String userName;

    @HumpConverter(converterFiled = "real_name")
    private String realName;

    @HumpConverter(converterFiled = "user_type")
    private Integer userType;

    @HumpConverter(converterFiled = "if_flag")
    private Boolean ifFlag;

    public AnalyzeVO() {

    }

    public AnalyzeVO(String userName, String realName, Integer userType, Boolean ifFlag) {
        this.userName = userName;
        this.realName = realName;
        this.userType = userType;
        this.ifFlag = ifFlag;
    }

    // 1、 封装查询条件。 传入sqlSession、 需要解析的类
    // 2、 处理返回结果。 根据自定义注解判断是否需要进行驼峰转换, 需要的话转换驼峰

}
