package com.yfshop.admin.api.express.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExpressResult implements Serializable {
    String dateTime;
    String context;
}
