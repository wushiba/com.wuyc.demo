package com.yfshop.shop.service.express.result;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class SfExpressResult {

    private String apiErrorMsg;
    private String apiResponseID;
    private String apiResultCode;
    private String apiResultData;


    public ResultData getApiResultData() {
        return JSONUtil.toBean(apiResultData, ResultData.class);
    }


    @NoArgsConstructor
    @Data
    public static class ResultData {
        private Boolean success;
        private String errorCode;
        private Object errorMsg;
        private MsgDataDTO msgData;

    }

    @NoArgsConstructor
    @Data
    public static class MsgDataDTO {
        private List<RouteRespsDTO> routeResps;
    }

    @NoArgsConstructor
    @Data
    public static class RouteRespsDTO {
        private String mailNo;
        private List<RoutesDTO> routes;

    }

    @NoArgsConstructor
    @Data
    public static class RoutesDTO {
        private String acceptAddress;
        private String acceptTime;
        private String remark;
        private String opCode;
    }
}
