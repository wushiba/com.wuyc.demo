package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
 * @since 2021-04-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_website_goods_record")
public class WebsiteGoodsRecord extends Model<WebsiteGoodsRecord> {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private LocalDateTime createTime;

    /**
     * 网点id
     */
    private Integer websiteId;

    /**
     * 商户id
     */
    private Integer merchantId;

    private String pidPath;

    /**
     * 数量
     */
    private Integer quantity;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
