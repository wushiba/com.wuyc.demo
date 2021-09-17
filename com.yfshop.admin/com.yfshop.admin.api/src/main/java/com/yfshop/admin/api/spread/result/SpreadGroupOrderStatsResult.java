package com.yfshop.admin.api.spread.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class SpreadGroupOrderStatsResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer orderCount;
    private BigDecimal orderPrice;

}
