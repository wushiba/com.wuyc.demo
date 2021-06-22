package com.yfshop.admin.api.website.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class WebsiteCodeDataResult implements Serializable {
    private Integer merchantId;
    private String merchantName;
    private String alias;
    private String mobile;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime activityTime;
    private Integer currentExchange;
    private Integer totalExchange;
}
