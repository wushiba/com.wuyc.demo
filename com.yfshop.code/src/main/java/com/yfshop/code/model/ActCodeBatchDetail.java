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
 * 活动码详情
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_act_code_batch_detail")
public class ActCodeBatchDetail extends Model<ActCodeBatchDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 活动码 4位活动id+6位年月日+6位随机数+2位crc校验位
     */
    private String actCode;

    /**
     * 加密后的活动码
     */
    private String cipherCode;

    /**
     * 溯源码
     */
    private String traceNo;

    /**
     * 活动id(优惠券活动)
     */
    private Integer actId;

    /**
     * 批次id
     */
    private Integer batchId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
