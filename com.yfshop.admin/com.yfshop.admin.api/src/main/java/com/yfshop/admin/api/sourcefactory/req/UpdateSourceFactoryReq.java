package com.yfshop.admin.api.sourcefactory.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-25 18:54
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(parent = CreateSourceFactoryReq.class)
@Data
public class UpdateSourceFactoryReq extends CreateSourceFactoryReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "工厂ID", required = true)
    @NotNull(message = "工厂ID不能为空")
    private Integer sourceFactoryId;
}
