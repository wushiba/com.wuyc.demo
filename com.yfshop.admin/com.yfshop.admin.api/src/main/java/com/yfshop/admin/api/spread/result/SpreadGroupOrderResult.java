package com.yfshop.admin.api.spread.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class SpreadGroupOrderResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private String merchantId;
    private String merchantName;
    private Integer orderCount;
    private BigDecimal orderPrice;
    private BigDecimal commission;

}
