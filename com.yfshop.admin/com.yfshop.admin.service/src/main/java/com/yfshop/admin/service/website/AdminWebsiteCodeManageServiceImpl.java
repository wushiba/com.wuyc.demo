package com.yfshop.admin.service.website;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.website.AdminWebsiteCodeManageService;
import com.yfshop.admin.api.website.req.WebsiteCodeQueryDetailsReq;
import com.yfshop.admin.api.website.req.WebsiteCodeQueryReq;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailExport;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
import com.yfshop.admin.dao.WebsiteCodeDao;
import com.yfshop.code.mapper.WebsiteCodeDetailMapper;
import com.yfshop.code.model.WebsiteCodeDetail;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class AdminWebsiteCodeManageServiceImpl implements AdminWebsiteCodeManageService {

    @Resource
    private WebsiteCodeDao websiteCodeDao;
    @Resource
    private WebsiteCodeDetailMapper websiteCodeDetailMapper;

    @Override
    public IPage<WebsiteCodeResult> queryWebsiteCodeList(WebsiteCodeQueryReq req) {
        IPage page = new Page<WebsiteCodeQueryReq>(req.getPageIndex(), req.getPageSize());
        List<WebsiteCodeResult> list = websiteCodeDao.queryWebsiteCodeList(page, req);
        page.setTotal(websiteCodeDao.queryWebsiteCodeCount(req));
        page.setRecords(list);
        return page;
    }

    @Override
    public IPage<WebsiteCodeDetailResult> queryWebsiteCodeDetailsList(WebsiteCodeQueryDetailsReq req) {
        LambdaQueryWrapper<WebsiteCodeDetail> wrappers = Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .and(wrapper -> wrapper
                        .eq(WebsiteCodeDetail::getPid, req.getMerchantId())
                        .or()
                        .like(WebsiteCodeDetail::getPidPath, req.getMerchantId()))
                .eq(StringUtils.isNotBlank(req.getAlias()), WebsiteCodeDetail::getAlias, req.getAlias())
                .eq(req.getBatchId() != null, WebsiteCodeDetail::getBatchId, req.getBatchId())
                .eq(StringUtils.isNotBlank(req.getIsActivate()), WebsiteCodeDetail::getIsActivate, req.getIsActivate())
                .eq(StringUtils.isNotBlank(req.getMobile()), WebsiteCodeDetail::getMobile, req.getMobile())
                .eq(StringUtils.isNotBlank(req.getMerchantName()), WebsiteCodeDetail::getMerchantName, req.getMerchantName());

        IPage<WebsiteCodeDetail> websiteCodeDetailIPage = websiteCodeDetailMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()),
                wrappers);
        websiteCodeDetailIPage.setTotal(websiteCodeDetailMapper.selectCount(wrappers));
        return BeanUtil.iPageConvert(websiteCodeDetailIPage, WebsiteCodeDetailResult.class);
    }

    @Override
    public List<WebsiteCodeDetailExport> exportWebsiteCodeDetails(WebsiteCodeQueryDetailsReq req) {
        LambdaQueryWrapper<WebsiteCodeDetail> wrappers = Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .and(wrapper -> wrapper
                        .eq(WebsiteCodeDetail::getPid, req.getMerchantId())
                        .or()
                        .like(WebsiteCodeDetail::getPidPath, req.getMerchantId()))
                .eq(StringUtils.isNotBlank(req.getAlias()), WebsiteCodeDetail::getAlias, req.getAlias())
                .eq(req.getBatchId() != null, WebsiteCodeDetail::getBatchId, req.getBatchId())
                .eq(StringUtils.isNotBlank(req.getIsActivate()), WebsiteCodeDetail::getIsActivate, req.getIsActivate())
                .eq(StringUtils.isNotBlank(req.getMobile()), WebsiteCodeDetail::getMobile, req.getMobile())
                .eq(StringUtils.isNotBlank(req.getMerchantName()), WebsiteCodeDetail::getMerchantName, req.getMerchantName());
        List<WebsiteCodeDetail> websiteCodeDetails = websiteCodeDetailMapper.selectList(wrappers);
        return BeanUtil.convertList(websiteCodeDetails, WebsiteCodeDetailExport.class);
    }
}
