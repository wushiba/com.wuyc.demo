package com.yfshop.shop.service.healthy.req;

import com.yfshop.common.validate.annotation.CandidateValue;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-05-27 10:47
 */
@Data
public class QueryHealthyOrdersReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private Integer userId;

    /**
     * ALL          全部
     * SERVICING    服务中
     * COMPLETED    已完成
     */
    @CandidateValue(candidateValue = {"ALL", "SERVICING", "COMPLETED"}, message = "未知的订单状态查询")
    private String orderStatus = "ALL";
}
