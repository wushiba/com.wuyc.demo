package com.yfshop.shop.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class QueryDrawPrizeReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer actId;

    private String prizeTitle;

    private Integer prizeLevel;

}
