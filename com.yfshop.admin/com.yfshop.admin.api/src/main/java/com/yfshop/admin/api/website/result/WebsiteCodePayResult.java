package com.yfshop.admin.api.website.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class WebsiteCodePayResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal amount;

    private String orderNo;


}
