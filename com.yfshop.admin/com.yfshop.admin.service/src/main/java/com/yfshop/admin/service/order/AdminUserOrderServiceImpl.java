package com.yfshop.admin.service.order;

import java.math.BigDecimal;

import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.google.common.collect.Lists;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.admin.api.order.request.OrderExpressReq;
import com.yfshop.admin.api.order.request.QueryOrderReq;
import com.yfshop.admin.api.order.result.OrderDetailResult;
import com.yfshop.admin.api.order.result.OrderResult;
import com.yfshop.admin.api.order.service.AdminUserOrderService;
import com.yfshop.admin.api.order.service.StOrderService;
import com.yfshop.admin.api.website.WebsiteBillService;
import com.yfshop.admin.dao.OrderDao;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.enums.ReceiveWayEnum;
import com.yfshop.common.enums.UserCouponStatusEnum;
import com.yfshop.common.enums.UserOrderStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.wx.api.request.WxPayRefundReq;
import com.yfshop.wx.api.service.MpService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 后台订单serviceImpl
 *
 * @author wuyc
 * created 2021/4/9 13:43
 **/
@DubboService
public class AdminUserOrderServiceImpl implements AdminUserOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserOrderServiceImpl.class);

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
    @Resource
    private DrawRecordMapper drawRecordMapper;
    @Resource
    private OrderAddressMapper orderAddressMapper;
    @Resource
    private WxPayNotifyMapper wxPayNotifyMapper;
    @Resource
    private WxPayRefundMapper wxPayRefundMapper;
    @DubboReference
    private MpService mpService;
    @DubboReference
    StOrderService stOrderService;

    /**
     * 用户付款后修改订单状态
     *
     * @param orderId 主订单id
     * @param billNo  支付流水号
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
        Asserts.assertEquals(order.getIsPay(), "I", 500, "订单未处于支付中");
        String orderStatus = "ZT".equalsIgnoreCase(order.getReceiveWay()) ? UserOrderStatusEnum.SUCCESS.getCode() : UserOrderStatusEnum.WAIT_DELIVERY.getCode();
        // 修改订单状态，用乐观锁
        int result = orderDao.updateOrderPayStatus(orderId, billNo);
        if (result <= 0) {
            Asserts.assertNotEquals(order.getIsPay(), "Y", 500, "订单不可以重复修改状态");
        }
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setIsPay("Y");
        orderDetail.setOrderStatus(orderStatus);
        orderDetailMapper.update(orderDetail, Wrappers.<OrderDetail>lambdaQuery().
                eq(OrderDetail::getOrderId, orderId));

        if ("ZT".equalsIgnoreCase(order.getReceiveWay())) {
            websiteBillService.insertWebsiteBill(orderId);
        }
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(Wrappers.<OrderDetail>lambdaQuery().
                eq(OrderDetail::getOrderId, orderId));
        orderDetailList.forEach(item -> {
            if (item.getUserCouponId() != null) {
                UserCoupon userCoupon = new UserCoupon();
                userCoupon.setId(item.getUserCouponId());
                userCoupon.setUseStatus(UserCouponStatusEnum.HAS_USE.getCode());
                userCouponMapper.updateById(userCoupon);
                DrawRecord drawRecord = new DrawRecord();
                drawRecord.setUseStatus(UserCouponStatusEnum.HAS_USE.getCode());
                drawRecordMapper.update(drawRecord, Wrappers.<DrawRecord>lambdaQuery()
                        .eq(DrawRecord::getUserCouponId, item.getUserCouponId()));
                //二等奖优惠券申通无忧下单
                if ("2032001".equals(orderDetail.getSkuId()) && ReceiveWayEnum.PS.getCode().equals(orderDetail.getReceiveWay())) {
                    stOrderService.pushStOrder(item.getOrderId(),item.getId());
                }
            }
        });
        return null;
    }

    /**
     * 用户确认订单
     *
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
            DrawRecord drawRecord = new DrawRecord();
            drawRecord.setUseStatus(UserCouponStatusEnum.HAS_USE.getCode());
            drawRecordMapper.update(drawRecord, Wrappers.<DrawRecord>lambdaQuery()
                    .eq(DrawRecord::getUserCouponId, orderDetail.getUserCouponId()));
        }
        return null;
    }

    @Override
    public IPage<OrderResult> list(QueryOrderReq req) throws ApiException {
        LambdaQueryWrapper<OrderDetail> wrapper = Wrappers.lambdaQuery(OrderDetail.class)
                .like(StringUtils.isNoneBlank(req.getUserName()), OrderDetail::getUserName, req.getUserName())
                .eq(StringUtils.isNoneBlank(req.getOrderNo()), OrderDetail::getOrderNo, req.getOrderNo())
                .eq(StringUtils.isNoneBlank(req.getReceiveWay()), OrderDetail::getReceiveWay, req.getReceiveWay())
                .eq(StringUtils.isNoneBlank(req.getOrderStatus()), OrderDetail::getOrderStatus, req.getOrderStatus())
                .ge(req.getStartTime() != null, OrderDetail::getCreateTime, req.getStartTime())
                .lt(req.getEndTime() != null, OrderDetail::getCreateTime, req.getEndTime())
                .orderByDesc(OrderDetail::getId);
        IPage<OrderDetail> iPage = orderDetailMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), wrapper);
        return BeanUtil.iPageConvert(iPage, OrderResult.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void closeOrder(Long id) throws ApiException, WxPayException {
        OrderDetail orderDetail = orderDetailMapper.selectById(id);
        switch (UserOrderStatusEnum.getByCode(orderDetail.getOrderStatus())) {
            case WAIT_PAY:
            case WAIT_DELIVERY:
                OrderDetail newOrderDetail = new OrderDetail();
                newOrderDetail.setOrderStatus(UserOrderStatusEnum.CLOSED.getCode());
                newOrderDetail.setId(id);
                orderDetailMapper.updateById(newOrderDetail);
                Order order = orderMapper.selectById(orderDetail.getOrderId());
                if (order != null && StringUtils.isNotBlank(order.getBillNo())) {
                    WxPayNotify wxPayNotify = wxPayNotifyMapper.selectOne(Wrappers.lambdaUpdate(WxPayNotify.class).eq(WxPayNotify::getTransactionId, order.getBillNo()));
                    orderDetail = orderDetailMapper.selectById(id);
                    if (orderDetail != null && wxPayNotify != null) {
                        WxPayRefund wxPayRefund = new WxPayRefund();
                        wxPayRefund.setCreateTime(LocalDateTime.now());
                        wxPayRefund.setOpenId(wxPayNotify.getOpenId());
                        BigDecimal payPrice = orderDetail.getPayPrice().multiply(new BigDecimal("100"));
                        //扣除0.6%的手续费
//                        BigDecimal subtractFee = payPrice.multiply(new BigDecimal("0.006")).setScale(0, RoundingMode.CEILING);
//                        int refundFee = payPrice.subtract(subtractFee).intValue();
//                        wxPayRefund.setTotalFee(refundFee);
                        wxPayRefund.setTotalFee(payPrice.intValue());
                        wxPayRefund.setTransactionId(wxPayNotify.getTransactionId());
                        wxPayRefund.setOuttradeNo(wxPayNotify.getOuttradeNo());
                        wxPayRefund.setRefundNo("refundNo-shopOrder-" + id);
                        wxPayRefundMapper.insert(wxPayRefund);
                        WxPayRefundReq wxPayRefundReq = BeanUtil.convert(wxPayRefund, WxPayRefundReq.class);
                        wxPayRefundReq.setTotalFee(wxPayNotify.getTotalFee());
                        wxPayRefundReq.setRefundFee(wxPayRefund.getTotalFee());
                        this.mpService.refund(wxPayRefundReq);
                    }
                }
                break;

        }
        return null;
    }


    @Override
    public Void updateOrderExpress(OrderExpressReq orderExpressReq) throws ApiException {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setExpressCompany(orderExpressReq.getExpressName());
        orderDetail.setExpressNo(orderExpressReq.getExpressNo());
        orderDetail.setOrderStatus(UserOrderStatusEnum.WAIT_RECEIVE.getCode());
        orderDetail.setId(orderExpressReq.getId());
        orderDetailMapper.updateById(orderDetail);
        return null;
    }

    @Override
    public OrderDetailResult getOrderDetail(Long id) throws ApiException {
        OrderDetail orderDetail = orderDetailMapper.selectById(id);
        OrderAddress orderAddress = orderAddressMapper.selectOne(Wrappers.lambdaQuery(OrderAddress.class).eq(OrderAddress::getOrderId, orderDetail.getOrderId()));
        OrderDetailResult orderDetailResult = new OrderDetailResult();
        orderDetailResult.setOrderNo(orderDetail.getOrderNo());
        orderDetailResult.setCreateTime(orderDetail.getCreateTime());
        orderDetailResult.setUserName(orderDetail.getUserName());
        orderDetailResult.setReceiveWay(orderDetail.getReceiveWay());
        if (orderAddress != null) {
            orderDetailResult.setAddress(String.format("%s,%s,%s%s%s", orderAddress.getRealname(), orderAddress.getMobile(), orderAddress.getProvince(), orderAddress.getCity(), orderAddress.getDistrict(), orderAddress.getAddress()));
        }
        orderDetailResult.setShipTime(orderDetail.getShipTime());
        orderDetailResult.setExpressCompany(orderDetail.getExpressCompany());
        orderDetailResult.setExpressNo(orderDetail.getExpressNo());
        orderDetailResult.setExpressStatus(orderDetail.getExpressStatus());
        orderDetailResult.setList(Lists.newArrayList());
        orderDetailResult.setId(id);
        OrderDetailResult.OrderDetails orderDetails = new OrderDetailResult.OrderDetails();
        orderDetails.setCreateTime(orderDetail.getCreateTime());
        orderDetails.setOrderNo(orderDetail.getOrderNo());
        orderDetails.setUserId(orderDetail.getUserId());
        orderDetails.setUserName(orderDetail.getUserName());
        orderDetails.setOrderId(orderDetail.getOrderId());
        orderDetails.setMerchantId(orderDetail.getMerchantId());
        orderDetails.setPidPath(orderDetail.getPidPath());
        orderDetails.setWebsiteCode(orderDetail.getWebsiteCode());
        orderDetails.setReceiveWay(orderDetail.getReceiveWay());
        orderDetails.setIsPay(orderDetail.getIsPay());
        orderDetails.setItemCover(orderDetail.getItemCover());
        orderDetails.setItemPrice(orderDetail.getItemPrice());
        orderDetails.setItemCount(orderDetail.getItemCount());
        orderDetails.setFreight(orderDetail.getFreight());
        orderDetails.setCouponPrice(orderDetail.getCouponPrice());
        orderDetails.setOrderPrice(orderDetail.getOrderPrice());
        orderDetails.setPayPrice(orderDetail.getPayPrice());
        orderDetails.setOrderStatus(orderDetail.getOrderStatus());
        orderDetails.setItemTitle(orderDetail.getItemTitle());
        orderDetails.setSpecNameValueJson(orderDetail.getSpecNameValueJson());
        orderDetails.setSpecValueStr(orderDetail.getSpecValueStr());
        orderDetails.setConfirmTime(orderDetail.getConfirmTime());
        orderDetailResult.getList().add(orderDetails);
        return orderDetailResult;
    }


    @Override
    public Void trySendStoOrder(Long id) {
        OrderDetail orderDetail = orderDetailMapper.selectById(id);
        if (orderDetail == null) return null;
        //二等奖优惠券申通无忧下单
        if (UserOrderStatusEnum.WAIT_DELIVERY.getCode().equals(orderDetail.getOrderStatus()) && "2032001".equals(orderDetail.getSkuId()) && ReceiveWayEnum.PS.getCode().equals(orderDetail.getReceiveWay())) {
            stOrderService.pushStOrder(orderDetail.getOrderId(), orderDetail.getId());
        }

        return null;
    }
}
