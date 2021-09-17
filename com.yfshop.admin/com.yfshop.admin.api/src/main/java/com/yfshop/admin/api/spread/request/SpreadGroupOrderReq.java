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
public class SpreadGroupOrderReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date startTime;

    private Date endTime;

    private Integer merchantId;

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
