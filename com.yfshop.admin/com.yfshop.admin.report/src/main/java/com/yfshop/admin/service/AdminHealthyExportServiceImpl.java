package com.yfshop.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.draw.request.QueryDrawRecordExportReq;
import com.yfshop.admin.api.draw.result.DrawRecordExportResult;
import com.yfshop.admin.api.draw.service.AdminDrawRecordExportService;
import com.yfshop.admin.api.healthy.AdminHealthyExportService;
import com.yfshop.admin.api.healthy.request.HealthySubOrderImportReq;
import com.yfshop.admin.api.healthy.request.QueryHealthySubOrderReq;
import com.yfshop.admin.api.healthy.result.HealthySubOrderExportResult;
import com.yfshop.code.mapper.DrawRecordMapper;
import com.yfshop.code.mapper.HealthySubOrderMapper;
import com.yfshop.code.model.DrawRecord;
import com.yfshop.code.model.HealthySubOrder;
import com.yfshop.common.healthy.enums.HealthySubOrderStatusEnum;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@DubboService
public class AdminHealthyExportServiceImpl implements AdminHealthyExportService {
    @Resource
    private HealthySubOrderMapper healthySubOrderMapper;

    @Override
    public List<HealthySubOrderExportResult> exportSubOrderList(QueryHealthySubOrderReq req) {
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
                .like(StringUtils.isNotBlank(req.getExpressCompany()), HealthySubOrder::getExpressCompany, req.getExpressCompany())
                .like(StringUtils.isNotBlank(req.getExpressNo()), HealthySubOrder::getExpressNo, req.getExpressNo())
                .ge(req.getStartTime() != null, HealthySubOrder::getExpectShipTime, req.getStartTime())
                .lt(req.getEndTime() != null, HealthySubOrder::getExpectShipTime, req.getEndTime());
        List<HealthySubOrder> list = healthySubOrderMapper.selectList(queryWrapper);
        List<HealthySubOrderExportResult> result = new ArrayList<>();
        for (HealthySubOrder subOrder : list) {
            HealthySubOrderExportResult exportResult = BeanUtil.convert(subOrder, HealthySubOrderExportResult.class);
            exportResult.setExpectShipTime(DateUtil.localDateTimeToDate(subOrder.getExpectShipTime()));
            result.add(exportResult);
        }
        return result;
    }

    @Override
    public Void importSubOrderList(List<HealthySubOrderImportReq> healthySubOrderImport) {
        if (!CollectionUtils.isEmpty(healthySubOrderImport)) {
            healthySubOrderImport.forEach(item -> {
                if (StringUtils.isNotBlank(item.getExpressNo())) {
                    HealthySubOrder healthySubOrder = new HealthySubOrder();
                    healthySubOrder.setOrderStatus(HealthySubOrderStatusEnum.IN_DELIVERY.getCode());
                    healthySubOrder.setExpressNo(item.getExpressNo());
                    healthySubOrder.setExpressCompany(item.getExpressCompany());
                    healthySubOrderMapper.update(healthySubOrder, Wrappers.lambdaQuery(HealthySubOrder.class)
                            .eq(HealthySubOrder::getOrderNo, item.getOrderNo())
                            .eq(HealthySubOrder::getOrderStatus, HealthySubOrderStatusEnum.WAIT_ALLOCATE.getCode()));
                }
            });
        }
        return null;
    }
}
