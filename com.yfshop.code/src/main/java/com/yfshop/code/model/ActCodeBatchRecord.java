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
 * 活动码下载记录
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_act_code_batch_record")
public class ActCodeBatchRecord extends Model<ActCodeBatchRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 批次id
     */
    private Integer batchId;

    /**
     * 商户id
     */
    private Integer merchatId;

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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
