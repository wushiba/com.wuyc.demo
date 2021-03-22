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
 * 商户表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_merchant")
public class Merchant extends Model<Merchant> {

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

    private Integer pid;

    /**
     * 上级名称，有编辑上级名称的时候同步到进来
     */
    private String pMerchantName;

    /**
     * pid_path
     */
    private String pidPath;

    private String roleAlias;

    private String roleName;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户手机号
     */
    private String mobile;

    /**
     * 密码
     */
    private String password;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 是否可用: Y(可用), N(禁用)
     */
    private String isEnable;

    /**
     * 是否删除， Y(删除)， N（未删除）, 默认未删除
     */
    private String isDelete;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
