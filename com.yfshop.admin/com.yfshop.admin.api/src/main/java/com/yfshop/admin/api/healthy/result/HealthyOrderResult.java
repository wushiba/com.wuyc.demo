package com.yfshop.admin.api.healthy.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yfshop.common.util.DateUtil;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@ApiModel
@Data
public class HealthyOrderResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String orderNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime payTime;

    private String orderStatus;

    /**
     * 收货人姓名
     */
    private String contracts;

}
