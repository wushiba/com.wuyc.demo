package com.yfshop.admin.api.express.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ExpressOrderResult implements Serializable {
    private String expressName;
    private String expressNo;
    private List<ExpressResult> list;
}
