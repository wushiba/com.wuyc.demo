package com.yfshop.admin.service.express;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.sf.csim.express.service.CallExpressServiceTools;
import com.sf.csim.express.service.HttpClientUtil;
import com.sto.link.request.LinkRequest;
import com.sto.link.util.LinkUtils;
import com.yfshop.admin.api.express.ExpressService;
import com.yfshop.admin.api.express.result.ExpressOrderResult;
import com.yfshop.admin.api.express.result.ExpressResult;
import com.yfshop.admin.api.express.result.SfExpressResult;
import com.yfshop.admin.api.express.result.StoExpressResult;
import com.yfshop.code.mapper.ExpressMapper;
import com.yfshop.code.mapper.OrderDetailMapper;
import com.yfshop.code.model.Express;
import com.yfshop.code.model.OrderDetail;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.JuHeExpressDeliveryUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@DubboService
public class ExpressServiceImpl implements ExpressService {
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private ExpressMapper expressMapper;
    Map<String, String> map = new HashMap<>();

    @PostConstruct
    public void init() {
        map.put("中通", "zto");
        map.put("韵达", "yd");
        map.put("圆通", "yt");
        map.put("申通", "sto");
        map.put("顺丰", "sf");
    }


    @Override
    public ExpressOrderResult queryExpress(Long id) throws ApiException {
        ExpressOrderResult expressOrderResult = new ExpressOrderResult();
        OrderDetail orderDetail = orderDetailMapper.selectById(id);
        List<ExpressResult> list = queryStExpress(orderDetail.getExpressNo());
        expressOrderResult.setExpressNo(orderDetail.getExpressNo());
        expressOrderResult.setExpressName(orderDetail.getExpressCompany());
        expressOrderResult.setList(list);
        return expressOrderResult;
    }

    @Override
    public ExpressOrderResult queryByExpressNo(String expressNo, String expressName, String receiverMobile) throws ApiException {
        ExpressOrderResult expressOrderResult = new ExpressOrderResult();
        expressOrderResult.setExpressName(expressName);
        expressOrderResult.setExpressNo(expressNo);
        String value = map.get(expressName);
        if (value == null) {
            expressOrderResult.setList(new ArrayList<>());
        }
        if ("sto".equals(value)) {
            expressOrderResult.setList(queryStExpress(expressNo));
        } else {
            receiverMobile = receiverMobile.substring(receiverMobile.length() - 4);
            expressOrderResult.setList(queryCommExpress(value, expressNo, receiverMobile));
        }
        return expressOrderResult;
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
            if (jsonArray != null) {
                jsonArray.forEach(item -> {
                    StoExpressResult.WaybillNoDTO sto = JSONUtil.toBean((JSONObject) item, StoExpressResult.WaybillNoDTO.class);
                    ExpressResult expressResult = new ExpressResult();
                    expressResult.setDateTime(sto.getOpTime());
                    expressResult.setContext(sto.getMemo());
                    expressResultList.add(expressResult);
                });
            }
        }
        return expressResultList;
    }

    private List<ExpressResult> queryCommExpress(String expressDeliveryCompanyNumber,
                                                 String expressDeliveryNumber,
                                                 String receiverPhone) {
        String no = expressDeliveryCompanyNumber + "_" + expressDeliveryNumber;
        Express express = expressMapper.selectOne(Wrappers.lambdaQuery(Express.class).eq(Express::getExpressNo, no));
        if (express != null) {
            return JSON.parseArray(express.getDatajson(), ExpressResult.class);
        }
        String key = CacheConstants.EXPRESS_KEY_PREFIX + no;
        Object expressListObject = redisService.get(key);
        if (expressListObject != null) {
            redisService.expire(key, 60 * 60 * 6);
            return JSON.parseArray(expressListObject.toString(), ExpressResult.class);
        }
        List<ExpressResult> expressResultList = new ArrayList<>();
        try {
            JuHeExpressDeliveryUtils.JuHeExpressDeliveryInfoResponse juHeExpressDeliveryInfoResponse = JuHeExpressDeliveryUtils.findExpressDeliveryInfo(expressDeliveryCompanyNumber, expressDeliveryNumber, "", receiverPhone);
            AtomicBoolean isSuccess = new AtomicBoolean(false);
            if (juHeExpressDeliveryInfoResponse.getSuccess()) {
                Lists.reverse(juHeExpressDeliveryInfoResponse.getList()).forEach(item -> {
                    ExpressResult expressResult = new ExpressResult();
                    expressResult.setDateTime(DateUtil.format(item.getDatetime(), "yyyy-MM-dd HH:mm:SS"));
                    expressResult.setContext(item.getRemark());
                    if (item.getRemark().contains("签收")) {
                        isSuccess.set(true);
                    }
                    expressResultList.add(expressResult);
                });
            }
            redisService.set(key, JSON.toJSONString(expressResultList), 60 * 60 * 6);
            if (isSuccess.get()) {
                express = new Express();
                express.setExpressNo(no);
                express.setDatajson(JSON.toJSONString(expressResultList));
                expressMapper.insert(express);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expressResultList;
    }


    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("中通", "zto");
        map.put("韵达", "yd");
        map.put("圆通", "yt");
        map.put("申通", "sto");
        map.put("顺丰", "sf");
        List<String> list = FileUtil.readUtf8Lines(new File("H://1.txt"));
        List<String> tag = new ArrayList<>();
        list.forEach(item -> {
            String[] s = item.split(",");
            String value = map.get(s[0]);
            if (value == null) {
                tag.add(item + ",未知");
                return;
            }
            AtomicReference<String> status = new AtomicReference<>(",未送达");
            try {
                JuHeExpressDeliveryUtils.JuHeExpressDeliveryInfoResponse juHeExpressDeliveryInfoResponse = JuHeExpressDeliveryUtils.findExpressDeliveryInfo(value, s[1], "", StringUtils.isEmpty(s[4]) ? "" : s[4].substring(s[4].length() - 4));
                if (juHeExpressDeliveryInfoResponse.getSuccess()) {
                    Lists.reverse(juHeExpressDeliveryInfoResponse.getList()).forEach(i -> {
                        if (i.getRemark().contains("签收")) {
                            status.set(",已签收");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            tag.add(item + status.get());
        });
        FileUtil.writeLines(tag, new File("H://2.txt"), "UTF-8");
    }
}
