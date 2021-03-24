package com.yfshop.admin.api.website.result;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商户网点表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteTypeResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 网点类型名称
     */
    private String typeName;



}
