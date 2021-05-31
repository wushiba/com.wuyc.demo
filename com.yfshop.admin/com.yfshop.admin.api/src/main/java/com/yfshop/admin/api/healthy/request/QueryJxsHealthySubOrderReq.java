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
public class QueryJxsHealthySubOrderReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer pageIndex = 1;

    @Max(value = 100L, message = "每页最多只能展示{value}个")
    private Integer pageSize = 10;

    @NotNull(message = "商户ID不能为空")
    private Integer merchantId;

    @NotBlank(message = "订单状态不能为空")
    @CandidateValue(candidateValue = {"IN_CIRCULATION", "WAIT_DELIVERY"})
    private String orderStatus;
}
