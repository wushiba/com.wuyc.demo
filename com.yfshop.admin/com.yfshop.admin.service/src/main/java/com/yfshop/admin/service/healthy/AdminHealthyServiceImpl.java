package com.yfshop.admin.service.healthy;

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
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
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
}
