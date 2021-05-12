package com.yfshop.open.api.blpshop.request;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.Locale;

@Data
public class ApiReq implements Serializable {
    private String bizContent;
    private String method;
    private String sign;
    private String appKey;
    private String token;


    public boolean checkSign() {
        String date = "appKey" + appKey + "bizContent" + bizContent + "method" + method + "2c55f7fa26f04c959711c47ebc546bf8";
        String sign = SecureUtil.md5(date.toLowerCase(Locale.ROOT));
        return this.sign.equals(sign);
    }

    public <T> T getReq(Class<T> beanClass) {
        return JSONUtil.toBean(bizContent, beanClass);
    }
}
