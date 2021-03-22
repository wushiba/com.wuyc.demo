package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 活动码批次记录
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_act_code_batch")
public class ActCodeBatch extends Model<ActCodeBatch> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

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
     * 文件地址
     */
    private String fileUrl;

    /**
     * 是否下载
     */
    private String isDownload;

    /**
     * 是否发送
     */
    private String isSend;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
