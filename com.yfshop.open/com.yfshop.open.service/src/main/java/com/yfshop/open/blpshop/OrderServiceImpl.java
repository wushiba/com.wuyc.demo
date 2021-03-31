package com.yfshop.open.blpshop;

import java.math.BigDecimal;

import com.google.common.collect.Lists;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.Order;
import com.yfshop.code.model.OrderAddress;
import com.yfshop.code.model.OrderDetail;
import com.yfshop.code.model.RlItemHotpot;
import com.yfshop.common.util.DateUtil;
import com.yfshop.open.api.blpshop.request.OrderReq;
import com.yfshop.open.api.blpshop.result.OrderResult;
import com.yfshop.open.api.blpshop.service.OrderService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@DubboService
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private RlItemHotpotMapper rlItemHotpotMapper;
    @Resource
    private OrderAddressMapper orderAddressMapper;

    @Override
    public OrderResult getOrder(OrderReq orderReq) {
        List<RlItemHotpot> list = rlItemHotpotMapper.selectList(Wrappers.emptyWrapper());
        List<Integer> skuIds = new ArrayList<>();
        list.stream().forEach(item -> {
            skuIds.add(item.getSkuId());
        });
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = Wrappers.<OrderDetail>lambdaQuery()
                .in(OrderDetail::getSkuId, skuIds)
                .eq(OrderDetail::getOrderStatus, "DFH")
                .eq(StringUtils.isNotBlank(orderReq.getPlatOrderNo()), OrderDetail::getOrderId, orderReq.getPlatOrderNo())
                .ge(orderReq.getStartTime() != null, "JH_02".equals(orderReq.getTimeType()) ? OrderDetail::getCreateTime : OrderDetail::getUpdateTime, orderReq.getStartTime())
                .le(orderReq.getEndTime() != null, "JH_02".equals(orderReq.getTimeType()) ? OrderDetail::getCreateTime : OrderDetail::getUpdateTime, orderReq.getEndTime());
        IPage<OrderDetail> iPage = orderDetailMapper.selectPage(new Page(orderReq.getPageIndex(), orderReq.getPageSize()), lambdaQueryWrapper);
        OrderResult orderResult = new OrderResult();
        List<OrderResult.Order> orderList = new ArrayList<>();
        List<OrderDetail> orderDetails = iPage.getRecords();
        Map<Long, List<OrderDetail>> OrderDetailList = orderDetails.stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));
        List<Long> orderIds = new ArrayList<>();
        OrderDetailList.forEach((key, value) -> {
            orderIds.add(key);
        });
        List<Order> orders = orderMapper.selectBatchIds(orderIds);
        List<OrderAddress> orderAddresses = orderAddressMapper.selectBatchIds(orderIds);
        Map<Long, Order> orderMap = orders.stream().collect(Collectors.toMap(Order::getId, Function.identity()));
        Map<Long, OrderAddress> orderAddressMap = orderAddresses.stream().collect(Collectors.toMap(OrderAddress::getOrderId, Function.identity()));
        OrderDetailList.forEach((key, value) -> {
            List<OrderResult.GoodInfo> goodInfos = new ArrayList<>();
            OrderAddress orderAddress = orderAddressMap.get(key);
            OrderResult.Order order = new OrderResult.Order();
            Order o = orderMap.get(key);
            order.setPlatOrderNo(key + "");
            order.setTradeStatus("JH_02");
            order.setTradeTime(DateUtil.localDateTimeToDate(o.getCreateTime()));
            order.setPayOrderNo(o.getBillNo());
            order.setProvince(orderAddress.getProvince());
            order.setCity(orderAddress.getCity());
            order.setArea(orderAddress.getDistrict());
            order.setAddress(orderAddress.getAddress());
            order.setMobile(orderAddress.getMobile());
            order.setPostFee(o.getFreight());
            order.setGoodsFee(o.getOrderPrice());
            order.setTotalMoney(o.getOrderPrice().add(o.getOrderPrice()));
            order.setRealPayMoney(o.getPayPrice());
            order.setFavourableMoney(o.getCouponPrice());
            order.setPayTime(DateUtil.localDateTimeToDate(o.getPayTime()));
            order.setReceiverName(orderAddress.getRealname());
            order.setNick(orderAddress.getRealname());
            order.setPayType("JH_WXWeb");
            order.setShouldPayType("担保交易");
            for (OrderDetail detail : value) {
                OrderResult.GoodInfo goodInfo=new OrderResult.GoodInfo();
                goodInfo.setProductId(key+"");
                goodInfo.setSubOrderNo(detail.getId()+"");
                goodInfo.setTradeGoodsNo(detail.getSkuId()+"");
                goodInfo.setPlatGoodsId(detail.getItemId()+"");
                goodInfo.setPlatSkuId(detail.getSkuId()+"");
                goodInfo.setOutItemId(detail.getItemId()+"");
                goodInfo.setOutSkuId(detail.getSkuId()+"");
                goodInfo.setTradeGoodsName(detail.getItemCount());
                goodInfo.setTradeGoodsSpec("");
                goodInfo.setGoodsCount(0);
                goodInfo.setPrice(new BigDecimal("0"));


            }
            order.setGoodInfos(goodInfos);
            orderList.add(order);
        });
        orderResult.setCode("10000");
        orderResult.setMessage("SUCCESS");
        orderResult.setOrders(orderList);
        orderResult.setNumTotalOrder(orderDetailMapper.selectCount(lambdaQueryWrapper));
        return orderResult;
    }
}
