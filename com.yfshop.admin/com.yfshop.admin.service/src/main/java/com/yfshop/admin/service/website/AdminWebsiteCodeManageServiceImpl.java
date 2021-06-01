package com.yfshop.admin.service.website;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.admin.api.website.AdminWebsiteCodeManageService;
import com.yfshop.admin.api.website.WebsiteCodeTaskService;
import com.yfshop.admin.api.website.request.WebsiteCodeExpressReq;
import com.yfshop.admin.api.website.request.WebsiteCodeQueryDetailsReq;
import com.yfshop.admin.api.website.request.WebsiteCodeQueryReq;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailExport;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
import com.yfshop.admin.dao.WebsiteCodeDao;
import com.yfshop.admin.task.OssDownloader;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.DateUtil;
import com.yfshop.wx.api.request.WxPayRefundReq;
import com.yfshop.wx.api.service.MpService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DubboService
public class AdminWebsiteCodeManageServiceImpl implements AdminWebsiteCodeManageService {

    @Resource
    private WebsiteCodeDao websiteCodeDao;
    @Resource
    private WebsiteCodeMapper websiteCodeMapper;
    @Resource
    private WebsiteCodeDetailMapper websiteCodeDetailMapper;
    @Autowired
    private OssDownloader ossDownloader;
    @DubboReference
    private WebsiteCodeTaskService websiteCodeTaskService;
    @Resource
    private WebsiteCodeGroupMapper websiteCodeGroupMapper;
    @Resource
    private WxPayNotifyMapper wxPayNotifyMapper;
    @Resource
    private WxPayRefundMapper wxPayRefundMapper;
    @DubboReference
    private MpService mpService;

    @Override
    public IPage<WebsiteCodeResult> queryWebsiteCodeList(WebsiteCodeQueryReq req) throws ApiException {
        IPage page = new Page<WebsiteCodeQueryReq>(req.getPageIndex(), req.getPageSize());
        List<WebsiteCodeResult> list = websiteCodeDao.queryWebsiteCodeList(page, req);
        page.setTotal(websiteCodeDao.queryWebsiteCodeCount(req));
        page.setRecords(list);
        return page;
    }


    @Override
    public IPage<WebsiteCodeResult> queryWebsiteCodeByOlderWl(WebsiteCodeQueryReq req) throws ApiException {
        List<String> orderStatus = new ArrayList<>();
        if ("UNSHIPPED".equals(req.getOrderStatus())) {
            orderStatus.add("WAIT");
        } else if ("SHIPPED".equals(req.getOrderStatus())) {
            orderStatus.add("DELIVERY");
            orderStatus.add("SUCCESS");
        }
        IPage page = new Page<WebsiteCodeQueryReq>(req.getPageIndex(), req.getPageSize());
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(WebsiteCode.class)
                .eq(StringUtils.isNotBlank(req.getBatchNo()), WebsiteCode::getBatchNo, req.getBatchNo())
                .like(StringUtils.isNotBlank(req.getMerchantName()), WebsiteCode::getMerchantName, req.getMerchantName())
                .like(StringUtils.isNotBlank(req.getExpressNo()), WebsiteCode::getExpressNo, req.getExpressNo())
                .like(StringUtils.isNotBlank(req.getRoleName()), WebsiteCode::getContracts, req.getRoleName())
                .like(StringUtils.isNotBlank(req.getAddress()), WebsiteCode::getAddress, req.getAddress())
                .in(!orderStatus.isEmpty(), WebsiteCode::getOrderStatus, orderStatus)
                .isNotNull(WebsiteCode::getBillno)
                .notIn(WebsiteCode::getOrderStatus, "PENDING", "PAYING", "CANCEL", "CLOSED")
                .ge(req.getStartTime() != null, WebsiteCode::getCreateTime, req.getStartTime())
                .lt(req.getEndTime() != null, WebsiteCode::getCreateTime, req.getEndTime()).orderByDesc(WebsiteCode::getCreateTime);
        IPage<WebsiteCode> websiteCodeIPage = websiteCodeMapper.selectPage(page, queryWrapper);
        return BeanUtil.iPageConvert(websiteCodeIPage, WebsiteCodeResult.class);
    }

