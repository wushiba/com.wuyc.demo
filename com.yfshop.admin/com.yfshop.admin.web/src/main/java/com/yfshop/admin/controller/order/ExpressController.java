package com.yfshop.admin.controller.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sto.link.request.LinkRequest;
import com.sto.link.util.LinkUtils;
import com.yfshop.admin.api.express.result.ExpressResult;
import com.yfshop.admin.api.express.result.StoExpressResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("admin/express")
@Validated
public class ExpressController implements BaseController {

    @ApiOperation(value = "申通快递", httpMethod = "GET")
    @RequestMapping(value = "/stoTraceQuery", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<List<ExpressResult>> stoTraceQuery(String wayBillNo) {
        List<ExpressResult> expressResultList = new ArrayList<>();
        LinkRequest data = new LinkRequest();
        data.setFromAppkey("CAKoUWcvhIUBCVz");
        data.setFromCode("CAKoUWcvhIUBCVz");
        data.setToAppkey("sto_trace_query");
        data.setToCode("sto_trace_query");
        data.setApiName("STO_TRACE_QUERY_COMMON");
        data.setContent("{\"order\": \"desc\",\"waybillNoList\": [" + wayBillNo + "]}");
        String url = "https://cloudinter-linkgatewayonline.sto.cn/gateway/link.do";
        String secretKey = "CNHOUUv7PBH0IqRH2DQcdsKEGPqmLLZ6";
        String json = LinkUtils.request(data, url, secretKey);
        //System.out.println(json);
        StoExpressResult stoExpressResult = JSONUtil.toBean(json.startsWith("<response>") ? JSONUtil.xmlToJson(json).toString() : json, StoExpressResult.class);
        if (stoExpressResult.getSuccess().equals("true")) {
            JSONObject dataJson = JSONUtil.parseObj(stoExpressResult.getData());
            JSONArray jsonArray = dataJson.getJSONArray(wayBillNo);
            jsonArray.forEach(item -> {
                StoExpressResult.WaybillNoDTO sto=JSONUtil.toBean((JSONObject)item, StoExpressResult.WaybillNoDTO.class);
                ExpressResult expressResult = new ExpressResult();
                expressResult.setDateTime(sto.getOpTime());
                expressResult.setContext(sto.getMemo());
                expressResultList.add(expressResult);
            });
        }
        return CommonResult.success(expressResultList);
    }
}
