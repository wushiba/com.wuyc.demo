package com.yfshop.admin.api.website.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class WebsiteCodeAmountResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer quantity;

    private BigDecimal amount;

    private BigDecimal postage;


}
