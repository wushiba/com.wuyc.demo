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
 * 用户购物车
 * </p>
 *
 * @author yoush
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_user_cart")
public class UserCart extends Model<UserCart> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 商户id
     */
    private Integer itemId;

    private Integer skuId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * sku名称
     */
    private String skuTitle;

    /**
     * 封面图
     */
    private String skuCover;

    /**
     * 规格id字符串(1,2,3,4,5)
     */
    private String specValueIdPath;

    /**
     * 规格名称和值的json串({"尺码":"38","颜色":"黑色"})
     */
    private String specNameValueJson;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
