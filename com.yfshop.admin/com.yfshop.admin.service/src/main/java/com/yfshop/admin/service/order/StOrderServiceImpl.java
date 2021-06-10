package com.yfshop.admin.service.order;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sto.link.request.LinkRequest;
import com.sto.link.util.LinkUtils;
import com.yfshop.admin.api.order.request.StOrderReq;
import com.yfshop.admin.api.order.result.StOrderResult;
import com.yfshop.admin.api.order.service.StOrderService;
import com.yfshop.code.mapper.OrderAddressMapper;
import com.yfshop.code.mapper.OrderDetailMapper;
import com.yfshop.code.mapper.OrderMapper;
import com.yfshop.code.model.Order;
import com.yfshop.code.model.OrderAddress;
import com.yfshop.code.model.OrderDetail;
import com.yfshop.common.enums.UserOrderStatusEnum;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class StOrderServiceImpl implements StOrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderAddressMapper orderAddressMapper;

    @Override
    @Async
    public void pushStOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        /**
         * 目前只发这些
         */
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(Wrappers.lambdaQuery(OrderDetail.class)
                .eq(OrderDetail::getOrderId, orderId)
                .in(OrderDetail::getSkuId, "20068", "20117", "20113")
                .eq(OrderDetail::getReceiveWay, "PS")
                .eq(OrderDetail::getIsPay, "Y")
                .eq(OrderDetail::getOrderStatus, UserOrderStatusEnum.WAIT_DELIVERY.getCode()));
        if (!CollectionUtils.isEmpty(orderDetailList)) {
            OrderAddress orderAddress = orderAddressMapper.selectOne(Wrappers.lambdaQuery(OrderAddress.class).eq(OrderAddress::getOrderId, orderId));
            StOrderReq stOrderReq = new StOrderReq();
            stOrderReq.setOrderNo(order.getId() + "");
            stOrderReq.setOrderSource("CAKFvLQuMfpsbGZ");
            stOrderReq.setBillType("00");
            stOrderReq.setOrderType("01");
            StOrderReq.SenderDTO senderDTO = new StOrderReq.SenderDTO();
            senderDTO.setName("四川申通龙泉公司");
            senderDTO.setTel("13799988851");
            senderDTO.setMobile("13799988851");
            senderDTO.setProvince("四川省");
            senderDTO.setCity("成都市");
            senderDTO.setArea("龙泉驿区");
            senderDTO.setAddress("大连路4号四川申通龙泉公司");
            stOrderReq.setSender(senderDTO);
            StOrderReq.ReceiverDTO receiverDTO = new StOrderReq.ReceiverDTO();
            receiverDTO.setProvince(orderAddress.getProvince());
            receiverDTO.setCity(orderAddress.getCity());
            receiverDTO.setArea(orderAddress.getDistrict());
            receiverDTO.setAddress(orderAddress.getAddress());
            receiverDTO.setTel(orderAddress.getMobile());
            receiverDTO.setMobile(orderAddress.getMobile());
            receiverDTO.setName(orderAddress.getRealname());
            stOrderReq.setReceiver(receiverDTO);
            StOrderReq.CargoDTO cargoDTO = new StOrderReq.CargoDTO();
            cargoDTO.setBattery("30");
            cargoDTO.setGoodsType("小件");
            cargoDTO.setGoodsName("椰岛陆龟酒");
            stOrderReq.setCargo(cargoDTO);
            StOrderReq.CustomerDTO customerDTO = new StOrderReq.CustomerDTO();
            customerDTO.setSiteCode("646640");
            customerDTO.setCustomerName("646640000002");
            customerDTO.setSitePwd("JJB123");
            stOrderReq.setCustomer(customerDTO);
            LinkRequest data = new LinkRequest();
            data.setFromAppkey("CAKoUWcvhIUBCVz");
            data.setFromCode("CAKoUWcvhIUBCVz");
            data.setToAppkey("sto_trace_query");
            data.setToCode("sto_trace_query");
            data.setApiName("STO_TRACE_QUERY_COMMON");
            String url = "http://cloudinter-linkgatewaytest.sto.cn/gateway/link.do";
            String secretKey = "CNHOUUv7PBH0IqRH2DQcdsKEGPqmLLZ6";
            String json = LinkUtils.request(data, url, secretKey);
            StOrderResult stOrderResult = JSONUtil.toBean(json, StOrderResult.class);
        }
    }

    public static void main(String[] args) {
        StOrderReq stOrderReq = new StOrderReq();
        stOrderReq.setOrderNo(1 + "");
        stOrderReq.setOrderSource("CAKFvLQuMfpsbGZ");
        stOrderReq.setBillType("00");
        stOrderReq.setOrderType("01");
        StOrderReq.SenderDTO senderDTO = new StOrderReq.SenderDTO();
        senderDTO.setName("五洲国际申通快递");
        senderDTO.setTel("13890312117");
        senderDTO.setMobile("13890312117");
        senderDTO.setProvince("四川省");
        senderDTO.setCity("眉山市");
        senderDTO.setArea("东坡区");
        senderDTO.setAddress("诗书路南段五洲国际申通快递");
        stOrderReq.setSender(senderDTO);
        StOrderReq.ReceiverDTO receiverDTO = new StOrderReq.ReceiverDTO();
        receiverDTO.setProvince("浙江省");
        receiverDTO.setCity("杭州市");
        receiverDTO.setArea("滨江区");
        receiverDTO.setAddress("人工智能产业园B座");
        receiverDTO.setTel("15669068377");
        receiverDTO.setMobile("15669068377");
        receiverDTO.setName("尤圣回");
        stOrderReq.setReceiver(receiverDTO);
        StOrderReq.CargoDTO cargoDTO = new StOrderReq.CargoDTO();
        cargoDTO.setBattery("30");
        cargoDTO.setGoodsType("小件");
        cargoDTO.setGoodsName("135ml鹿龟酒");
        stOrderReq.setCargo(cargoDTO);
        StOrderReq.CustomerDTO customerDTO = new StOrderReq.CustomerDTO();
        customerDTO.setSiteCode("646643");
        customerDTO.setCustomerName("646643000296");
        customerDTO.setSitePwd("1259...");
        stOrderReq.setCustomer(customerDTO);
        LinkRequest data = new LinkRequest();
        data.setFromAppkey("CAKFvLQuMfpsbGZ");
        data.setFromCode("CAKFvLQuMfpsbGZ");
        data.setToAppkey("sto_oms");
        data.setToCode("sto_oms");
        data.setApiName("OMS_EXPRESS_ORDER_CREATE");
        data.setContent(JSONUtil.toJsonStr(stOrderReq));
        String url = "https://cloudinter-linkgatewayonline.sto.cn/gateway/link.do";
        String secretKey = "Omj0YY5P29cAvWhddNukhdxwxL4S1b4x";
        String json = LinkUtils.request(data, url, secretKey);
        System.out.println(json);
        StOrderResult stOrderResult =  JSONUtil.toBean(json.startsWith("<response>") ? JSONUtil.xmlToJson(json).toString():json, StOrderResult.class);
        System.out.println(stOrderResult);
    }
}
