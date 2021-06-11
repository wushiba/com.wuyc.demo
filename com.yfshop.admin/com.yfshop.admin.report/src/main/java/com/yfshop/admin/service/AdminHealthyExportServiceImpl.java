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
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class AdminHealthyExportServiceImpl implements AdminHealthyExportService {
    @Resource
    private HealthySubOrderMapper healthySubOrderMapper;

    @Override
    public List<HealthySubOrderExportResult> exportSubOrderList(QueryHealthySubOrderReq req) {
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(HealthySubOrder.class)
                .eq(org.apache.commons.lang3.StringUtils.isNotBlank(req.getPOrderNo()), HealthySubOrder::getPOrderNo, req.getPOrderNo())
                .eq(org.apache.commons.lang3.StringUtils.isNotBlank(req.getOrderNo()), HealthySubOrder::getOrderNo, req.getOrderNo())
                .eq(org.apache.commons.lang3.StringUtils.isNotBlank(req.getContracts()), HealthySubOrder::getContracts, req.getContracts())
                .eq(org.apache.commons.lang3.StringUtils.isNotBlank(req.getMobile()), HealthySubOrder::getMobile, req.getMobile())
                .eq(org.apache.commons.lang3.StringUtils.isNotBlank(req.getAddress()), HealthySubOrder::getAddress, req.getAddress())
                .eq(org.apache.commons.lang3.StringUtils.isNotBlank(req.getOrderStatus()), HealthySubOrder::getOrderStatus, req.getOrderStatus())
                .eq(StringUtils.isNotBlank(req.getPostWay()), HealthySubOrder::getPostWay, req.getPostWay())
                .eq(req.getProvinceId() != null, HealthySubOrder::getProvinceId, req.getProvinceId())
                .eq(req.getCityId() != null, HealthySubOrder::getCityId, req.getCityId())
                .eq(req.getDistrictId() != null, HealthySubOrder::getDistrictId, req.getDistrictId())
                .ge(req.getStartTime() != null, HealthySubOrder::getExpectShipTime, req.getStartTime())
                .like(req.getExpressCompany() != null, HealthySubOrder::getExpressCompany, req.getExpressCompany())
                .like(req.getExpressNo() != null, HealthySubOrder::getExpressNo, req.getExpressNo())
                .lt(req.getEndTime() != null, HealthySubOrder::getExpectShipTime, req.getEndTime());
        List<HealthySubOrder> list = healthySubOrderMapper.selectList(queryWrapper);

        return BeanUtil.convertList(list, HealthySubOrderExportResult.class);
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
