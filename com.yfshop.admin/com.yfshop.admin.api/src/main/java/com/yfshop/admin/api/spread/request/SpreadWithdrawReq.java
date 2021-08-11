package com.yfshop.admin.api.spread.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.yfshop.common.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class SpreadWithdrawReq implements Serializable {

    private String merchantName;

    private Integer merchantId;

    private String merchantMobile;

    private String merchantRole;

    private BigDecimal withdraw;

    private String billNo;

    private String transactionId;

    private String openId;

    private String reUserName;

    private String ipStr;

    /**
     * WAIT 等待 SUCCESS 成功 FAIL 失败
     */
    private String status;

    private Date startTime;
    private Date endTime;

    private Date settlementStartTime;
    private Date settlementEndTime;

    private Integer pageIndex = 1;
    private Integer pageSize = 10;

    public Date getEndTime() {
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }

    public Date getSettlementEndTime() {
        return settlementEndTime == null ? null : DateUtil.plusDays(settlementEndTime, 1);
    }
}
