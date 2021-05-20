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
 * @since 2021-04-20
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
     * 文件源地址
     */
    private String fileSrcUrl;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 上传溯源码文件md5
     */
    private String fileMd5;

    /**
     * WAIT等待 DOING 生成中 SUCCESS 成功 FAIL 失败
     */
    private String fileStatus;

    /**
     * 是否下载
     */
    private String isDownload;

    /**
     * 是否发送
     */
    private String isSend;

    /**
     * 0导入 1生成
     */
    private Integer type;


    private String spec;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
