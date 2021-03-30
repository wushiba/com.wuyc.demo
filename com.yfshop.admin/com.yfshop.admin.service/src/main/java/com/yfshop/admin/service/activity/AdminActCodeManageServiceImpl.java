package com.yfshop.admin.service.activity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.activity.request.ActCodeQueryReq;
import com.yfshop.admin.api.activity.result.ActCodeResult;
import com.yfshop.admin.api.activity.service.AdminActCodeManageService;
import com.yfshop.admin.dao.ActCodeDao;
import com.yfshop.code.mapper.ActCodeBatchMapper;
import com.yfshop.code.model.ActCodeBatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * youshenghui
 * 活动码管理接口
 */
@DubboService
public class AdminActCodeManageServiceImpl implements AdminActCodeManageService {

    @Resource
    private ActCodeBatchMapper actCodeBatchMapper;
    @Resource
    private ActCodeDao actCodeDao;

    @Override
    public IPage<ActCodeResult> queryActCodeList(ActCodeQueryReq req) {
        IPage<ActCodeResult> iPage = new Page(req.getPageIndex(), req.getPageSize());
        List<ActCodeResult> list = actCodeDao.queryActCodeList(iPage, req);
        iPage.setTotal(actCodeBatchMapper.selectCount(Wrappers.<ActCodeBatch>lambdaQuery()
                .eq(req.getActId() != null, ActCodeBatch::getActId, req.getActId())
                .eq(StringUtils.isNotBlank(req.getBatchNo()), ActCodeBatch::getBatchNo, req.getBatchNo())));
        iPage.setRecords(list);
        return iPage;
    }
}