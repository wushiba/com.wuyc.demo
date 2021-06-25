package com.yfshop.admin.api.mall.request;

import com.yfshop.common.enums.BannerPositionsEnum;
import com.yfshop.common.validate.annotation.CandidateValue;
import com.yfshop.common.validate.annotation.CheckEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-22 17:29
 */
@ApiModel
@Data
public class CreateBannerReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "banner名称", required = true)
    @NotBlank(message = "banner名称不能为空")
    private String bannerName;

    @ApiModelProperty(value = "banner类型", allowableValues = "home|banner", required = true)
    @NotBlank(message = "banner类型不能为空")
    @CheckEnum(value = BannerPositionsEnum.class, message = "banner类型只能是home|banner|personal_center")
    private String positions;

    @ApiModelProperty(value = "banner图片链接", required = true)
    @NotBlank(message = "banner图片链接不能为空")
    private String imageUrl;

    @ApiModelProperty(value = "banner跳转链接", required = true)
    @NotBlank(message = "banner跳转链接不能为空")
    private String jumpUrl;

    @ApiModelProperty(value = "是否上架", allowableValues = "Y|N", required = true)
    @NotBlank(message = "是否上架不能为空")
    @CandidateValue(candidateValue = {"Y", "N"}, message = "是否上架值只能是Y|N")
    private String isEnable = "N";

    @ApiModelProperty(value = "排序字段")
    private Integer sort = 0;
}