    @Override
    public IPage<WebsiteCodeResult> queryWebsiteCodeByWl(WebsiteCodeQueryReq req) throws ApiException {
        List<String> orderStatus = new ArrayList<>();
        if ("UNSHIPPED".equals(req.getOrderStatus())) {
            orderStatus.add("WAIT");
        } else if ("SHIPPED".equals(req.getOrderStatus())) {
            orderStatus.add("DELIVERY");
            orderStatus.add("SUCCESS");
        }
        IPage page = new Page<WebsiteCodeQueryReq>(req.getPageIndex(), req.getPageSize());
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(WebsiteCodeGroup.class)
                .eq(req.getId() != null, WebsiteCodeGroup::getId, req.getId())
                .like(StringUtils.isNotBlank(req.getMerchantName()), WebsiteCodeGroup::getMerchantName, req.getMerchantName())
                .like(StringUtils.isNotBlank(req.getExpressNo()), WebsiteCodeGroup::getExpressNo, req.getExpressNo())
                .like(StringUtils.isNotBlank(req.getAddress()), WebsiteCodeGroup::getAddress, req.getAddress())
                .in(!orderStatus.isEmpty(), WebsiteCodeGroup::getOrderStatus, orderStatus)
                .notIn(WebsiteCodeGroup::getOrderStatus, "PENDING", "PAYING", "CANCEL", "CLOSED")
                .ge(req.getStartTime() != null, WebsiteCodeGroup::getCreateTime, req.getStartTime())
                .lt(req.getEndTime() != null, WebsiteCodeGroup::getCreateTime, req.getEndTime()).orderByDesc(WebsiteCodeGroup::getCreateTime);
        IPage<WebsiteCodeGroup> websiteCodeGroupIPage = websiteCodeGroupMapper.selectPage(page, queryWrapper);
        return BeanUtil.iPageConvert(websiteCodeGroupIPage, WebsiteCodeResult.class);
    }

