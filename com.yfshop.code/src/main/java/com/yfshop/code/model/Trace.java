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
@TableName("yf_trace")
public class Trace extends Model<Trace> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    /**
     * 盒码
     */
    private String traceNo;

    /**
     * 箱码
     */
    private String boxNo;

    /**
     * 1001噜渴200ML,1002噜渴458ML
     */
    private String productNo;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
