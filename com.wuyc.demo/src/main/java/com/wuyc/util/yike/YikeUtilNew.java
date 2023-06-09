package com.wuyc.util.yike;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.wuyc.util.yike.vo.QueryYikeStatusRes;
import com.wuyc.util.yike.vo.YikeResponse;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sp0313
 * @date 2023年05月29日 09:27:00
 */
public class YikeUtilNew {

    public final static String YIKE_NEW_APP_KEY = "superA-turn";
    public final static String YIKE_NEW_APP_SECRET = "b833bbabf3534f41b3e262f28dd96380";

    public final static String YIKE_HOST_URL = "https://digi-sit.jetour.com.cn/yike-open-api";

    // 推送绑定关系URL
    public final static String YIKE_PUSH_BIND_RELATION_PATH = "/transferIntroduction/sendUpInvite";

    // 查询绑定关系邀请状态
    public final static String YIKE_QUERY_BIND_RELATION_PATH = "/transferIntroduction/queryInviteBind?outId=";

    // 批量查询绑定关系邀请状态
    public final static String YIKE_BATCH_QUERY_BIND_RELATION_PATH = "/transferIntroduction/batchQueryInviteBind?outIdList=";


    public static void main(String[] args) throws Exception {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("outId", "12341113");
        bodyMap.put("inviterName", "邀请人姓名004");
        bodyMap.put("inviterPhone", "15800009901");
        bodyMap.put("name", "被邀请人04");
        bodyMap.put("phone", "13800000001");
        bodyMap.put("erpCode", "17300");
        bodyMap.put("seriesName", "新X70S");
        bodyMap.put("seriesId", "jt-series-14");
//        pushBindRelation(bodyMap);
//        queryBindRelation("12341111");
        batchQueryBindRelation("12341111,12341112,12341113");
    }

    public static void queryBindRelation(String outId) {
//        Map<String, String> headerMap = initHeaderMap();
//        String body = HttpRequest.get(YIKE_HOST_URL + YIKE_QUERY_BIND_RELATION_PATH + outId)
//                .addHeaders(headerMap)
//                .execute().body();
//        System.out.println(body);

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headerMap = new HttpHeaders();
        String timestamp = System.currentTimeMillis() + "";
        headerMap.add("appKey", YIKE_NEW_APP_KEY);
        headerMap.add("timestamp", timestamp);
        headerMap.add("sign", setSign(timestamp));
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                YIKE_HOST_URL + YIKE_QUERY_BIND_RELATION_PATH + outId,
                HttpMethod.GET, new HttpEntity(null, headerMap), JSONObject.class);
        System.out.println(JSON.toJSONString(responseEntity));
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode.value() != 200) {
            // 抛异常
            return;
        }

        YikeResponse<QueryYikeStatusRes> yikeResponse = JSON.parseObject(responseEntity.getBody().toJSONString(),
                new TypeReference<YikeResponse<QueryYikeStatusRes>>() {
                });
        System.out.println(JSON.toJSONString(yikeResponse));
    }

    public static void batchQueryBindRelation(String outIds) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headerMap = new HttpHeaders();
        String timestamp = System.currentTimeMillis() + "";
        headerMap.add("appKey", YIKE_NEW_APP_KEY);
        headerMap.add("timestamp", timestamp);
        headerMap.add("sign", setSign(timestamp));
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                YIKE_HOST_URL + YIKE_BATCH_QUERY_BIND_RELATION_PATH + outIds,
                HttpMethod.GET, new HttpEntity(null, headerMap), JSONObject.class);
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode.value() != 200) {
            // 抛异常
            return;
        }

        YikeResponse<List<QueryYikeStatusRes>> yikeResponse = JSON.parseObject(responseEntity.getBody().toJSONString(),
                new TypeReference<YikeResponse<List<QueryYikeStatusRes>>>() {
                });
        System.out.println(JSON.toJSONString(yikeResponse));
    }


    public static void pushBindRelation(Map<String, String> bodyMap) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        // 设置响应的返回类型
        httpHeaders.add("Accept", MediaType.APPLICATION_JSON.toString());
        String timestamp = System.currentTimeMillis() + "";
        httpHeaders.add("appKey", YIKE_NEW_APP_KEY);
        httpHeaders.add("timestamp", timestamp);
        httpHeaders.add("sign", setSign(timestamp));

        // 构建表单数据
        HttpEntity<String> formEntity = new HttpEntity<>(JSONObject.toJSONString(bodyMap), httpHeaders);
        JSONObject jsonObject = restTemplate.postForObject(YIKE_HOST_URL + YIKE_PUSH_BIND_RELATION_PATH, formEntity, JSONObject.class);
        System.out.println(jsonObject);

//        Map<String, String> headerMap = initHeaderMap();
//        String body = HttpRequest.post(YIKE_HOST_URL + YIKE_PUSH_BIND_RELATION_PATH)
//                .addHeaders(headerMap)
//                .body(JSON.toJSONString(bodyMap)).execute().body();
//        System.out.println(body);
    }


    private static String setSign(String timestamp) {
        return SecureUtil.md5(YIKE_NEW_APP_KEY + "|" + timestamp + "|" + YIKE_NEW_APP_SECRET);
    }

}
