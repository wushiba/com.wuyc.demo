package com.yfshop.wx.service;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import com.yfshop.wx.api.service.MpPayService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@DubboService
public class MpPayServiceImpl implements MpPayService {
    @Autowired
    private WxPayService wxPayService;

    @Override
    public WxPayMpOrderResult createPayOrder(WxPayUnifiedOrderRequest request) throws WxPayException {
        WxPayMpOrderResult wxPayMpOrderResult=wxPayService.createOrder(request);
        System.out.println(wxPayMpOrderResult.toString());

        WxPayMpOrderResult payResult = WxPayMpOrderResult.builder()
                .appId(wxPayMpOrderResult.getAppId())
                .timeStamp(wxPayMpOrderResult.getTimeStamp())
                .nonceStr(wxPayMpOrderResult.getNonceStr())
                .packageValue(wxPayMpOrderResult.getPackageValue())
                .signType(wxPayMpOrderResult.getSignType())
                .build();
        Map<String, String> params = SignUtils.xmlBean2Map(payResult);
        StringBuilder toSign = new StringBuilder();
        for (String key : new TreeMap<>(params).keySet()) {
            String value = params.get(key);
            if (StringUtils.isNotEmpty(value)){
                toSign.append(key).append("=").append(value).append("&");
            }
        }
        toSign.append("key=").append("F1887D3F9E6EE7A32FE5E76F4AB80D63");
        System.out.println(toSign.toString());
        System.out.println(SecureUtil.md5(toSign.toString()));

        return wxPayMpOrderResult;
    }


    public static void main(String agrs[]){
        // 此map用于参与调起sdk支付的二次签名,格式全小写，timestamp只能是10位,格式固定，切勿修改
        WxPayMpOrderResult payResult = WxPayMpOrderResult.builder()
                .appId("wxfe620617eec2f4b0")
                .timeStamp("1617872792")
                .nonceStr("QTDFd2XZoEQHxJYj")
                .packageValue("prepay_id=wx08170632632153fdbcd49c44bc1df70000")
                .signType("MD5")
                .build();
        System.out.println(SignUtils.createSign(payResult, "MD5", "F1887D3F9E6EE7A32FE5E76F4AB80D63", null));
         String a="appId=wxfe620617eec2f4b0&nonceStr=QTDFd2XZoEQHxJYj&package=prepay_id=wx08170632632153fdbcd49c44bc1df70000&signType=MD5&timeStamp=1617872792&key=F1887D3F9E6EE7A32FE5E76F4AB80D63";
        System.out.println(SecureUtil.md5(a));
    }

}
