package com.yfshop.admin.service.healthy;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.healthy.AdminHealthyService;
import com.yfshop.admin.api.healthy.request.QueryHealthyOrderReq;
import com.yfshop.admin.api.healthy.request.QueryHealthySubOrderReq;
import com.yfshop.admin.api.healthy.result.HealthyOrderDetailResult;
import com.yfshop.admin.api.healthy.result.HealthyOrderResult;
import com.yfshop.admin.api.healthy.result.HealthySubOrderResult;
import com.yfshop.code.mapper.HealthyOrderMapper;
import com.yfshop.code.mapper.HealthySubOrderMapper;
import com.yfshop.code.model.HealthyOrder;
import com.yfshop.code.model.HealthySubOrder;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.healthy.enums.HealthyOrderStatusEnum;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DubboService
public class AdminHealthyServiceImpl implements AdminHealthyService {
    @Resource
    private HealthyOrderMapper healthyOrderMapper;
    @Resource
    private HealthySubOrderMapper healthySubOrderMapper;

    @Override
    public IPage<HealthyOrderResult> findOrderList(QueryHealthyOrderReq req) {
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(HealthyOrder.class)
                .eq(StringUtils.isNotBlank(req.getOrderNo()), HealthyOrder::getOrderNo, req.getOrderNo())
                .eq(StringUtils.isNotBlank(req.getContracts()), HealthyOrder::getContracts, req.getContracts())
                .eq(StringUtils.isNotBlank(req.getOrderStatus()), HealthyOrder::getOrderStatus, req.getOrderStatus())
                .ne(HealthyOrder::getOrderStatus, "CANCEL")
                .ge(req.getStartTime() != null, HealthyOrder::getPayTime, req.getStartTime())
                .lt(req.getEndTime() != null, HealthyOrder::getPayTime, req.getEndTime());
        IPage<HealthyOrder> iPage = healthyOrderMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), queryWrapper);
        return BeanUtil.iPageConvert(iPage, HealthyOrderResult.class);
    }

    @Override
    public HealthyOrderDetailResult getOrderDetail(Integer id) {
        HealthyOrder healthyOrder = healthyOrderMapper.selectById(id);
        HealthyOrderDetailResult healthyOrderDetailResult = BeanUtil.convert(healthyOrder, HealthyOrderDetailResult.class);
        List<HealthySubOrder> list = healthySubOrderMapper.selectList(Wrappers.lambdaQuery(HealthySubOrder.class).eq(HealthySubOrder::getPOrderId, id));
        healthyOrderDetailResult.setList(BeanUtil.convertList(list, HealthySubOrderResult.class));
        return null;
    }

    @Override
    public IPage<HealthySubOrderResult> findSubOrderList(QueryHealthySubOrderReq req) {
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(HealthySubOrder.class)
                .eq(StringUtils.isNotBlank(req.getPOrderNo()), HealthySubOrder::getPOrderNo, req.getPOrderNo())
                .eq(StringUtils.isNotBlank(req.getOrderNo()), HealthySubOrder::getOrderNo, req.getOrderNo())
                .eq(StringUtils.isNotBlank(req.getContracts()), HealthySubOrder::getContracts, req.getContracts())
                .eq(StringUtils.isNotBlank(req.getMobile()), HealthySubOrder::getMobile, req.getMobile())
                .eq(StringUtils.isNotBlank(req.getAddress()), HealthySubOrder::getAddress, req.getAddress())
                .eq(StringUtils.isNotBlank(req.getOrderStatus()), HealthySubOrder::getOrderStatus, req.getOrderStatus())
                .eq(StringUtils.isNotBlank(req.getPostWay()), HealthySubOrder::getPostWay, req.getPostWay())
                .eq(req.getProvinceId() != null, HealthySubOrder::getProvinceId, req.getProvinceId())
                .eq(req.getCityId() != null, HealthySubOrder::getCityId, req.getCityId())
                .eq(req.getDistrictId() != null, HealthySubOrder::getDistrictId, req.getDistrictId())
                .ne(HealthySubOrder::getOrderStatus, "CANCEL");
        IPage<HealthySubOrder> iPage = healthyOrderMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), queryWrapper);
        return BeanUtil.iPageConvert(iPage, HealthySubOrderResult.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void notifyByWechatPay(String orderNo, String wechatBillNo) throws ApiException {
        Asserts.assertStringNotBlank(orderNo, 500, "订单ID不能为空");
        Asserts.assertStringNotBlank(wechatBillNo, 500, "支付流水号不能为空");
        HealthyOrder order = healthyOrderMapper.selectOne(Wrappers.lambdaQuery(HealthyOrder.class).eq(HealthyOrder::getOrderNo, orderNo));
        Asserts.assertNonNull(order, 500, "订单不存在");
        HealthyOrder bean = new HealthyOrder();
        bean.setOrderNo(orderNo);
        bean.setBillNo(wechatBillNo);
        bean.setOrderStatus(HealthyOrderStatusEnum.SERVICING.getCode());
        bean.setPayTime(LocalDateTime.now());
        int rows = healthyOrderMapper.update(bean, Wrappers.lambdaQuery(HealthyOrder.class).eq(HealthyOrder::getOrderNo, orderNo)
                .eq(HealthyOrder::getOrderStatus, HealthyOrderStatusEnum.PAYING.getCode()));
        if (rows <= 0) {
            return null;
        }

        String[] postRule = StringUtils.split(order.getPostRule(), "-");
        int count = Integer.parseInt(postRule[1]);

        // 今日11点时刻
        LocalDateTime today11Clock = LocalDateTime.of(LocalDate.now().getYear(), LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(), 11, 0, 0, 0);

        // 第一次配送时间
        Date firstPostTime;
        if (bean.getPayTime().isAfter(today11Clock)) {
            // 第3天开始
            firstPostTime = DateUtil.parse(DateTime.of(DateUtils.addDays(new Date(), 2)).toDateStr());
        } else {
            // 第2天开始
            firstPostTime = DateUtil.parse(DateTime.of(DateUtils.addDays(new Date(), 1)).toDateStr());
        }

        // 配送时间列表
        List<Date> postDateTimes = new ArrayList<>();
        postDateTimes.add(firstPostTime);
        Date temp = firstPostTime;
        for (int time = 1; time < order.getChildOrderCount(); time++) {
            if ("W".equals(postRule[0])) {
                temp = DateUtils.addWeeks(temp, 1);
                postDateTimes.add(temp);
            } else if ("M".equals(postRule[0])) {
                temp = DateUtils.addMonths(temp, 1);
                postDateTimes.add(temp);
            }
        }

        // 每次配送商品数量
        int per = order.getItemSpec() / order.getChildOrderCount();
        int remain = order.getItemSpec() % order.getChildOrderCount();

        // create sub order
        for (int i = 0; i < postDateTimes.size(); i++) {
            LocalDateTime expectShipTime = LocalDateTime.ofInstant(postDateTimes.get(i).toInstant(), ZoneId.systemDefault());
            // 最后一次配送加上余量
            int postItemCount = (i == postDateTimes.size() - 1) ? per + remain : per;
            HealthySubOrder subOrder = new HealthySubOrder();
            subOrder.setCreateTime(LocalDateTime.now());
            subOrder.setUpdateTime(LocalDateTime.now());
            subOrder.setUserId(order.getUserId());
            subOrder.setUserName(null);
            subOrder.setPOrderId(order.getId());
            subOrder.setPOrderNo(order.getOrderNo());
            subOrder.setOrderNo(order.getOrderNo() + i);
            subOrder.setMerchantId(null);
            subOrder.setPostWay(null);
            subOrder.setOrderStatus("gkjgjkgjhghjghjgjy");
            subOrder.setConfirmTime(null);
            subOrder.setExpectShipTime(expectShipTime);
            subOrder.setShipTime(null);
            subOrder.setExpressCompany(null);
            subOrder.setExpressNo(null);
            subOrder.setProvince(order.getProvince());
            subOrder.setCity(order.getCity());
            subOrder.setDistrict(order.getDistrict());
            subOrder.setProvinceId(order.getProvinceId());
            subOrder.setCityId(order.getCityId());
            subOrder.setDistrictId(order.getDistrictId());
            subOrder.setAddress(order.getAddress());
            subOrder.setMobile(order.getMobile());
            subOrder.setContracts(order.getContracts());
            subOrder.setPostItemCount(postItemCount);
            healthySubOrderMapper.insert(subOrder);
        }
        return null;
    }

}
