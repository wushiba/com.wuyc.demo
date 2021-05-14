package com.yfshop.shop.service.order;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sf.csim.express.service.CallExpressServiceTools;
import com.sf.csim.express.service.HttpClientUtil;
import com.sto.link.request.LinkRequest;
import com.sto.link.util.LinkUtils;
import com.yfshop.code.mapper.OrderDetailMapper;
import com.yfshop.code.model.OrderDetail;
import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.express.result.ExpressResult;
import com.yfshop.shop.service.express.result.SfExpressResult;
import com.yfshop.shop.service.express.result.StoExpressResult;
import com.yfshop.shop.service.order.service.FrontExpressService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.*;

@DubboService
public class FrontExpressServiceImpl implements FrontExpressService {
    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Override
    public List<ExpressResult> queryExpress(Long id) throws ApiException {
        OrderDetail orderDetail = orderDetailMapper.selectById(id);
        return queryExpressByWayBillNo(orderDetail.getExpressNo());
    }

    @Override
    public List<ExpressResult> queryExpressByWayBillNo(String wayBillNo) throws ApiException {
        if (StringUtils.isBlank(wayBillNo)) return new ArrayList<>();
        if (wayBillNo.startsWith("SF")) {
            return querySfExpress(wayBillNo);
        } else {
            return queryStExpress(wayBillNo);
        }
    }


    private List<ExpressResult> queryStExpress(String wayBillNo) {
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
                StoExpressResult.WaybillNoDTO sto = JSONUtil.toBean((JSONObject) item, StoExpressResult.WaybillNoDTO.class);
                ExpressResult expressResult = new ExpressResult();
                expressResult.setDateTime(sto.getOpTime());
                expressResult.setContext(sto.getMemo());
                expressResultList.add(expressResult);
            });
        }
        return expressResultList;
    }

    private List<ExpressResult> querySfExpress(String wayBillNo) {
        List<ExpressResult> expressResultList = new ArrayList<>();
        try {
            String url = "https://sfapi.sf-express.com/std/service";
            String msgData = "{\"language\": \"0\",\"trackingType\": \"1\",\"trackingNumber\": [" + wayBillNo + "],\"methodType\": \"1\"}";
            Map<String, String> params = new HashMap<>();
            params.put("partnerID", "JJBWLgX");  // 顾客编码 ，对应丰桥上获取的clientCode
            params.put("requestID", UUID.randomUUID().toString().replace("-", ""));
            params.put("serviceCode", "EXP_RECE_SEARCH_ROUTES");// 接口服务码
            params.put("timestamp", System.currentTimeMillis() + "");
            params.put("msgData", msgData);
            params.put("msgDigest", CallExpressServiceTools.getMsgDigest(msgData, System.currentTimeMillis() + "", "ntSULhd3ef4ObEwAh686uG21eXuwblYf"));//数据签名
            String result = HttpClientUtil.post(url, params);
            System.out.println(result);
            SfExpressResult sfExpressResult = JSONUtil.toBean(result, SfExpressResult.class);
            SfExpressResult.ResultData resultData = sfExpressResult.getApiResultData();
            if (resultData != null && resultData.getSuccess() && CollectionUtils.isNotEmpty(resultData.getMsgData().getRouteResps())) {
                resultData.getMsgData().getRouteResps().get(0).getRoutes().forEach(item -> {
                    ExpressResult expressResult = new ExpressResult();
                    expressResult.setContext(item.getRemark());
                    expressResult.setDateTime(item.getAcceptTime());
                    expressResultList.add(expressResult);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expressResultList;
    }
}