    @Override
    public IPage<WebsiteCodeDetailResult> queryWebsiteCodeDetailsList(WebsiteCodeQueryDetailsReq req) throws ApiException {
        LambdaQueryWrapper<WebsiteCodeDetail> wrappers = Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .and(req.getMerchantId() != null, wrapper -> wrapper
                        .eq(WebsiteCodeDetail::getPid, req.getMerchantId())
                        .or()
                        .like(WebsiteCodeDetail::getPidPath, "." + req.getMerchantId() + "."))
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
    public List<WebsiteCodeDetailExport> exportWebsiteCodeDetails(WebsiteCodeQueryDetailsReq req) throws ApiException {
        LambdaQueryWrapper<WebsiteCodeDetail> wrappers = Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .and(wrapper -> wrapper
                        .eq(WebsiteCodeDetail::getPid, req.getMerchantId())
                        .or()
                        .like(WebsiteCodeDetail::getPidPath, "." + req.getMerchantId() + "."))
                .eq(StringUtils.isNotBlank(req.getAlias()), WebsiteCodeDetail::getAlias, req.getAlias())
                .eq(req.getBatchId() != null, WebsiteCodeDetail::getBatchId, req.getBatchId())
                .eq(StringUtils.isNotBlank(req.getIsActivate()), WebsiteCodeDetail::getIsActivate, req.getIsActivate())
                .eq(StringUtils.isNotBlank(req.getMobile()), WebsiteCodeDetail::getMobile, req.getMobile())
                .eq(StringUtils.isNotBlank(req.getMerchantName()), WebsiteCodeDetail::getMerchantName, req.getMerchantName());
        List<WebsiteCodeDetail> websiteCodeDetails = websiteCodeDetailMapper.selectList(wrappers);
        List<WebsiteCodeDetailExport> websiteCodeDetailExportList = new ArrayList<>();
        websiteCodeDetails.forEach(item -> {
            WebsiteCodeDetailExport websiteCodeDetailExport = BeanUtil.convert(item, WebsiteCodeDetailExport.class);
            websiteCodeDetailExport.setActivateTime(DateUtil.localDateTimeToDate(item.getActivityTime()));
            websiteCodeDetailExportList.add(websiteCodeDetailExport);
        });
        return websiteCodeDetailExportList;
    }

    @Override
    public Void updateWebsiteCodeExpress(WebsiteCodeExpressReq websiteCodeExpressReq) throws ApiException {
        WebsiteCode websiteCode = BeanUtil.convert(websiteCodeExpressReq, WebsiteCode.class);
        websiteCode.setOrderStatus("DELIVERY");
        websiteCodeMapper.updateById(websiteCode);
        return null;
    }

    @Override
    public String getWebsiteCodeUrl(Integer id) throws ApiException {
        WebsiteCode websiteCode = websiteCodeMapper.selectById(id);
        Asserts.assertStringNotBlank(websiteCode.getFileUrl(), 500, "文件不存在！");
        Integer count = websiteCode.getDownloadCount();
        websiteCode.setDownloadCount(count == null ? 1 : count + 1);
        websiteCodeMapper.updateById(websiteCode);
        return ossDownloader.privateDownloadUrl(websiteCode.getFileUrl(), 60, null);
    }

    @Override
    public Void retryWebsiteCode(Integer websiteCodeId) {
        websiteCodeTaskService.buildWebSiteCode(websiteCodeId);
        return null;
    }

    @Override
    public List<WebsiteCodeResult> queryWebsiteDetailsCodeByWl(String orderNo) {
        LambdaQueryWrapper wrapper = Wrappers.lambdaQuery(WebsiteCode.class)
                .eq(WebsiteCode::getOrderNo, orderNo);
        List<WebsiteCode> websiteCodeList = websiteCodeMapper.selectList(wrapper);
        return BeanUtil.convertList(websiteCodeList, WebsiteCodeResult.class);
    }

    @Override
    public Void updateWebsiteCodeGroupExpress(WebsiteCodeExpressReq websiteCodeExpressReq) {
        WebsiteCodeGroup websiteCodeGroup = BeanUtil.convert(websiteCodeExpressReq, WebsiteCodeGroup.class);
        websiteCodeGroup.setOrderStatus("DELIVERY");
        websiteCodeGroupMapper.updateById(websiteCodeGroup);
        websiteCodeGroup = websiteCodeGroupMapper.selectById(websiteCodeExpressReq.getId());
        if (websiteCodeGroup != null && StringUtils.isNotBlank(websiteCodeGroup.getOrderNo())) {
            WebsiteCode websiteCode = new WebsiteCode();
            websiteCode.setOrderStatus("DELIVERY");
            websiteCode.setExpressName(websiteCodeExpressReq.getExpressName());
            websiteCode.setExpressNo(websiteCodeExpressReq.getExpressNo());
            websiteCodeMapper.update(websiteCode, Wrappers.lambdaQuery(WebsiteCode.class).eq(WebsiteCode::getOrderNo, websiteCodeGroup.getOrderNo()));
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void closeWebsiteCode(Integer id) throws ApiException, WxPayException {
        WebsiteCodeGroup websiteCodeGroup = websiteCodeGroupMapper.selectById(id);
        Asserts.assertNonNull(websiteCodeGroup, 500, "没有查询到改订单");
        String orderStatus = websiteCodeGroup.getOrderStatus();
        Asserts.assertTrue("WAIT".equals(orderStatus) || "DELIVERY".equals(orderStatus), 500, "当前订单不支持关闭");
        websiteCodeGroup.setOrderStatus("CLOSED");
        websiteCodeGroupMapper.updateById(websiteCodeGroup);
        WebsiteCode websiteCode = new WebsiteCode();
        if (StringUtils.isNotBlank(websiteCodeGroup.getBillno())) {
            websiteCode.setOrderStatus("CLOSED");
            websiteCodeMapper.update(websiteCode, Wrappers.lambdaQuery(WebsiteCode.class).eq(WebsiteCode::getBillno, websiteCodeGroup.getBillno()));
            WxPayNotify wxPayNotify = wxPayNotifyMapper.selectOne(Wrappers.lambdaUpdate(WxPayNotify.class).eq(WxPayNotify::getTransactionId, websiteCodeGroup.getBillno()));
            if (wxPayNotify != null) {
                WxPayRefund wxPayRefund = new WxPayRefund();
                wxPayRefund.setCreateTime(LocalDateTime.now());
                wxPayRefund.setOpenId(wxPayNotify.getOpenId());
                BigDecimal totalFee = new BigDecimal(String.valueOf(wxPayNotify.getTotalFee()));
                //扣除0.6%的手续费
                BigDecimal subtractFee = totalFee.multiply(new BigDecimal("0.006")).setScale(0, RoundingMode.CEILING);
                wxPayRefund.setTotalFee(totalFee.subtract(subtractFee).intValue());
                wxPayRefund.setTransactionId(wxPayNotify.getTransactionId());
                wxPayRefund.setOuttradeNo(wxPayNotify.getOuttradeNo());
                wxPayRefund.setRefundNo("refundNo-websiteCode-" + id);
                wxPayRefundMapper.insert(wxPayRefund);
                this.mpService.refund(BeanUtil.convert(wxPayRefund, WxPayRefundReq.class));
            }
        }
        return null;
    }
}
