package com.yfshop.admin.service.draw;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.draw.request.QueryDrawRecordReq;
import com.yfshop.admin.api.draw.result.DrawRecordResult;
import com.yfshop.admin.api.draw.service.AdminDrawRecordService;
import com.yfshop.code.mapper.DrawRecordMapper;
import com.yfshop.code.model.DrawRecord;
import com.yfshop.common.util.BeanUtil;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class AdminDrawRecordServiceImpl implements AdminDrawRecordService {
    @Resource
    DrawRecordMapper drawRecordMapper;

    @Override
    public IPage<DrawRecordResult> getDrawRecordList(QueryDrawRecordReq queryDrawRecordReq) {
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
        IPage<DrawRecord> iPage = drawRecordMapper.selectPage(new Page<>(queryDrawRecordReq.getPageIndex(), queryDrawRecordReq.getPageSize()), wrapper);
        IPage<DrawRecordResult> result = BeanUtil.iPageConvert(iPage, DrawRecordResult.class);
        //result.setTotal(drawRecordMapper.selectCount(wrapper));
        return result;
    }
}
