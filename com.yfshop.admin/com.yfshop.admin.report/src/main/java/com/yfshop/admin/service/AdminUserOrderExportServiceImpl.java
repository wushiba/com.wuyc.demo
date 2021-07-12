package com.yfshop.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.order.request.QueryOrderReq;
import com.yfshop.admin.api.order.result.OrderExportResult;
import com.yfshop.admin.api.order.service.AdminUserOrderExportService;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@DubboService
public class AdminUserOrderExportServiceImpl implements AdminUserOrderExportService {
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private DrawRecordMapper drawRecordMapper;
    @Resource
    private OrderAddressMapper orderAddressMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderMapper orderMapper;

    @Override
    public List<OrderExportResult> orderExport(QueryOrderReq req) throws ApiException {
        List<Integer> itemIds = new ArrayList<>();
        List<Long> orderAddressIds = new ArrayList<>();
        DrawRecord drawRecord = null;
        if (req.getCategoryId() != null) {
            itemIds = itemMapper.selectList(Wrappers.lambdaQuery(Item.class).eq(Item::getCategoryId, req.getCategoryId())).stream().map(Item::getId).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(req.getActCode()) || StringUtils.isNotBlank(req.getTraceNo())) {
            drawRecord = drawRecordMapper.selectOne(Wrappers.lambdaQuery(DrawRecord.class)
                    .eq(StringUtils.isNotBlank(req.getActCode()), DrawRecord::getActCode, req.getActCode())
                    .eq(StringUtils.isNotBlank(req.getTraceNo()), DrawRecord::getTraceNo, req.getTraceNo()));
        }
        if (StringUtils.isNotBlank(req.getReceiverMobile()) || StringUtils.isNotBlank(req.getReceiverName())) {
            orderAddressIds = orderAddressMapper.selectList(Wrappers.lambdaQuery(OrderAddress.class)
                    .like(StringUtils.isNotBlank(req.getReceiverMobile()), OrderAddress::getMobile, req.getReceiverMobile())
                    .like(StringUtils.isNotBlank(req.getReceiverName()), OrderAddress::getRealname, req.getReceiverName())
            ).stream().map(OrderAddress::getOrderId).distinct().collect(Collectors.toList());

        }
        LambdaQueryWrapper<OrderDetail> wrapper = Wrappers.lambdaQuery(OrderDetail.class)
                .eq(req.getOrderId() != null, OrderDetail::getOrderId, req.getOrderId())
                .eq(StringUtils.isNoneBlank(req.getOrderNo()), OrderDetail::getOrderNo, req.getOrderNo())
                .eq(StringUtils.isNoneBlank(req.getReceiveWay()), OrderDetail::getReceiveWay, req.getReceiveWay())
                .eq(StringUtils.isNoneBlank(req.getExpressNo()), OrderDetail::getExpressNo, req.getExpressNo())
                .eq(StringUtils.isNoneBlank(req.getOrderStatus()), OrderDetail::getOrderStatus, req.getOrderStatus())
                .eq(drawRecord != null, OrderDetail::getUserCouponId, drawRecord == null ? null : drawRecord.getUserCouponId())
                .isNotNull("Y".equals(req.getIsUseCoupon()), OrderDetail::getUserCouponId)
                .isNull("N".equals(req.getIsUseCoupon()), OrderDetail::getUserCouponId)
                .like(StringUtils.isNoneBlank(req.getUserName()), OrderDetail::getUserName, req.getUserName())
                .like(StringUtils.isNoneBlank(req.getItemTitle()), OrderDetail::getItemTitle, req.getItemTitle())
                .eq(req.getCouponName() != null, OrderDetail::getCouponPrice, req.getCouponName())
                .in(CollectionUtils.isNotEmpty(itemIds), OrderDetail::getItemId, itemIds)
                .in(CollectionUtils.isNotEmpty(orderAddressIds), OrderDetail::getOrderId, orderAddressIds)
                .ge(req.getStartTime() != null, OrderDetail::getCreateTime, req.getStartTime())
                .lt(req.getEndTime() != null, OrderDetail::getCreateTime, req.getEndTime())
                .orderByDesc(OrderDetail::getId);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(wrapper);
        List<Long> orderIds = orderDetailList.stream().map(OrderDetail::getOrderId).distinct().collect(Collectors.toList());
        Map<Long, Order> orderMap = orderMapper.selectBatchIds(orderIds).stream().collect(Collectors.toMap(Order::getId, Function.identity()));
        Map<Long, OrderAddress> orderAddressMap = orderAddressMapper.selectList(Wrappers.lambdaQuery(OrderAddress.class).in(OrderAddress::getOrderId, orderIds)).stream().collect(Collectors.toMap(OrderAddress::getOrderId, Function.identity()));
        List<OrderExportResult> resultList = new ArrayList<>();
        orderDetailList.forEach(item -> {
            OrderExportResult exportResult = BeanUtil.convert(item, OrderExportResult.class);
            exportResult.setCreateTime(DateUtil.localDateTimeToDate(item.getCreateTime()));
            Order o = orderMap.get(item.getOrderId());
            if (o != null) {
                exportResult.setPayTime(DateUtil.localDateTimeToDate(o.getPayTime()));
            }
            OrderAddress orderAddress = orderAddressMap.get(item.getOrderId());
            if (orderAddress != null) {
                exportResult.setAddress(orderAddress.getAddress());
                exportResult.setCity(orderAddress.getCity());
                exportResult.setProvince(orderAddress.getProvince());
                exportResult.setDistrict(orderAddress.getDistrict());
                exportResult.setRealname(orderAddress.getRealname());
                exportResult.setMobile(orderAddress.getMobile());
            }
            resultList.add(exportResult);
        });

        return resultList;
    }
}
