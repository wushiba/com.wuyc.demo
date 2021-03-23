package com.yfshop.admin.api.mall.request;

import com.yfshop.common.validate.annotation.CandidateValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-22 17:29
 */
@ApiModel
@Data
public class CreateItemCategoryReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类名称", required = true)
    @NotBlank(message = "分类名称不能为空")
    @Length(min = 1, max = 4, message = "分类名称不能超过{max}个字")
    private String categoryName;

    @ApiModelProperty(value = "是否上架", allowableValues = "Y|N", required = true)
    @NotBlank(message = "是否上架不能为空")
    @CandidateValue(candidateValue = {"Y", "N"}, message = "是否上架值只能是Y|N")
    private String isEnable = "Y";

    @ApiModelProperty(value = "排序字段")
    private Integer sort = 0;
}
