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
 * 商户批次码详情
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteCodeDetailResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 批次id
     */
    private Integer batchId;

    /**
     * 商户码 3位地区码+6位pid+6位年月日+5位序号
     */
    private String alias;

    /**
     * 是否激活, Y|N
     */
    private String isActivate;

    /**
     * 商户id
     */
    private Integer merchantId;

    /**
     * 绑定商户名称
     */
    private String merchantName;

    /**
     * 商户手机号码
     */
    private String mobile;


}
