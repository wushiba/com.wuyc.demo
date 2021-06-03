package com.yfshop.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * 聚合查询快递信息服务工具类
 *
 * @author Xulg
 * Created in 2019-04-17 13:47
 */
public class JuHeExpressDeliveryUtils {
    private static final Logger logger = LoggerFactory.getLogger(JuHeExpressDeliveryUtils.class);

    private static final String DEF_CHARSET = "UTF-8";

    private static final int DEF_CONN_TIMEOUT = 30000;

    private static final int DEF_READ_TIMEOUT = 30000;


    @SuppressWarnings("all")
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    /**
     * 快递信息接口地址
     */
    private static final String INFO_URL = "http://v.juhe.cn/exp/index";

    /**
     * 快递公司编号接口地址
     */
    private static final String COMPANY_URL = "http://v.juhe.cn/exp/com";

    private static final String APP_KEY = "34f005fe8c8c612f2d6c5c6507d1ab0c";

    public static void main(String[] args) {
//        JuHeExpressDeliveryCompanyResponse response1 = queryAllExpressDeliveryCompanyNumber();
//        System.out.println(JSON.toJSONString(response1, true));

        JuHeExpressDeliveryInfoResponse response2 = findExpressDeliveryInfo("sf",
                "SF1313722597010",
                "", "8377");
        System.out.println(JSON.toJSONString(response2, true));
    }

    /**
     * 根据快递单号查询快递信息
     *
     * @param expressDeliveryCompanyNumber 需要查询的快递公司编号(聚合接口提供)
     * @param expressDeliveryNumber        需要查询的快递单号
     * @param senderPhone                  寄件人手机号后四位，顺丰快递需要提供senderPhone和receiverPhone其中一个
     * @param receiverPhone                收件人手机号后四位，顺丰快递需要提供senderPhone和receiverPhone其中一个
     * @return the response
     */
    public static JuHeExpressDeliveryInfoResponse findExpressDeliveryInfo(String expressDeliveryCompanyNumber,
                                                                          String expressDeliveryNumber,
                                                                          String senderPhone,
                                                                          String receiverPhone) {
        if (StringUtils.isBlank(expressDeliveryCompanyNumber)) {
            throw new IllegalArgumentException("快递公司编号不能为空");
        }
        if (StringUtils.isBlank(expressDeliveryNumber)) {
            throw new IllegalArgumentException("快递单号不能为空");
        }
        // 顺丰快递需要提供senderPhone和receiverPhone其中一个
        if ("sf".equalsIgnoreCase(expressDeliveryCompanyNumber)) {
            if (StringUtils.isBlank(senderPhone)
                    && StringUtils.isBlank(receiverPhone)) {
                throw new IllegalArgumentException("顺丰快递必须提供" +
                        "寄件人手机号后四位或收件人手机号后四位");
            }
        }

        Map<String, Object> params = new HashMap<>(5);
        params.put("com", expressDeliveryCompanyNumber);
        params.put("no", expressDeliveryNumber);
        if (StringUtils.isNotBlank(senderPhone)) {
            params.put("senderPhone", senderPhone);
        }
        if (StringUtils.isNotBlank(receiverPhone)) {
            params.put("receiverPhone", receiverPhone);
        }
        params.put("key", APP_KEY);
        params.put("dtype", "json");

        // 请求接口
        String resultStr = requestForUrl(INFO_URL, params, "GET");
        JSONObject result = JSON.parseObject(resultStr);

        logger.info("聚合查询快递信息结果: " + resultStr);
        System.out.println("聚合查询快递信息结果: " + resultStr);

        // 封装数据
        JuHeExpressDeliveryInfoResponse response = new JuHeExpressDeliveryInfoResponse();
        Integer errorCode = result.getInteger("error_code");
        response.setErrorCode(errorCode);
        response.setResponseJsonString(resultStr);
        response.setResultCode(result.getInteger("resultcode"));
        response.setSuccess(Integer.valueOf(0).equals(errorCode));
        response.setReason(result.getString("reason"));
        if (response.getSuccess()) {
            String resultString = result.getString("result");
            JSONObject resultObject = JSON.parseObject(resultString);
            response.setResultString(resultString);
            response.setExpressDeliveryCompanyName(resultObject.getString("company"));
            response.setExpressDeliveryCompanyNumber(resultObject.getString("com"));
            response.setExpressDeliveryNumber(resultObject.getString("no"));
            response.setStatus(resultObject.getInteger("status"));
            List<JSONObject> list = JSON.parseArray(resultObject.getString("list"), JSONObject.class);
            if (list != null) {
                List<ExpressDeliveryTransferDetail> deliveryTransferDetailList = new ArrayList<>(list.size());
                for (JSONObject jsonObject : list) {
                    ExpressDeliveryTransferDetail detail = new ExpressDeliveryTransferDetail();
                    detail.setDatetime(jsonObject.getDate("datetime"));
                    detail.setRemark(jsonObject.getString("remark"));
                    detail.setZone(jsonObject.getString("zone"));
                    deliveryTransferDetailList.add(detail);
                }
                response.setList(deliveryTransferDetailList);
            }
        }
        return response;
    }

