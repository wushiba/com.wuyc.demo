package com.yfshop.admin.api.merchant.request;

import com.yfshop.common.util.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Xulg
 * Created in 2021-03-25 14:00
 */
@ApiModel
@Data
public class QueryGoodsRecordReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date startTime;
    private Date endTime;
    private Integer merchantId;

    public Date getEndTime() {
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }
}
