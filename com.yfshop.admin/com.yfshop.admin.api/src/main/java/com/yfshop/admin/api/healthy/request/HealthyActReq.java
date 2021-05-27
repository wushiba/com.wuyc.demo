package com.yfshop.admin.api.healthy.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品图片表
 * </p>
 *
 * @author yoush
 * @since 2021-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HealthyActReq implements Serializable {

    /**
     * 商品编号
     */
    private String actName;

    /**
     * 商品编号
     */
    private Integer itemId;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 排序字段
     */
    private Integer sort;

    private String isEnable;

}
