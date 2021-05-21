package com.yfshop.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.draw.request.QueryDrawRecordExportReq;
import com.yfshop.admin.api.draw.result.DrawRecordExportResult;
import com.yfshop.admin.api.draw.service.AdminDrawRecordExportService;
import com.yfshop.code.mapper.DrawRecordMapper;
import com.yfshop.code.model.DrawRecord;
import com.yfshop.common.util.BeanUtil;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class AdminDrawRecordExportServiceImpl implements AdminDrawRecordExportService {
    @Resource
    DrawRecordMapper drawRecordMapper;

    @Override
    public List<DrawRecordExportResult> getDrawRecordExport(QueryDrawRecordExportReq queryDrawRecordReq) {
        LambdaQueryWrapper<DrawRecord> wrapper = Wrappers.lambdaQuery(DrawRecord.class)
                .like(StringUtils.isNotBlank(queryDrawRecordReq.getActTitle()), DrawRecord::getActTitle, queryDrawRecordReq.getActTitle())
                .eq(queryDrawRecordReq.getPrizeLevel() != null, DrawRecord::getPrizeLevel, queryDrawRecordReq.getPrizeLevel())
                .like(StringUtils.isNotBlank(queryDrawRecordReq.getPrizeTitle()), DrawRecord::getPrizeTitle, queryDrawRecordReq.getPrizeTitle())
                .eq(queryDrawRecordReq.getUserId() != null, DrawRecord::getUserId, queryDrawRecordReq.getUserId())
                .like(StringUtils.isNotBlank(queryDrawRecordReq.getUserName()), DrawRecord::getUserName, queryDrawRecordReq.getUserName())
                .like(StringUtils.isNotBlank(queryDrawRecordReq.getLocation()), DrawRecord::getUserLocation, queryDrawRecordReq.getLocation())
                .eq(StringUtils.isNotBlank(queryDrawRecordReq.getUseStatus()), DrawRecord::getUseStatus, queryDrawRecordReq.getUseStatus())
                .eq(StringUtils.isNotBlank(queryDrawRecordReq.getActCode()), DrawRecord::getActCode, queryDrawRecordReq.getActCode())
                .eq(StringUtils.isNotBlank(queryDrawRecordReq.getTraceNo()), DrawRecord::getTraceNo, queryDrawRecordReq.getTraceNo())
                .eq(StringUtils.isNotBlank(queryDrawRecordReq.getSpec()), DrawRecord::getSpec, queryDrawRecordReq.getSpec())
                .like(StringUtils.isNotBlank(queryDrawRecordReq.getDealerName()), DrawRecord::getDealerName, queryDrawRecordReq.getDealerName())
                .like(StringUtils.isNotBlank(queryDrawRecordReq.getDealerAddress()), DrawRecord::getDealerAddress, queryDrawRecordReq.getDealerAddress())
                .ge(queryDrawRecordReq.getStartTime() != null, DrawRecord::getCreateTime, queryDrawRecordReq.getStartTime())
                .lt(queryDrawRecordReq.getEndTime() != null, DrawRecord::getCreateTime, queryDrawRecordReq.getEndTime())
                .orderByDesc(DrawRecord::getId);
        List<DrawRecord> records = drawRecordMapper.selectList(wrapper);
        return BeanUtil.convertList(records, DrawRecordExportResult.class);
    }
}
