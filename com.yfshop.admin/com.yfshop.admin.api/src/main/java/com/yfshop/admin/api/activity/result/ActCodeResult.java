package com.yfshop.admin.api.activity.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商户批次码详情
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ActCodeResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 批次号 年月日+id编号
     */
    private String batchNo;

    /**
     * 活动码数量
     */
    private Integer quantity;

    /**
     * 活动id
     */
    private Integer actId;

    /**
     * 活动名
     */
    private String actTitle;

    /**
     * 文件地址
     */
    private String fileUrl;


    private String fileStatus;

    /**
     * 是否下载
     */
    private String isDownload;

    /**
     * 是否发送
     */
    private String isSend;


}
