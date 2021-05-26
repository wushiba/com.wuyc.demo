package com.yfshop.shop.service.healthy.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

/**
 * @author Xulg
 * Description:
 * Created in 2021-05-26 15:34
 */
@Data
public class SubmitHealthyOrderReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "商品ID不能为空")
    private Integer itemId;
    @NotNull(message = "地址信息不能为空")
    private Integer addressId;
    @NotNull(message = "购买数量不能为空")
    @Positive(message = "购买数量必须大于0")
    private Integer buyCount = 1;
    private String clientIp;
}