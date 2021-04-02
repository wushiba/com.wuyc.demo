package com.yfshop.shop.service.merchant.result;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WebsiteCodeDetailResult {

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
     * 激活时间
     */
    private LocalDateTime activityTime;

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

    /**
     * 上级id
     */
    private Integer pid;

    /**
     * 上级路径
     */
    private String pidPath;

}
