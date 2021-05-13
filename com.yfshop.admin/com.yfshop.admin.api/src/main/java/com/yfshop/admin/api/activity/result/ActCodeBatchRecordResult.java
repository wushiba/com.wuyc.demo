package com.yfshop.admin.api.activity.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class ActCodeBatchRecordResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 批次id
     */
    private Integer batchId;

    /**
     * 商户id
     */
    private Integer merchantId;

    /**
     * EMAIL,DOWNLOAD
     */
    private String type;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 工厂名
     */
    private String factoryName;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 工厂详细地址
     */
    private String address;

    /**
     * 联系人
     */
    private String mobile;



}
