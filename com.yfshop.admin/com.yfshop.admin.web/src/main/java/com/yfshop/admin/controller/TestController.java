package com.yfshop.admin.controller;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yfshop.admin.controller.TestController.HaagenDazsHelper.SendTicketReq;
import com.yfshop.admin.controller.TestController.HaagenDazsHelper.SendTicketResult;
import com.yfshop.admin.controller.TestController.QunarAcquireGiftHelper2.AcquireGiftResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.exception.Asserts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * @author Xulg
 * @since 2021-07-26 13:48
 * Description: 测试去哪儿领取会员接口
 */
// @Profile("uat")
// @Controller
// @RequestMapping("/test")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("/acquireGift")
    @ResponseBody
    public CommonResult<Object> acquireGift(boolean isProProfile, String userPhone, String orderNo) {
        QunarAcquireGiftHelper2 qunarAcquireGiftHelper2 = new QunarAcquireGiftHelper2(isProProfile);
        AcquireGiftResult acquireGiftResult = qunarAcquireGiftHelper2.acquireGift(userPhone, orderNo);
        return CommonResult.success(acquireGiftResult);
    }

    @RequestMapping("/acquireHGDS/{isProProfile}")
    @ResponseBody
    public CommonResult<Object> acquireHGDS(@PathVariable boolean isProProfile, @RequestBody SendTicketReq req) {
        HaagenDazsHelper helper = new HaagenDazsHelper(isProProfile);
        req.setReturnUrl("https://prev-upms.yufanlook.com/test/callbackHGDS");
        SendTicketResult sendTicketResult = helper.sendTicket(req);
        return CommonResult.success(sendTicketResult);
    }

    @RequestMapping("/callbackHGDS")
    @ResponseBody
    public CommonResult<Object> callbackHGDS(HttpServletRequest request) {
        Object handleCallback = HaagenDazsHelper.handleCallback(request, callbackData -> {
            logger.info("哈根达斯回调啊啊啊啊啊啊啊啊啊啊啊啊啊======\r\n" + JSON.toJSONString(callbackData, true));
            return callbackData;
        });
        return CommonResult.success(handleCallback);
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
                sb.append(key).append("=").append(value).append("&");
            }
            String s = StringUtils.removeEnd(sb.toString(), "&") + pubKey;
            return new MD5().digestHex(s);
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

    public static class HaagenDazsHelper {
        private static final Logger LOGGER = LoggerFactory.getLogger(HaagenDazsHelper.class);

        private static final Pattern MOBILE_PATTERN = Pattern.compile("(?:0|86|\\+86)?1[3456789]\\d{9}");
        private static final List<String> IGNORE_KEYS = Collections.unmodifiableList(Arrays.asList("sign", "data"));
        private static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmssSSS";

        // API系统参数 测试的
        // Key: a7d228be783344ad
        // Secret: 7eb0b604463d45f7ba991e628d8024dd
        // private static final String APP_KEY_UAT = "a7d228be783344ad";
        private static final String APP_KEY_UAT = "fcd83a1dc51b4411";
        // private static final String APP_SECRET_UAT = "7eb0b604463d45f7ba991e628d8024dd";
        private static final String APP_SECRET_UAT = "2d9d3d53e808447f9588037a0dfea295";
        private static final String DOMAIN_UAT = "https://hdt-pre.hadatong.com/haagendazs/api/";

        // API系统参数
        // Key: 2aea744535ca4063
        // Secret: aacf036e8ec946ea93fd38046a2527a1
        private static final String APP_KEY_PRO = "2aea744535ca4063";
        private static final String APP_SECRET_PRO = "aacf036e8ec946ea93fd38046a2527a1";
        private static final String DOMAIN_PRO = "https://www.hadatong.com/haagendazs/api";

        private final boolean isProProfile;
        private final String appKey;
        private final String appSecret;
        private final String url;

        public HaagenDazsHelper(boolean isProProfile) {
            this.isProProfile = isProProfile;
            appKey = isProProfile ? APP_KEY_PRO : APP_KEY_UAT;
            url = isProProfile ? DOMAIN_PRO : DOMAIN_UAT;
            appSecret = isProProfile ? APP_SECRET_PRO : APP_SECRET_UAT;
        }

        public SendTicketResult sendTicket(SendTicketReq req) {
            String orderSn = req.orderSn;
            String returnUrl = req.returnUrl;
            List<PhoneInfo> list = req.list;
            Asserts.assertStringNotBlank(orderSn, 500, "订单ID不能为空");
            Asserts.assertTrue(orderSn.length() >= 1 && orderSn.length() <= 64,
                    500, "订单ID长度非法");
            Asserts.assertTrue(list != null && !list.isEmpty(),
                    500, "发送短信手机号及数量不能为空");
            for (PhoneInfo phoneInfo : list) {
                Asserts.assertTrue(MOBILE_PATTERN.matcher(phoneInfo.phone).matches(),
                        500, "非法的手机号" + phoneInfo.phone);
                Asserts.assertTrue(phoneInfo.sendAmount >= 1 && phoneInfo.sendAmount <= 100,
                        500, "非法的发送数量" + phoneInfo.sendAmount);
            }
            Asserts.assertStringNotBlank(returnUrl, 500, "回调地址不能为空");

            Map<String, Object> data = new HashMap<>(5);
            data.put("orderSn", orderSn);
            data.put("returnUrl", returnUrl);
            data.put("list", list);
            TreeMap<String, Object> formMap = buildRequestParameters("card.send", data);
            String body = HttpRequest.post(url).form(formMap).execute().body();
            LOGGER.info("哈根达斯发券接口：地址{}参数{}响应结果{}", url, JSON.toJSONString(formMap), body);
            //{"code":200,"data":"success","msg":"调用成功","sessionId":"","stack":""}
            JSONObject jsonObject = JSON.parseObject(body);
            SendTicketResult sendTicketResult = new SendTicketResult();
            sendTicketResult.setBody(body);
            sendTicketResult.setCode(jsonObject.getInteger("code"));
            sendTicketResult.setMsg(jsonObject.getString("msg"));
            sendTicketResult.setStack(jsonObject.getString("stack"));
            sendTicketResult.setData(jsonObject.getString("data"));
            return sendTicketResult;
        }

        public static Object handleCallback(HttpServletRequest request, MyFunction<CallbackData, Object> callback) {
            String data = request.getParameterMap().keySet().iterator().next();
            LOGGER.info("哈根达斯的回调的数据啊啊啊啊啊" + data);
            CallbackData callbackData = JSON.parseObject(data, CallbackData.class);
            return callback.apply(callbackData);
        }

        public FindOrderResult findOrder(String orderSn) {
            Asserts.assertStringNotBlank(orderSn, 500, "订单ID不能为空");
            Asserts.assertTrue(orderSn.length() >= 1 && orderSn.length() <= 64,
                    500, "订单ID长度非法");

            Map<String, Object> data = new HashMap<>(3);
            data.put("orderSn", orderSn);
            TreeMap<String, Object> formMap = buildRequestParameters("card.total", data);
            String body = HttpRequest.post(url).form(formMap).execute().body();
            LOGGER.info("哈根达斯查询订单卡券总数与剩余数量接口：地址{}参数{}响应结果{}", url, JSON.toJSONString(formMap), body);
            JSONObject jsonObject = JSON.parseObject(body);

            FindOrderResult findOrderResult = new FindOrderResult();
            findOrderResult.setBody(body);
            findOrderResult.setCode(jsonObject.getInteger("code"));
            findOrderResult.setMsg(jsonObject.getString("msg"));
            findOrderResult.setStack(jsonObject.getString("stack"));
            findOrderResult.setData(jsonObject.getString("data"));
            if (jsonObject.containsKey("data")) {
                JSONObject responseData = jsonObject.getJSONObject("data");
                findOrderResult.setOrderSn(responseData.getString("orderSn"));
                findOrderResult.setQueryTime(responseData.getDate("queryTime"));
                findOrderResult.setGoodsName(responseData.getString("goodsName"));
                findOrderResult.setTotal(responseData.getInteger("total"));
                findOrderResult.setAvl(responseData.getInteger("avl"));
            }
            return findOrderResult;
        }

        private List<CardDetail> getCardDetails(JSONObject responseData, String key) {
            List<CardDetail> list = new ArrayList<>();
            JSONArray success = responseData.getJSONArray(key);
            if (success != null && success.size() > 0) {
                for (Object o : success) {
                    JSONObject successInfo = (JSONObject) o;
                    CardDetail cardDetail = new CardDetail();
                    cardDetail.setPhone(successInfo.getString("phone"));
                    cardDetail.setCardNo(successInfo.getString("cardNo"));
                    cardDetail.setMsg(successInfo.getString("msg"));
                    list.add(cardDetail);
                }
            }
            return list;
        }

        private TreeMap<String, Object> buildRequestParameters(String method, Map<String, Object> data) {
            TreeMap<String, Object> map = new TreeMap<>();
            map.put("method", method);
            map.put("appkey", appKey);
            map.put("timestamp", new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date()));
            map.put("format", "json");
            map.put("version", "1.0");
            map.put("data", JSON.toJSONString(data));
            map.put("sign", createSign(map));
            return map;
        }

        private String createSign(TreeMap<String, Object> parameters) {
            StringBuilder sb = new StringBuilder();
            for (Entry<String, Object> entry : parameters.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (IGNORE_KEYS.contains(key) || value == null) {
                    continue;
                }
                sb.append(key).append("=").append(value).append("&");
            }
            MD5 md5 = new MD5();
            String digest = md5.digestHex(StringUtils.removeEnd(sb.toString(), "&")).toUpperCase();
            return md5.digestHex(digest + appSecret).toUpperCase();
        }

        public static class SendTicketReq implements Serializable {
            private static final long serialVersionUID = 1L;
            // 订单号（可登录 B 端后
            // 台查询或联系我司获
            // 取）
            private String orderSn;
            // 通知地址
            private String returnUrl;
            // 发送短信手机号及数量
            private List<PhoneInfo> list;

            public String getOrderSn() {
                return orderSn;
            }

            public void setOrderSn(String orderSn) {
                this.orderSn = orderSn;
            }

            public String getReturnUrl() {
                return returnUrl;
            }

            public void setReturnUrl(String returnUrl) {
                this.returnUrl = returnUrl;
            }

            public List<PhoneInfo> getList() {
                return list;
            }

            public void setList(List<PhoneInfo> list) {
                this.list = list;
            }

            public void addPhoneInfo(String phone, Integer sendAmount) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(new PhoneInfo(phone, sendAmount));
            }
        }

        public static class PhoneInfo implements Serializable {
            private static final long serialVersionUID = 1L;
            // 用户手机号
            private String phone;
            // 数量
            private Integer sendAmount;

            public PhoneInfo() {}

            public PhoneInfo(String phone, Integer sendAmount) {
                this.sendAmount = sendAmount;
                this.phone = phone;
            }

            public Integer getSendAmount() {
                return sendAmount;
            }

            public void setSendAmount(Integer sendAmount) {
                this.sendAmount = sendAmount;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }
        }

        private static abstract class BaseResult implements Serializable {
            private static final long serialVersionUID = 1L;
            private String body;
            // 状态码 200：成功 ；500 ：失败
            private Integer code;
            // 响应描述
            private String msg;
            // 异常栈信息
            private String stack;
            // 每个接口特有的参数，详见每个接口定义
            private String data;

            public String getBody() {
                return body;
            }

            public void setBody(String body) {
                this.body = body;
            }

            public Integer getCode() {
                return code;
            }

            public void setCode(Integer code) {
                this.code = code;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public String getStack() {
                return stack;
            }

            public void setStack(String stack) {
                this.stack = stack;
            }

            public String getData() {
                return data;
            }

            public void setData(String data) {
                this.data = data;
            }
        }

        public static class SendTicketResult extends BaseResult implements Serializable {
            private static final long serialVersionUID = 1L;
            // 订单号
            // private String orderSn;
            // 成功信息列表
            // private List<CardDetail> success;
            // 失败信息列表
            // private List<CardDetail> fail;
        }

        public static class CardDetail implements Serializable {
            private static final long serialVersionUID = 1L;
            // 手机号
            private String phone;
            // 卡券号
            private String cardNo;
            // 描述
            private String msg;
            private Boolean isSuccess;

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getCardNo() {
                return cardNo;
            }

            public void setCardNo(String cardNo) {
                this.cardNo = cardNo;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public Boolean getIsSuccess() {
                return isSuccess;
            }

            public void setIsSuccess(Boolean success) {
                isSuccess = success;
            }
        }

        public static class FindOrderResult extends BaseResult implements Serializable {
            private static final long serialVersionUID = 1L;
            // 订单号
            private String orderSn;
            // 查询时间
            private Date queryTime;
            // 商品名
            private String goodsName;
            // 总数量
            private Integer total;
            // 剩余数量
            private Integer avl;

            public String getOrderSn() {
                return orderSn;
            }

            public void setOrderSn(String orderSn) {
                this.orderSn = orderSn;
            }

            public Date getQueryTime() {
                return queryTime;
            }

            public void setQueryTime(Date queryTime) {
                this.queryTime = queryTime;
            }

            public String getGoodsName() {
                return goodsName;
            }

            public void setGoodsName(String goodsName) {
                this.goodsName = goodsName;
            }

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

            public Integer getAvl() {
                return avl;
            }

            public void setAvl(Integer avl) {
                this.avl = avl;
            }
        }

        public static class CallbackData implements Serializable {
            private static final long serialVersionUID = 1L;
            private String orderSn;
            private List<CardDetail> success;
            private List<CardDetail> faild;

            public String getOrderSn() {
                return orderSn;
            }

            public void setOrderSn(String orderSn) {
                this.orderSn = orderSn;
            }

            public List<CardDetail> getSuccess() {
                return success;
            }

            public void setSuccess(List<CardDetail> success) {
                this.success = success;
            }

            public List<CardDetail> getFaild() {
                return faild;
            }

            public void setFaild(List<CardDetail> faild) {
                this.faild = faild;
            }
        }

        public interface MyFunction<T, R> {
            R apply(T t);
        }

        public static void main(String[] args) {
            if (true) {
                String json = "{\n" +
                        " \"faild\": [],\n" +
                        " \"orderSn\": \"TO18321318968\",\n" +
                        " \"success\": [\n" +
                        " {\n" +
                        " \"cardNo\": \"f4d5b805cd5311e9bb8500ff731e544e\",\n" +
                        " \"msg\": \"成功\",\n" +
                        " \"phone\": \"18825019768\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"cardNo\": \"f4d94ec5cd5311e9bb8500ff731e544e\",\n" +
                        " \"msg\": \"成功\",\n" +
                        " \"phone\": \"13711122205\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"cardNo\": \"f4db63b4cd5311e9bb8500ff731e544e\",\n" +
                        " \"msg\": \"成功\",\n" +
                        " \"phone\": \"13711122205\"\n" +
                        " }\n" +
                        " ]\n" +
                        "}";
                CallbackData callbackData = JSON.parseObject(json, CallbackData.class);
                System.out.println(callbackData);
            }

            HaagenDazsHelper helper = new HaagenDazsHelper(false);

            SendTicketReq req = new SendTicketReq();
            req.setOrderSn("TO1816194366420210722185");
            req.setReturnUrl(null);
            req.setList(Arrays.asList(new PhoneInfo("15268848628", 1)));
            SendTicketResult sendTicketResult = helper.sendTicket(req);
            System.out.println(JSON.toJSONString(sendTicketResult, true));

            FindOrderResult findOrderResult = helper.findOrder(null);
            System.out.println(JSON.toJSONString(findOrderResult, true));
            findOrderResult = helper.findOrder(req.getOrderSn());
            System.out.println(JSON.toJSONString(findOrderResult, true));
        }
    }

}
