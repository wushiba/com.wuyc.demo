package com.wuyc.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sp0313
 * @date 2023年07月16日 14:32:00
 */
public class TransReissueUtil {

//    public final static String MOBILE_URL = "127.0.0.1:8181/trans";
    public final static String MOBILE_URL = "https://mobile-consumer-uat.jetour.com.cn//web/car-service/test-drive/trans-reissue?access_token=4Uo8yzakSNo_WiO8TgIASAAAAAAAAAAC";

//    public final static String MOBILE_URL = "https://mobile-bff.jetour-sit.supaur.tech/web/pgc/comments/accounts/mine";

    public static void main(String[] args) throws Exception {
//        buildJsonObject("D://failInviteCode.txt");
//        buildJsonObject("D://failTransRecord.txt");
        buildJsonObject("D://uatFailTransRecord.txt");
    }

    public static void buildJsonObject(String path) throws Exception {
        List<String> lineList = FileUtil.readLines(path, "UTF-8");
        if (CollectionUtils.isEmpty(lineList)) {
            return;
        }

        List<String> failStrList = new ArrayList<>();
        lineList.forEach(data -> {
            if (data.length() > 50) {
                String[] split = data.split("testDriveReq");
                System.out.println(split[1].substring(13));
                failStrList.add(split[1].substring(13));
            }
        });
        System.out.println("亿客失败的条数=" + failStrList.size());

        failStrList.forEach(data -> {
            Map<String, Object> params = buildTestDriveReq(data);
//            String result = HttpRequest.get(MOBILE_URL)
//                    .form(params).execute().body();
            String result = HttpRequest.post(MOBILE_URL)
                    .body(JSON.toJSONString(params)).execute().body();
            System.out.println(result);
        });

    }

    private static Map<String, Object> buildTestDriveReq(String data) {
        data = data.replace("(", "");
        data = data.replace(")", "");
        String[] split = data.split(",");
        Map<String, Object> params = new HashMap<>(32);
        for (String param : split) {
            String[] paramArr = param.split("=");
            if (StringUtils.isEmpty(paramArr[1]) || "null".equals(paramArr[1].trim())) {
                params.put(paramArr[0].trim(), null);
            } else {
                params.put(paramArr[0].trim(), paramArr[1]);
            }
        }
        return params;
    }

}
