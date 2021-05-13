package com.yfshop.admin.api.order.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class OrderExpressReq implements Serializable {
    @NotNull(message = "id不能为空")
    private Long id;
    @NotBlank(message = "快递名不能为空")
    private String expressName;
    @NotBlank(message = "快递名不能为空")
    private String expressNo;

}