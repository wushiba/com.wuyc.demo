package com.yfshop.admin.service.order;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.order.service.AdminUserOrderService;
import com.yfshop.admin.api.website.WebsiteBillService;
import com.yfshop.admin.dao.OrderDao;
import com.yfshop.code.mapper.OrderAddressMapper;
import com.yfshop.code.mapper.OrderDetailMapper;
import com.yfshop.code.mapper.OrderMapper;
import com.yfshop.code.mapper.UserCouponMapper;
import com.yfshop.code.model.Order;
import com.yfshop.code.model.OrderDetail;
import com.yfshop.code.model.UserCoupon;
import com.yfshop.common.enums.UserCouponStatusEnum;
import com.yfshop.common.enums.UserOrderStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * 后台订单serviceImpl
 * @author wuyc
 * created 2021/4/9 13:43
 **/
@DubboService
public class AdminUserServiceImpl implements AdminUserOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserServiceImpl.class);

    @Resource
    private OrderDao orderDao;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserCouponMapper userCouponMapper;
    @Resource
    private WebsiteBillService websiteBillService;
    @Resource
    private OrderDetailMapper orderDetailMapper;

    /**
     * 用户付款后修改订单状态
     * @param orderId   主订单id
     * @param billNo    支付流水号
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void updateOrderPayStatus(Long orderId, String billNo) throws ApiException {
        logger.info("====进入订单支付成功通知orderId=" + orderId + ",billNo=" + billNo);
        Asserts.assertNonNull(orderId, 500, "主订单id不可以为空");
        Order order = orderMapper.selectById(orderId);
        Asserts.assertNonNull(order, 500, "订单不存在");
        Asserts.assertNotEquals(order.getIsPay(), "Y", 500, "订单不可以重复修改状态");

        // 修改订单状态，用乐观锁
        int result = orderDao.updateOrderPayStatus(orderId, billNo);
        if (result <= 0) {
            Asserts.assertNotEquals(order.getIsPay(), "Y", 500, "订单不可以重复修改状态");
        }
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setIsPay("Y");
        orderDetail.setOrderStatus(UserOrderStatusEnum.WAIT_DELIVERY.getCode());
        orderDetailMapper.update(orderDetail, Wrappers.<OrderDetail>lambdaQuery().
                eq(OrderDetail::getOrderId, orderId));

        if ("ZT".equalsIgnoreCase(order.getReceiveWay())) {
            websiteBillService.insertWebsiteBill(orderId);
        }
        return null;
    }

    /**
     * 用户确认订单
     * @param userId        用户id
     * @param orderDetailId 订单详情id
     * @throws ApiException
     */
    @Override
    public Void confirmOrder(Integer userId, Long orderDetailId) throws ApiException {
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

        // 优惠券改成已使用
        if (orderDetail.getUserCouponId() != null) {
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setId(orderDetail.getUserCouponId());
            userCoupon.setUseStatus(UserCouponStatusEnum.HAS_USE.getCode());
            userCouponMapper.updateById(userCoupon);
        }
        return null;
    }

}
