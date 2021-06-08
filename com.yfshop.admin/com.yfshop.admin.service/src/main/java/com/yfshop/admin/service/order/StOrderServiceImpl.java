package com.yfshop.admin.service.order;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.order.request.StOrderReq;
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
            stOrderReq.setOrderSource("雨帆健康家");
            stOrderReq.setBillType("01");
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

            StOrderReq.CustomerDTO customerDTO=new StOrderReq.CustomerDTO();
            customerDTO.setSiteCode("");
            customerDTO.setCustomerName("");
            customerDTO.setSitePwd("");
            stOrderReq.setCustomer(customerDTO);
        }

    }
}
