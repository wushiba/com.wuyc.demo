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
public class SpreadStatsResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal total;

    private BigDecimal settlement;

    private BigDecimal withdraw;



}
