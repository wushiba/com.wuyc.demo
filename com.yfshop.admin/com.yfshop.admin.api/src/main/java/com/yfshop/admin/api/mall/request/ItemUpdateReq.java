package com.yfshop.admin.api.mall.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 编辑商品所需要的参数封装类
 *
 * @author Xulg
 * Created in 2019-06-25 13:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ItemUpdateReq extends ItemCreateReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "商品id不能为空")
    private Integer itemId;
}
