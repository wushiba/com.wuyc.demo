package com.yfshop.admin.service.website;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.website.WebsiteBillService;
import com.yfshop.admin.api.website.result.WebsiteBillDayResult;
import com.yfshop.admin.api.website.result.WebsiteBillResult;
import com.yfshop.code.mapper.OrderAddressMapper;
import com.yfshop.code.mapper.OrderDetailMapper;
import com.yfshop.code.mapper.OrderMapper;
import com.yfshop.code.mapper.WebsiteBillMapper;
import com.yfshop.code.model.Order;
import com.yfshop.code.model.OrderAddress;
import com.yfshop.code.model.OrderDetail;
import com.yfshop.code.model.WebsiteBill;
import com.yfshop.common.enums.ReceiveWayEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.DateUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 网店记账服务
 *
 * @author youshenghui
 * Created in 2021-03-23 9:10
 */
@Validated
@DubboService
public class WebsiteBillServiceImpl implements WebsiteBillService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private WebsiteBillMapper websiteBillMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderAddressMapper orderAddressMapper;

    /**
     * 获取网店记账列表
     *
     * @param merchantId
     * @param dateTime
     * @param status
     * @return
     */
    @Override
    public WebsiteBillDayResult getBillListByMerchantId(Integer merchantId, Date dateTime, String status) throws ApiException {
        Date nextDate = null;
        if (dateTime != null) {
            nextDate = DateUtil.plusDays(dateTime, 1);
        }
        List<WebsiteBill> websiteBills = websiteBillMapper.selectList(Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getMerchantId, merchantId)
                .ge(dateTime != null,WebsiteBill::getCreateTime, dateTime)
                .lt(nextDate != null, WebsiteBill::getCreateTime, nextDate)
                .eq(WebsiteBill::getIsConfirm, status)
                .orderByDesc(WebsiteBill::getCreateTime));
        WebsiteBillDayResult websiteBillDayResult = new WebsiteBillDayResult();
        List<WebsiteBillResult> websiteBillResults = new ArrayList<>();
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(new BigDecimal("0"));
        Integer totalQuantity = websiteBills.size();
        websiteBills.forEach(item -> {
            WebsiteBillResult websiteBillResult = new WebsiteBillResult();
            BeanUtil.copyProperties(item, websiteBillResult);
            websiteBillResults.add(websiteBillResult);
            totalAmount.set(websiteBillResult.getPayPrice().add(totalAmount.get()));
        });
        websiteBillDayResult.setWebSiteBillList(websiteBillResults);
        websiteBillDayResult.setTotalAmount(totalAmount.get().doubleValue());
        websiteBillDayResult.setTotalQuantity(totalQuantity);
        return websiteBillDayResult;
    }

    @Override
    public WebsiteBillDayResult getBillByWebsiteCode(String websiteCode, Date dateTime) {
        Date nextDate = null;
        if (dateTime != null) {
            nextDate = DateUtil.plusDays(dateTime, 1);
        }
        List<WebsiteBill> websiteBills = websiteBillMapper.selectList(Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getWebsiteCode, websiteCode)
                .ge(dateTime != null,WebsiteBill::getCreateTime, dateTime)
                .lt(nextDate != null, WebsiteBill::getCreateTime, nextDate)
                .eq(WebsiteBill::getIsConfirm, 'Y')
                .orderByDesc(WebsiteBill::getCreateTime));
        WebsiteBillDayResult websiteBillDayResult = new WebsiteBillDayResult();
        List<WebsiteBillResult> websiteBillResults = new ArrayList<>();
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(new BigDecimal("0"));
        Integer totalQuantity = websiteBills.size();
        websiteBills.forEach(item -> {
            WebsiteBillResult websiteBillResult = new WebsiteBillResult();
            BeanUtil.copyProperties(item, websiteBillResult);
            websiteBillResults.add(websiteBillResult);
            totalAmount.set(websiteBillResult.getPayPrice().add(totalAmount.get()));
        });
        websiteBillDayResult.setWebSiteBillList(websiteBillResults);
        websiteBillDayResult.setTotalAmount(totalAmount.get().doubleValue());
        websiteBillDayResult.setTotalQuantity(totalQuantity);
        return websiteBillDayResult;
    }

    /**
     * 确认记账
     *
     * @param merchantId
     * @param billIds
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void billConfirm(Integer merchantId, List<Long> billIds) throws ApiException {
        LambdaQueryWrapper<WebsiteBill> lambdaQueryWrapper = Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getMerchantId, merchantId)
                .in(WebsiteBill::getId, billIds)
                .eq(WebsiteBill::getIsConfirm, "N");
        List<WebsiteBill> websiteBills = websiteBillMapper.selectList(lambdaQueryWrapper);
        WebsiteBill updateWebsiteBill = new WebsiteBill();
        updateWebsiteBill.setIsConfirm("Y");
        websiteBillMapper.update(updateWebsiteBill, lambdaQueryWrapper);
        List<Long> orderIds = websiteBills.stream().map(WebsiteBill::getOrderId).collect(Collectors.toList());
        orderConfirm(orderIds);
        return null;
    }

    /**
     * 一键确认记账
     *
     * @param merchantId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void billAllConfirm(Integer merchantId) throws ApiException {
        LambdaQueryWrapper<WebsiteBill> lambdaQueryWrapper = Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getMerchantId, merchantId)
                .eq(WebsiteBill::getIsConfirm, "N");
        List<WebsiteBill> websiteBills = websiteBillMapper.selectList(lambdaQueryWrapper);
        WebsiteBill updateWebsiteBill = new WebsiteBill();
        updateWebsiteBill.setIsConfirm("Y");
        websiteBillMapper.update(updateWebsiteBill, lambdaQueryWrapper);
        List<Long> orderIds = websiteBills.stream().map(WebsiteBill::getOrderId).collect(Collectors.toList());
        orderConfirm(orderIds);
        return null;
    }

    /**
     * 用户自提二等奖成功后，生成网点记账单
     * @param orderId     用户主订单id
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void insertWebsiteBill(Long orderId) throws ApiException {
        Asserts.assertNonNull(orderId, 500, "主订单id不可以为空");
        Order order = orderMapper.selectById(orderId);
        Asserts.assertNonNull(order, 500, "订单不存在");
        if (ReceiveWayEnum.PS.getCode().equalsIgnoreCase(order.getReceiveWay())) {
            return null;
        }

        OrderAddress orderAddress = orderAddressMapper.selectOne(Wrappers.lambdaQuery(OrderAddress.class)
                .eq(OrderAddress::getOrderId, orderId));

        List<OrderDetail> detailList = orderDetailMapper.selectList(Wrappers.lambdaQuery(OrderDetail.class)
                .eq(OrderDetail::getOrderId, orderId));

        detailList.forEach(detail -> {
            WebsiteBill websiteBill = new WebsiteBill();
            websiteBill.setCreateTime(LocalDateTime.now());
            websiteBill.setUpdateTime(LocalDateTime.now());
            websiteBill.setMerchantId(detail.getMerchantId());
            websiteBill.setPidPath(detail.getPidPath());
            websiteBill.setUserId(detail.getUserId());
            websiteBill.setNickname(orderAddress.getRealname());
            websiteBill.setOrderId(orderId);
            websiteBill.setItemTitle(detail.getItemTitle());
            websiteBill.setPayPrice(detail.getPayPrice());
            websiteBill.setBillNo(order.getBillNo());
            websiteBill.setIsConfirm("N");
            websiteBill.setWebsiteCode(detail.getWebsiteCode());
            websiteBillMapper.insert(websiteBill);
        });
        return null;
    }


    /**
     * 更新订单状态为已完成
     *
     * @param orderIds
     */
    private void orderConfirm(List<Long> orderIds) {
        if (!CollectionUtil.isEmpty(orderIds)) {
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = Wrappers.<OrderDetail>lambdaQuery()
                    .in(OrderDetail::getOrderId, orderIds);
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderStatus("YWC");
            orderDetailMapper.update(orderDetail, lambdaQueryWrapper);
        }
    }

}