    /**
     * 查询快递公司编号信息
     */
    public static JuHeExpressDeliveryCompanyResponse queryAllExpressDeliveryCompanyNumber() {
        Map<String, Object> params = new HashMap<>(5);
        params.put("key", APP_KEY);

        // 请求接口
        String resultStr = requestForUrl(COMPANY_URL, params, "GET");
        JSONObject result = JSON.parseObject(resultStr);

        // 封装数据
        JuHeExpressDeliveryCompanyResponse response = new JuHeExpressDeliveryCompanyResponse();
        Integer errorCode = result.getInteger("error_code");
        response.setErrorCode(errorCode);
        response.setResponseJsonString(resultStr);
        response.setResultCode(result.getInteger("resultcode"));
        response.setSuccess(Integer.valueOf(0).equals(errorCode));
        response.setReason(result.getString("reason"));
        if (response.getSuccess()) {
            String resultString = result.getString("result");
            response.setResultString(resultString);
            List<JSONObject> list = JSON.parseArray(resultString, JSONObject.class);
            if (list != null) {
                List<JuHeExpressDeliveryCompanyDetail> companyDetailList = new ArrayList<>(list.size());
                for (JSONObject jsonObject : list) {
                    JuHeExpressDeliveryCompanyDetail detail = new JuHeExpressDeliveryCompanyDetail();
                    detail.setExpressDeliveryCompanyName(jsonObject.getString("com"));
                    detail.setExpressDeliveryCompanyNumber(jsonObject.getString("no"));
                    companyDetailList.add(detail);
                }
                response.setList(companyDetailList);
            }
        }
        return response;
    }

