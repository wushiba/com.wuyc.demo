package com.yfshop.admin.api.mall.request;

import com.yfshop.common.validate.annotation.CandidateValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-22 17:29
 */
@Data
public class CreateBannerReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotBlank(message = "banner名称不能为空")
    private String bannerName;
    @NotBlank(message = "banner类型不能为空")
    @CandidateValue(candidateValue = {"home", "banner"}, message = "banner类型只能是home|banner")
    private String positions;
    @NotBlank(message = "banner图片链接不能为空")
    private String imageUrl;
    @NotBlank(message = "banner跳转链接不能为空")
    private String jumpUrl;
    @NotBlank(message = "是否上架不能为空")
    @CandidateValue(candidateValue = {"Y", "N"}, message = "是否上架值只能是Y|N")
    private String isEnable = "N";
    private Integer sort = 0;
}
