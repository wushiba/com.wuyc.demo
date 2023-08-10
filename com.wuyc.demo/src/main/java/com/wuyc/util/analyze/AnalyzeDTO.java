package com.wuyc.util.analyze;

import lombok.Data;

/**
 * @author sp0313
 * @date 2023年08月10日 09:50:00
 */
@Data
public class AnalyzeDTO {

    private String user_name;

    private String real_name;

    private Integer user_type;

    private Boolean if_flag;

    public AnalyzeDTO() {
    }

    public AnalyzeDTO(String user_name, String real_name, Integer user_type, Boolean if_flag) {
        this.user_name = user_name;
        this.real_name = real_name;
        this.user_type = user_type;
        this.if_flag = if_flag;
    }
}
