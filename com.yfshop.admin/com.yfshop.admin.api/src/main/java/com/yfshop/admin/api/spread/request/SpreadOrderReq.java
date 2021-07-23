package com.yfshop.admin.api.spread.request;

import com.yfshop.common.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author yoush
 * @since 2021-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SpreadOrderReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Date startTime;
    
    private Date endTime;

    private String orderNo;

    private String itemName;

    private String merchantName;

    private Integer merchantId;

    private String merchantMobile;

    private String merchantRole;

    private String pidName;

    private Integer pid;

    /**
     * 订单金额
     */
    private BigDecimal orderPrice;

    /**
     * 渠道佣金
     */
    private BigDecimal firstCommission;

    /**
     * 上级佣金
     */
    private BigDecimal secondCommission;

    /**
     * WAIT 预结算 SUCCESS 已完成 FAIL 已失败
     */
    private String orderStatus;

    private String key;

    private Integer pageIndex = 1;

    private Integer pageSize = 10;

    public Date getEndTime() {
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }
}
