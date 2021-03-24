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
 * ip地址表
 * </p>
 *
 * @author yoush
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_ip_address")
public class IpAddress extends Model<IpAddress> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * ip区间开始
     */
    private String ipStartStr;

    /**
     * ip区间结束
     */
    private String ipEndStr;

    private Long ipStartLong;

    private Long ipEndLong;

    /**
     * ip所在地址
     */
    private String address;

    /**
     * 运营商
     */
    private String operator;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
