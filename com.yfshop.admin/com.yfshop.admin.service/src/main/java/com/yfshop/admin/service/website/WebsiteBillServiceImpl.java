package com.yfshop.admin.service.website;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.website.WebsiteBillService;
import com.yfshop.admin.api.website.result.WebsiteBillDayResult;
import com.yfshop.admin.api.website.result.WebsiteBillResult;
import com.yfshop.code.mapper.OrderDetailMapper;
import com.yfshop.code.mapper.WebsiteBillMapper;
import com.yfshop.code.model.OrderDetail;
import com.yfshop.code.model.WebsiteBill;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.DateUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    private WebsiteBillMapper websiteBillMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

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
        Date nextDate = DateUtil.plusDays(dateTime, 1);
        List<WebsiteBill> websiteBills = websiteBillMapper.selectList(Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getMerchantId, merchantId).ge(WebsiteBill::getCreateTime, DateUtil.dateToLocalDateTime(dateTime))
                .lt(WebsiteBill::getCreateTime, DateUtil.dateToLocalDateTime(nextDate))
                .eq(WebsiteBill::getIsConfirm, status));
        WebsiteBillDayResult websiteBillDayResult = new WebsiteBillDayResult();
        List<WebsiteBillResult> websiteBillResults = new ArrayList<>();
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(new BigDecimal("0"));
        Integer totalQuantity = websiteBills.size();
        websiteBills.forEach(item -> {
            WebsiteBillResult websiteBillResult = new WebsiteBillResult();
            BeanUtil.copyProperties(item, websiteBillResults);
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
    public Void billConfirm(Integer merchantId, List<Integer> billIds) throws ApiException {
        LambdaQueryWrapper<WebsiteBill> lambdaQueryWrapper = Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getMerchantId, merchantId)
                .in(WebsiteBill::getId, billIds)
                .eq(WebsiteBill::getIsConfirm, "N");
        List<WebsiteBill> websiteBills = websiteBillMapper.selectList(lambdaQueryWrapper);
        WebsiteBill updateWebsiteBill = new WebsiteBill();
        updateWebsiteBill.setIsConfirm("Y");
        websiteBillMapper.update(updateWebsiteBill, lambdaQueryWrapper);
        List<Integer> orderIds = websiteBills.stream().map(WebsiteBill::getOrderId).collect(Collectors.toList());
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
        List<Integer> orderIds = websiteBills.stream().map(WebsiteBill::getOrderId).collect(Collectors.toList());
        orderConfirm(orderIds);
        return null;
    }


    /**
     * 更新订单状态为已完成
     *
     * @param orderIds
     */
    private void orderConfirm(List<Integer> orderIds) {
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = Wrappers.<OrderDetail>lambdaQuery()
                .in(OrderDetail::getOrderId, orderIds);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderStatus("YWC");
        orderDetailMapper.update(orderDetail, lambdaQueryWrapper);
    }

}
