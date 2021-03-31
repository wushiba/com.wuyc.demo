package com.yfshop.shop.service.order;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.OrderAddressMapper;
import com.yfshop.code.mapper.OrderDetailMapper;
import com.yfshop.code.mapper.OrderMapper;
import com.yfshop.code.mapper.UserCouponMapper;
import com.yfshop.code.model.Order;
import com.yfshop.code.model.OrderDetail;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.shop.service.order.result.OrderResult;
import com.yfshop.shop.service.order.service.FrontUserOrderService;
import org.apache.dubbo.config.annotation.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * @Title:用户订单Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-31 16:16:25
 * @Version:1.1.0
 */
@Service(dynamic = true)
public class FrontUserOrderServiceImpl implements FrontUserOrderService {

    @Resource
    private UserCouponMapper userCouponMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderAddressMapper orderAddressMapper;


    @Override
    public List<OrderResult> findUserOrderList(Integer userId, String useStatus) throws ApiException {

        return null;
    }

    @Override
    public List<OrderResult> getUserOrderDetail(Integer userId, Integer orderId) throws ApiException {
        return null;
    }

    @Override
    public void cancelOrder(Integer userId, Integer orderId) throws ApiException {
        Order order = orderMapper.selectOne(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getUserId, userId)
                .eq(Order::getId, orderId));
        Asserts.assertNonNull(order, 500, "主订单信息不存在");
        Asserts.assertNotEquals("Y", order.getIsPay(), 500, "订单已支付,不能取消。");

//
    }

    @Override
    public void confirmOrder(Integer userId, Integer orderDetailId) throws ApiException {
        OrderDetail orderDetail = orderDetailMapper.selectOne(Wrappers.lambdaQuery(OrderDetail.class)
                .eq(OrderDetail::getUserId, userId)
                .eq(OrderDetail::getId, orderDetailId));
        Asserts.assertNonNull(orderDetail, 500, "子订单信息不存在");
        Order order = orderMapper.selectOne(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getUserId, userId)
                .eq(Order::getId, orderDetail.getOrderId()));
        Asserts.assertNonNull(order, 500, "主订单信息不存在");
        Asserts.assertNotEquals("N", order.getIsPay(), 500, "订单状态不正确。");

        orderDetail.setOrderStatus("SUCCESS");
        orderDetailMapper.updateById(orderDetail);
    }
}

