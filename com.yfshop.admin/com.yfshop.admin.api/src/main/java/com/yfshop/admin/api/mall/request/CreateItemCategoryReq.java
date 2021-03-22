package com.yfshop.admin.api.mall.request;

import com.yfshop.common.validate.annotation.CandidateValue;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-22 17:29
 */
@Data
public class CreateItemCategoryReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotBlank(message = "分类名称不能为空")
    @Length(min = 1, max = 4, message = "分类名称不能超过{max}个字")
    private String categoryName;
    @NotBlank(message = "是否上架不能为空")
    @CandidateValue(candidateValue = {"Y", "N"}, message = "是否上架值只能是Y|N")
    private String isEnable = "Y";
    private Integer sort = 0;
}
