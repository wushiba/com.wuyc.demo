package com.yfshop.admin.api.draw.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class QueryDrawActivityReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String isEnable;

    private String actTitle;

    private Integer pageIndex;

    private Integer pageSize;

}