    /**
     * 发送请求
     *
     * @param strUrl the request url
     * @param params the request params
     * @param method the request method
     * @return the response string
     */
    @SuppressWarnings("all")
    private static String requestForUrl(String strUrl, Map<String, Object> params, String method) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (method == null || "GET".equals(method)) {
                strUrl = strUrl + "?" + urlEncode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (method == null || "GET".equals(method)) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", USER_AGENT);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params != null && "POST".equals(method)) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlEncode(params));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHARSET));
            for (String strRead; (strRead = reader.readLine()) != null; ) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    /**
     * 将map型转为请求参数型
     *
     * @param data the data
     * @return the params string
     */
    @SuppressWarnings("all")
    private static String urlEncode(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        try {
            for (Map.Entry entry : data.entrySet()) {
                sb.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue()
                                .toString(), "UTF-8"))
                        .append("&");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return sb.toString();
    }

    /**
     * 快递信息封装类
     */
    public static class JuHeExpressDeliveryInfoResponse implements Serializable {
        private static final long serialVersionUID = -6924118678311689028L;

        /**
         * 老版状态码
         */
        private Integer resultCode;

        /**
         * 返回说明
         */
        private String reason;

        /**
         * 返回码
         * 0为成功
         */
        private Integer errorCode;

        /**
         * 返回结果集
         */
        private String resultString;

        /**
         * 快递公司名字
         */
        private String expressDeliveryCompanyName;

        /**
         * 快递公司编号
         */
        private String expressDeliveryCompanyNumber;

        /**
         * 快递单号
         */
        private String expressDeliveryNumber;

        /**
         * 1表示此快递单的物流信息不会发生变化，
         * 此时您可缓存下来；
         * 0表示有变化的可能性
         */
        private Integer status;

        /**
         * 快递中转信息
         */
        private List<ExpressDeliveryTransferDetail> list;

        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 接口返回数据json字符串
         */
        private String responseJsonString;

        private JuHeExpressDeliveryInfoResponse() {
        }

        public Integer getResultCode() {
            return resultCode;
        }

        public void setResultCode(Integer resultCode) {
            this.resultCode = resultCode;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Integer getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(Integer errorCode) {
            this.errorCode = errorCode;
        }

        public String getResultString() {
            return resultString;
        }

        public void setResultString(String resultString) {
            this.resultString = resultString;
        }

        public String getExpressDeliveryCompanyName() {
            return expressDeliveryCompanyName;
        }

        public void setExpressDeliveryCompanyName(String expressDeliveryCompanyName) {
            this.expressDeliveryCompanyName = expressDeliveryCompanyName;
        }

        public String getExpressDeliveryCompanyNumber() {
            return expressDeliveryCompanyNumber;
        }

        public void setExpressDeliveryCompanyNumber(String expressDeliveryCompanyNumber) {
            this.expressDeliveryCompanyNumber = expressDeliveryCompanyNumber;
        }

        public String getExpressDeliveryNumber() {
            return expressDeliveryNumber;
        }

        public void setExpressDeliveryNumber(String expressDeliveryNumber) {
            this.expressDeliveryNumber = expressDeliveryNumber;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public List<ExpressDeliveryTransferDetail> getList() {
            return list;
        }

        public void setList(List<ExpressDeliveryTransferDetail> list) {
            this.list = list;
        }

        public String getResponseJsonString() {
            return responseJsonString;
        }

        public void setResponseJsonString(String responseJsonString) {
            this.responseJsonString = responseJsonString;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }
    }

    /**
     * 快递中转信息
     */
    public static class ExpressDeliveryTransferDetail implements Serializable {
        private static final long serialVersionUID = -5506558121628795638L;

        /**
         * 物流事件发生的时间
         */
        private Date datetime;

        /**
         * 物流事件的描述
         */
        private String remark;

        /**
         * 快件当时所在区域，由于快递公司升级，
         * 现大多数快递不提供此信息
         */
        private String zone;

        private ExpressDeliveryTransferDetail() {
        }

        public Date getDatetime() {
            return datetime;
        }

        public void setDatetime(Date datetime) {
            this.datetime = datetime;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }
    }

    /**
     * 快递公司编号信息封装
     */
    public static class JuHeExpressDeliveryCompanyResponse implements Serializable {
        private static final long serialVersionUID = -6894634768329882662L;

        /**
         * 老版状态码
         */
        private Integer resultCode;

        /**
         * 返回说明
         */
        private String reason;

        /**
         * 返回码
         * 0为成功
         */
        private Integer errorCode;

        /**
         * 返回结果集
         */
        private String resultString;

        /**
         * 快递公司信息
         */
        private List<JuHeExpressDeliveryCompanyDetail> list;

        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 接口返回数据json字符串
         */
        private String responseJsonString;

        private JuHeExpressDeliveryCompanyResponse() {
        }

        public Integer getResultCode() {
            return resultCode;
        }

        public void setResultCode(Integer resultCode) {
            this.resultCode = resultCode;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Integer getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(Integer errorCode) {
            this.errorCode = errorCode;
        }

        public String getResultString() {
            return resultString;
        }

        public void setResultString(String resultString) {
            this.resultString = resultString;
        }

        public List<JuHeExpressDeliveryCompanyDetail> getList() {
            return list;
        }

        public void setList(List<JuHeExpressDeliveryCompanyDetail> list) {
            this.list = list;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getResponseJsonString() {
            return responseJsonString;
        }

        public void setResponseJsonString(String responseJsonString) {
            this.responseJsonString = responseJsonString;
        }
    }

    public static class JuHeExpressDeliveryCompanyDetail implements Serializable {
        private static final long serialVersionUID = -1426768478432597618L;

        /**
         * 快递公司名字
         */
        private String expressDeliveryCompanyName;

        /**
         * 快递公司编号
         */
        private String expressDeliveryCompanyNumber;

        private JuHeExpressDeliveryCompanyDetail() {
        }

        public String getExpressDeliveryCompanyName() {
            return expressDeliveryCompanyName;
        }

        public void setExpressDeliveryCompanyName(String expressDeliveryCompanyName) {
            this.expressDeliveryCompanyName = expressDeliveryCompanyName;
        }

        public String getExpressDeliveryCompanyNumber() {
            return expressDeliveryCompanyNumber;
        }

        public void setExpressDeliveryCompanyNumber(String expressDeliveryCompanyNumber) {
            this.expressDeliveryCompanyNumber = expressDeliveryCompanyNumber;
        }
    }

/*
{
    "resultcode": "200",
    "reason": "查询物流信息成功",
    "result": {
        "company": "EMS",
        "com": "ems",
        "no": "1186465887499",
        "status": "1",
        "list": [
            {
                "datetime": "2016-06-15 21:44:04",
                "remark": "离开郴州市 发往长沙市【郴州市】",
                "zone": ""
            },
            {
                "datetime": "2016-06-15 21:46:45",
                "remark": "郴州市邮政速递物流公司国际快件监管中心已收件（揽投员姓名：侯云,联系电话:）【郴州市】",
                "zone": ""
            },
            {
                "datetime": "2016-06-16 12:04:00",
                "remark": "离开长沙市 发往贵阳市（经转）【长沙市】",
                "zone": ""
            },
            {
                "datetime": "2016-06-17 07:53:00",
                "remark": "到达贵阳市处理中心（经转）【贵阳市】",
                "zone": ""
            },
            {
                "datetime": "2016-06-18 07:40:00",
                "remark": "离开贵阳市 发往毕节地区（经转）【贵阳市】",
                "zone": ""
            },
            {
                "datetime": "2016-06-18 09:59:00",
                "remark": "离开贵阳市 发往下一城市（经转）【贵阳市】",
                "zone": ""
            },
            {
                "datetime": "2016-06-18 12:01:00",
                "remark": "到达  纳雍县 处理中心【毕节地区】",
                "zone": ""
            },
            {
                "datetime": "2016-06-18 17:34:00",
                "remark": "离开纳雍县 发往纳雍县阳长邮政支局【毕节地区】",
                "zone": ""
            },
            {
                "datetime": "2016-06-20 17:55:00",
                "remark": "投递并签收，签收人：单位收发章 *【毕节地区】",
                "zone": ""
            }
		]
    },
    "error_code": 0
}
*/

}
