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
    private String bizcontent;
    private String method;
    private String sign;
    private String appkey;
    private String token;


    public boolean checkSign() {
        String date = "2c55f7fa26f04c959711c47ebc546bf8appKey" + appkey + "bizcontent" + bizcontent + "method" + method+"token"+token + "2c55f7fa26f04c959711c47ebc546bf8";
        //System.out.println(date);
        String sign = SecureUtil.md5(date.toLowerCase());
       // System.out.println(sign);
        return this.sign.equals(sign);
    }

    public <T> T getReq(Class<T> beanClass) {
        return JSONUtil.toBean(bizcontent, beanClass);
    }
}
