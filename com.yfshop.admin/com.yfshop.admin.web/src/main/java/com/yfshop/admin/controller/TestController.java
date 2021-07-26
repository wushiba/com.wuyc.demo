package com.yfshop.admin.controller;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yfshop.admin.controller.TestController.QunarAcquireGiftHelper2.AcquireGiftResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.exception.Asserts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Xulg
 * @since 2021-07-26 13:48
 * Description: 测试去哪儿领取会员接口
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping("/acquireGift")
    public CommonResult<Object> acquireGift(boolean isProProfile, String userPhone, String orderNo) {
        QunarAcquireGiftHelper2 qunarAcquireGiftHelper2 = new QunarAcquireGiftHelper2(isProProfile);
        AcquireGiftResult acquireGiftResult = qunarAcquireGiftHelper2.acquireGift(userPhone, orderNo);
        return CommonResult.success(acquireGiftResult);
    }

    public static class QunarAcquireGiftHelper2 {
        private static final Logger logger = LoggerFactory.getLogger(QunarAcquireGiftHelper2.class);

        private static final String URL = "/qstar/open.do";
        private static final String DOMAIN_UAT = "http://mpromotion.beta.qunar.com";
        private static final String DOMAIN_PRO = "https://mpromotion.qunar.com";
        private static final String PUB_KEY_UAT = "";
        private static final String PUB_KEY_PRO = "jjb^%oussftsdy";

        // "jujibao": {
        //     "strategyIds": [
        //     4098
        //     ],
        //     "isOpen": true,
        //             "number": 100000,
        //             "pubkey": "jjb^%oussftsdy",
        //             "qstarKey": "jjb^&^%out",
        //             "orderNoPre": "jjb",
        //             "channelName": "聚积宝供应商分销",
        //             "channelType": "external"

        private final boolean isProProfile;
        private final String requestUrl;
        private final String pubKey;

        public QunarAcquireGiftHelper2(boolean isProProfile) {
            this.isProProfile = isProProfile;
            requestUrl = (isProProfile ? DOMAIN_PRO : DOMAIN_UAT) + URL;
            pubKey = isProProfile ? PUB_KEY_PRO : PUB_KEY_UAT;
        }

        /**
         * 领取去哪儿礼包
         *
         * @param userPhone 用户手机号
         * @param orderNo   订单ID
         * @return the gift result
         */
        public AcquireGiftResult acquireGift(String userPhone, String orderNo) {
            Asserts.assertStringNotBlank(userPhone, 500, "用户手机号不能为空");
            Asserts.assertStringNotBlank(orderNo, 500, "订单号不能为空");
            Asserts.assertTrue(orderNo.length() <= 30, 500, "订单号长度不能超过30");
            return acquireGift(userPhone, null, null, null, "jujibao", orderNo);
        }

        @SuppressWarnings("SameParameterValue")
        AcquireGiftResult acquireGift(String userPhone, String username, String openId, String qUserId, String source, String orderNo) {
            boolean flag = StringUtils.isNotBlank(userPhone) || StringUtils.isNotBlank(username)
                    || StringUtils.isNotBlank(openId) || StringUtils.isNotBlank(qUserId);
            Asserts.assertTrue(flag, 500, "phone，username，openId，qUserId至少提供一个参数");
            Asserts.assertStringNotBlank(source, 500, "渠道来源不能为空");
            Asserts.assertStringNotBlank(orderNo, 500, "订单号不能为空");
            Asserts.assertTrue(orderNo.length() <= 30, 500, "订单号长度不能超过30");
            TreeMap<String, Object> formMap = new TreeMap<>();
            formMap.put("phone", userPhone);
            formMap.put("username", username);
            formMap.put("openId", openId);
            formMap.put("qUserId", qUserId);
            formMap.put("source", source);
            formMap.put("time", System.currentTimeMillis() / 1000);
            formMap.put("memberType", 0);
            formMap.put("orderNo", orderNo);
            formMap.put("sign", createSign(formMap));
            String body = HttpRequest.post(requestUrl).form(formMap).execute().body();
            logger.info("去哪儿会员开卡接口：地址{}参数{}响应结果{}", requestUrl, JSON.toJSONString(formMap), body);
            Asserts.assertStringNotBlank(body, 500, "去哪儿会员开卡接口调用失败");
            JSONObject jsonObject = JSON.parseObject(body);
            AcquireGiftResult acquireGiftResult = new AcquireGiftResult();
            acquireGiftResult.setBody(body);
            acquireGiftResult.setStatus(jsonObject.getInteger("status"));
            acquireGiftResult.setMessage(jsonObject.getString("message"));
            if (jsonObject.containsKey("data")) {
                JSONObject data = jsonObject.getJSONObject("data");
                acquireGiftResult.setDataCode(data.getInteger("code"));
                acquireGiftResult.setDataMessage(data.getString("message"));
                acquireGiftResult.setDataSuccessSubCode(data.getInteger("successSubCode"));
                acquireGiftResult.setDataStartDate(data.getDate("startDate"));
                acquireGiftResult.setDataEndDate(data.getDate("endDate"));
                acquireGiftResult.setDataSuccess(data.getBoolean("success"));
            }
            return acquireGiftResult;
        }

        private String createSign(TreeMap<String, Object> parameters) {
            StringBuilder sb = new StringBuilder();
            for (Entry<String, Object> entry : parameters.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value == null) {
                    continue;
                }
                if (value instanceof String && StringUtils.isEmpty((String) value)) {
                    continue;
                }
                sb.append(key).append("&").append(value);
            }
            sb.append(pubKey);
            return new MD5().digestHex(sb.toString());
        }

        /* ************************************************************************************************************** */

        public static class AcquireGiftResult implements Serializable {
            private static final long serialVersionUID = 1L;

        /*
        {
            "status":0,
            "message":null,
            "data":{
                "code":0,
                "message":"开通成功",
                "successSubCode":1,
                "startDate":"2019-02-26",
                "endDate":"2020-02-26",
                "success":true
            }
        }
        code取值如下：
            值	含义
             0	开通成功
            -1	开通失败，服务异常
            -3	开通失败，任务未配置或任务未开启或验签失败.
            -4	开通失败，请求失效（参数中的时间距离北京时间差距超过了5分钟）
            -5	开通失败，渠道售卖量达到最大上限
            -6	开通失败，机票会员不支持月卡体验
            -7	开通失败，用户已经体验过月卡，不支持月卡再次体验
            -8	开通失败，用户卡无法升级或无法续期
             5	用户可开卡数量达到最大限制
        successCode取值如下： 成功又区分为新开卡成功与续期成功
            值  含义
            1	新开卡用户
            2	续期成功用户
        */

            // 接口响应
            private String body;
            private Integer status;
            private String message;
            //  0	开通成功
            // -1	开通失败，服务异常
            // -3	开通失败，任务未配置或任务未开启或验签失败.
            // -4	开通失败，请求失效（参数中的时间距离北京时间差距超过了5分钟）
            // -5	开通失败，渠道售卖量达到最大上限
            // -6	开通失败，机票会员不支持月卡体验
            // -7	开通失败，用户已经体验过月卡，不支持月卡再次体验
            // -8	开通失败，用户卡无法升级或无法续期
            //  5	用户可开卡数量达到最大限制
            private Integer dataCode;
            private String dataMessage;
            // 1	新开卡用户
            // 2	续期成功用户
            private Integer dataSuccessSubCode;
            private Date dataStartDate;
            private Date dataEndDate;
            private Boolean dataSuccess;

            public String getBody() {
                return body;
            }

            public void setBody(String body) {
                this.body = body;
            }

            public Integer getStatus() {
                return status;
            }

            public void setStatus(Integer status) {
                this.status = status;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public Integer getDataCode() {
                return dataCode;
            }

            public void setDataCode(Integer dataCode) {
                this.dataCode = dataCode;
            }

            public String getDataMessage() {
                return dataMessage;
            }

            public void setDataMessage(String dataMessage) {
                this.dataMessage = dataMessage;
            }

            public Integer getDataSuccessSubCode() {
                return dataSuccessSubCode;
            }

            public void setDataSuccessSubCode(Integer dataSuccessSubCode) {
                this.dataSuccessSubCode = dataSuccessSubCode;
            }

            public Date getDataStartDate() {
                return dataStartDate;
            }

            public void setDataStartDate(Date dataStartDate) {
                this.dataStartDate = dataStartDate;
            }

            public Date getDataEndDate() {
                return dataEndDate;
            }

            public void setDataEndDate(Date dataEndDate) {
                this.dataEndDate = dataEndDate;
            }

            public Boolean getDataSuccess() {
                return dataSuccess;
            }

            public void setDataSuccess(Boolean dataSuccess) {
                this.dataSuccess = dataSuccess;
            }
        }

        /* ************************************************************************************************************** */

        public static void main(String[] args) {
            QunarAcquireGiftHelper2 helper2 = new QunarAcquireGiftHelper2(true);
            AcquireGiftResult acquireGiftResult = helper2.acquireGift("15268848628", "test_000000000001");
            System.out.println(JSON.toJSONString(acquireGiftResult, true));
        }
    }

}
