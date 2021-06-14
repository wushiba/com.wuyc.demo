package com.yfshop.admin.service.order;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sto.link.request.LinkRequest;
import com.sto.link.util.LinkUtils;
import com.yfshop.admin.api.order.request.StOrderReq;
import com.yfshop.admin.api.order.request.WuYouOrderReq;
import com.yfshop.admin.api.order.result.StOrderResult;
import com.yfshop.admin.api.order.service.StOrderService;
import com.yfshop.code.mapper.OrderAddressMapper;
import com.yfshop.code.mapper.OrderDetailMapper;
import com.yfshop.code.mapper.OrderMapper;
import com.yfshop.code.model.Order;
import com.yfshop.code.model.OrderAddress;
import com.yfshop.code.model.OrderDetail;
import com.yfshop.common.enums.UserOrderStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@DubboService
public class StOrderServiceImpl implements StOrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderAddressMapper orderAddressMapper;

    private static final Logger logger = LoggerFactory.getLogger(StOrderServiceImpl.class);


    @Override
    @Async
    public void pushStOrder(Long orderId, Long childOrderId) {

        /**
         * 目前只发这些
         */
        OrderDetail orderDetail = orderDetailMapper.selectById(childOrderId);
        if (orderDetail == null) {
            logger.info("订单未查询到->{}",childOrderId);
            return;
        }
        if (UserOrderStatusEnum.WAIT_DELIVERY.getCode().equals(orderDetail.getOrderStatus())) {
            logger.info("订单状态不对->{}",childOrderId);
            return;
        }
        OrderAddress orderAddress = orderAddressMapper.selectOne(Wrappers.lambdaQuery(OrderAddress.class).eq(OrderAddress::getOrderId, orderId));
        if (orderAddress == null) {
            logger.info("订单地址不存在->{}",childOrderId);
            return;
        }
        String url = "https://cloudinter-linkgatewayonline.sto.cn/gateway/link.do";
        String secretKey = "Omj0YY5P29cAvWhddNukhdxwxL4S1b4x";
        LinkRequest wuyou = new LinkRequest();
        wuyou.setFromAppkey("CAKFvLQuMfpsbGZ");
        wuyou.setFromCode("CAKFvLQuMfpsbGZ");
        wuyou.setToAppkey("sto_merchant");
        wuyou.setToCode("sto_merchant_code");
        wuyou.setApiName("ADD_WUYOU_ORDER");
        WuYouOrderReq wuYouOrderReq = new WuYouOrderReq();
        wuYouOrderReq.setPlatformOrderId(orderId + "");
        wuYouOrderReq.setPayType("标准快递");
        wuYouOrderReq.setGoodsType("135ml鹿龟酒");
        wuYouOrderReq.setGoodsNum("1");
        wuYouOrderReq.setUserName("18780003433");
        wuYouOrderReq.setPassword(SecureUtil.md5("Sto1259...***"));
        wuYouOrderReq.setCustomerName("646643000296");
        wuYouOrderReq.setSenderName("眉山申通");
        wuYouOrderReq.setSenderMobile("13890312117");
        wuYouOrderReq.setSenderProvince("四川省");
        wuYouOrderReq.setSenderCity("眉山市");
        wuYouOrderReq.setSenderDistrict("东坡区");
        wuYouOrderReq.setSenderDetail("诗书路南段998号申通快递");
        wuYouOrderReq.setRecipientName(orderAddress.getRealname());
        wuYouOrderReq.setRecipientMobile(orderAddress.getMobile());
        wuYouOrderReq.setRecipientProvince(orderAddress.getProvince());
        wuYouOrderReq.setRecipientCity(orderAddress.getCity());
        wuYouOrderReq.setRecipientDistrict(orderAddress.getDistrict());
        wuYouOrderReq.setRecipientDetail(orderAddress.getAddress());
        wuYouOrderReq.setOrderAndGetBillCode(1);
        wuyou.setContent(JSONUtil.toJsonStr(wuYouOrderReq));
        OrderDetail detail = new OrderDetail();
        detail.setId(childOrderId);
        detail.setExpressStatus("FAIL");
        try {
            String json = LinkUtils.request(wuyou, url, secretKey);
            logger.info("请求申通数据响应结果->{}",json);
            StOrderResult o = JSONUtil.toBean(json, StOrderResult.class);
            if (o.getSuccess() && StringUtils.isNotBlank(o.getData().getWaybillCode())) {
                detail.setOrderStatus(UserOrderStatusEnum.WAIT_RECEIVE.getCode());
                detail.setExpressCompany("申通");
                detail.setExpressNo(o.getData().getWaybillCode());
                detail.setShipTime(LocalDateTime.now());
                detail.setExpressStatus("SUCCESS");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        orderDetailMapper.updateById(detail);
    }

    public static void main(String[] args) {
        String url = "https://cloudinter-linkgatewayonline.sto.cn/gateway/link.do";
        String secretKey = "Omj0YY5P29cAvWhddNukhdxwxL4S1b4x";
        LinkRequest wuyou = new LinkRequest();
        wuyou.setFromAppkey("CAKFvLQuMfpsbGZ");
        wuyou.setFromCode("CAKFvLQuMfpsbGZ");
        wuyou.setToAppkey("sto_merchant");
        wuyou.setToCode("sto_merchant_code");
        wuyou.setApiName("ADD_WUYOU_ORDER");
        WuYouOrderReq wuYouOrderReq = new WuYouOrderReq();
        wuYouOrderReq.setPlatformOrderId(3 + "");
        wuYouOrderReq.setPayType("标准快递");
        wuYouOrderReq.setGoodsType("135ml鹿龟酒");
        wuYouOrderReq.setGoodsNum("1");
        wuYouOrderReq.setUserName("18780003433");
        wuYouOrderReq.setPassword(SecureUtil.md5("Sto1259...***"));
        wuYouOrderReq.setCustomerName("646643000296");
        wuYouOrderReq.setSenderName("眉山申通");
        wuYouOrderReq.setSenderMobile("13890312117");
        wuYouOrderReq.setSenderProvince("四川省");
        wuYouOrderReq.setSenderCity("眉山市");
        wuYouOrderReq.setSenderDistrict("东坡区");
        wuYouOrderReq.setSenderDetail("诗书路南段998号申通快递");
        wuYouOrderReq.setRecipientName("尤圣回");
        wuYouOrderReq.setRecipientMobile("15669068377");
        wuYouOrderReq.setRecipientProvince("浙江省");
        wuYouOrderReq.setRecipientCity("杭州市");
        wuYouOrderReq.setRecipientDistrict("滨江区");
        wuYouOrderReq.setRecipientDetail("人工智能产业园");
        wuYouOrderReq.setOrderAndGetBillCode(1);
        wuyou.setContent(JSONUtil.toJsonStr(wuYouOrderReq));
        //String json = LinkUtils.request(wuyou, url, secretKey);
        //System.out.println(json);
        String json = "{\"data\":{\"expressCode\":\"STO\",\"waybillCode\":\"772013715937274\",\"platformOrderId\":\"2\"},\"success\":\"true\",\"errorCode\":\"200\",\"errorMsg\":\"成功\"}";
        StOrderResult stOrderResult = JSONUtil.toBean(json, StOrderResult.class);
        System.out.println(stOrderResult.toString());
    }
}
