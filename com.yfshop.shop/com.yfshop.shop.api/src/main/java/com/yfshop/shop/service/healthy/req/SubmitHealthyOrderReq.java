package com.yfshop.shop.service.healthy.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

    @NotBlank(message = "配送规格不能为空")
    @Pattern(regexp = "^([WM])-[1-9]\\d*$", message = "非法的配送规格")
    private String postRule;

    private String clientIp;
}