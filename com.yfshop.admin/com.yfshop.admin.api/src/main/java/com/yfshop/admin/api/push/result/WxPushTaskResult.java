package com.yfshop.admin.api.push.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class WxPushTaskResult implements Serializable {

    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 推送主题
     */
    private String title;

    /**
     * 推送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime pushTime;

    /**
     * 推送人数
     */
    private Integer pushCount;

    /**
     * 成功人数
     */
    private Integer successCount;

    /**
     * 失败人数
     */
    private Integer failCount;

    /**
     * CLOSED 已关闭 WAIT 等待 DOING 进行中 SUCCESS 成功 FAIL 失败
     */
    private String status;

    /**
     * CREATE ,EXCEL
     */
    private String source;

    /**
     * 导入的文件
     */
    private String fileUrl;

    /**
     * 模板消息id
     */
    private String templateId;

    /**
     * 模板数据[{"name":"","value":""}]
     */
    private String templateData;

    private String remark;

}



