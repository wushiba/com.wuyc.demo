package com.yfshop.admin.api.healthy.request;

import com.yfshop.common.validate.annotation.CandidateValue;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-05-31 11:15
 */
@Data
public class PostWayHealthySubOrderReq implements Serializable {
    private Long id;

    @NotNull(message = "商户ID不能为空")
    private Integer merchantId;

    @NotNull(message = "指派商户ID不能为空")
    private Integer currentMerchantId;
}
