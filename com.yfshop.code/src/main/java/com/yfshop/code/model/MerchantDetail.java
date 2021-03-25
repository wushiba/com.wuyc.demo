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
 * 商户详情表(扩展表)
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_merchant_detail")
public class MerchantDetail extends Model<MerchantDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    private Integer merchantId;

    /**
     * 网点类型id
     */
    private Integer websiteTypeId;

    /**
     * 网点类型名称
     */
    private String websiteTypeName;

    /**
     * 是否有冰箱， Y(有)， N(无)
     */
    private String isRefrigerator;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    private String geoHash;

    /**
     * 门头照
     */
    private String headImage;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
