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
 * 
 * </p>
 *
 * @author yoush
 * @since 2021-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_trace_details")
public class TraceDetails extends Model<TraceDetails> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    /**
     * 箱码
     */
    private String boxNo;

    /**
     * 经销商编号
     */
    private String dealerNo;

    /**
     * 经销商手机号
     */
    private String dealerMobile;

    /**
     * 经销商名称
     */
    private String dealerName;

    /**
     * 经销商地址
     */
    private String dealerAddress;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
