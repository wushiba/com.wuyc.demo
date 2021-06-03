package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author yoush
 * @since 2021-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_express")
public class Express extends Model<Express> {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String expressNo;

    @TableField("dataJson")
    private String datajson;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
