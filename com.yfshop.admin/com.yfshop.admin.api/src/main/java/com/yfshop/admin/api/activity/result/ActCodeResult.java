package com.yfshop.admin.api.activity.result;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

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

    private String fileSrcUrl;

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


    private Integer type;

    private String spec;


    private String fileName;


    public String getFileName() {
        String fileName = "光明活动码";
        if (StringUtils.isNotBlank(this.getFileSrcUrl())) {
            fileName = fileName + FileUtil.getName(this.getFileSrcUrl());
        } else {
            fileName = fileName + getBatchNo() + "(内盒码" + getQuantity() / 10000f + "万个" + getSpec() + "ml).txt";
        }
        return fileName;
    }

}
