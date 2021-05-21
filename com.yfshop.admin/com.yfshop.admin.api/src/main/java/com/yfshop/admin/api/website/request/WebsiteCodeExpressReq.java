package com.yfshop.admin.api.website.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class WebsiteCodeExpressReq implements Serializable {
    private Integer id;
    @NotBlank(message = "快递名不能为空")
    private String expressName;
    @NotBlank(message = "快递名不能为空")
    private String expressNo;
}
