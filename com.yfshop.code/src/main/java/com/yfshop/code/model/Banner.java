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
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_banner")
public class Banner extends Model<Banner> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * banner名称
     */
    private String bannerName;

    /**
     * home(首页图片) | banner(轮播图)
     */
    private String positions;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 跳转链接
     */
    private String jumpUrl;

    /**
     * 排序字段
     */
    private Integer sort;

    private String isEnable;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
