package com.yfshop.admin.api.mall.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-22 17:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateItemCategoryReq extends CreateItemCategoryReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;
}
