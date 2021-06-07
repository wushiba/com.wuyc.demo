package com.yfshop.shop.service.healthy.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author Xulg
 * @since 2021-06-07 15:09
 * Description: TODO 请加入类描述信息
 */
@Data
public class PreviewShowShipPlansReq implements Serializable {
    private static final long serialVersionUID = -4763583178646353008L;

    @NotNull(message = "商品ID不能为空")
    private Integer itemId;

    @NotBlank(message = "配送规格不能为空")
    @Pattern(regexp = "^([WM])-[1-9]\\d*$", message = "非法的配送规格")
    private String postRule;
}
