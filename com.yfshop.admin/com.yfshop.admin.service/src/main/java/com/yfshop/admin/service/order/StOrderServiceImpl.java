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
            orderAddressMapper.selectOne(Wrappers.lambdaQuery(OrderAddress.class).eq(OrderAddress::getOrderId,orderId));
            StOrderReq stOrderReq = new StOrderReq();
            stOrderReq.setOrderNo(order.getId() + "");
            stOrderReq.setOrderSource("雨帆健康家");
            stOrderReq.setBillType("01");
            StOrderReq.SenderDTO senderDTO = new StOrderReq.SenderDTO();
            senderDTO.setName("湖南雨帆噜渴乳业股份有限公司");
            senderDTO.setTel("400-668-0091");
            senderDTO.setMobile("400-668-0091");
            stOrderReq.setSender(senderDTO);

            StOrderReq.ReceiverDTO receiverDTO = new StOrderReq.ReceiverDTO();


            stOrderReq.setReceiver(receiverDTO);
        }

    }
}
