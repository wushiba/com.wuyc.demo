package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 活动码详情
 * </p>
 *
 * @author yoush
 * @since 2021-04-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_act_code_batch_detail")
public class ActCodeBatchDetail extends Model<ActCodeBatchDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    /**
     * 活动码 4位活动id+6位年月日+6位随机数+2位crc校验位
     */
    private String actCode;

    /**
     * 溯源码
     */
    private String traceNo;

    /**
     * 活动id(抽奖活动)
     */
    private Integer actId;

    /**
     * 批次id
     */
    private Integer batchId;

    private String spec;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
