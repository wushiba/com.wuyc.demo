package com.yfshop.admin.service.merchant;

import com.google.common.collect.Lists;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.service.merchant.MerchantInfoService;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.req.WebsiteCodeBindReq;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
import com.yfshop.admin.api.website.result.WebsiteTypeResult;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.DateUtil;
import com.yfshop.common.util.GeoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户用户服务
 *
 * @author youshenghui
 * Created in 2021-03-23 9:10
 */
@Validated
@DubboService
public class MerchantInfoServiceImpl implements MerchantInfoService {

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private MerchantDetailMapper merchantDetailMapper;

    @Resource
    private WebsiteCodeDetailMapper websiteCodeDetailMapper;

    @Resource
    private WebsiteCodeMapper websiteCodeMapper;

    @Resource
    private WebsiteTypeMapper websiteTypeMapper;

    @Override
    public MerchantResult getWebsiteInfo(Integer merchantId) throws ApiException {
        Merchant merchant = merchantMapper.selectById(merchantId);
        Asserts.assertNonNull(merchant, 500, "网点信息不存在！");
        MerchantDetail merchantDetail = merchantDetailMapper.selectOne(Wrappers.<MerchantDetail>lambdaQuery()
                .eq(MerchantDetail::getMerchantId, merchantId));
        MerchantResult merchantResult = BeanUtil.convert(merchant, MerchantResult.class);
        if (merchantDetail != null) {
            BeanUtil.copyProperties(merchantDetail, merchantResult);
        }
        return merchantResult;
    }

    @Override
    public List<WebsiteCodeDetailResult> getWebsiteCode(Integer merchantId) throws ApiException {
        List<WebsiteCodeDetail> websiteCodeDetails = websiteCodeDetailMapper.selectList(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getMerchantId, merchantId)
                .orderByDesc(WebsiteCodeDetail::getUpdateTime));
        return BeanUtil.convertList(websiteCodeDetails, WebsiteCodeDetailResult.class);
    }

    @Override
    public List<WebsiteTypeResult> getWebsiteType() throws ApiException {
        List<WebsiteType> websiteTypeList = websiteTypeMapper.selectList(Wrappers.emptyWrapper());
        return BeanUtil.convertList(websiteTypeList, WebsiteTypeResult.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void websiteCodeBind(WebsiteCodeBindReq websiteReq) throws ApiException {
        WebsiteCodeDetail websiteCodeDetail = websiteCodeDetailMapper.selectOne(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getAlias, websiteReq.getWebsiteCode())
                .eq(WebsiteCodeDetail::getIsActivate, 'N'));
        Asserts.assertNonNull(websiteCodeDetail, 500, "网点码已被绑定！");
        Merchant merchant;
        if (websiteReq.getId() == null) {
            merchant = BeanUtil.convert(websiteReq, Merchant.class);
            merchant.setRoleAlias(GroupRoleEnum.WD.getCode());
            merchant.setRoleName(GroupRoleEnum.WD.getDescription());
            merchantMapper.insert(merchant);
            MerchantDetail merchantDetail = BeanUtil.convert(websiteReq, MerchantDetail.class);
            merchantDetail.setMerchantId(merchant.getId());
            merchantDetail.setGeoHash(GeoUtils.toBase32(websiteReq.getLatitude(), websiteReq.getLongitude(), 12));
            merchantDetailMapper.insert(merchantDetail);
        } else {
            merchant = BeanUtil.convert(websiteReq, Merchant.class);
            merchant.setRoleAlias(GroupRoleEnum.WD.getCode());
            MerchantDetail merchantDetail = merchantDetailMapper.selectOne(Wrappers.<MerchantDetail>lambdaQuery()
                    .eq(MerchantDetail::getMerchantId, websiteReq.getId()));
            Integer merchantDetailId = merchantDetail.getId();
            merchantDetail = BeanUtil.convert(websiteReq, MerchantDetail.class);
            merchantDetail.setMerchantId(merchant.getId());
            merchantDetail.setId(merchantDetailId);
            merchantDetailMapper.updateById(merchantDetail);
        }
        Merchant merchantPid = merchantMapper.selectById(websiteCodeDetail.getPid());
        if (merchantPid != null) {
            merchant.setPid(merchantPid.getId());
            merchant.setPidPath(merchantPid.getPidPath() + merchant.getId() + ".");
            merchant.setPMerchantName(merchantPid.getMerchantName());
        } else {
            merchant.setPidPath(merchant.getId() + ".");
        }
        merchantMapper.updateById(merchant);
        websiteCodeDetail.setMerchantId(merchant.getId());
        websiteCodeDetail.setMobile(merchant.getMobile());
        websiteCodeDetail.setIsActivate("Y");
        websiteCodeDetailMapper.updateById(websiteCodeDetail);
        return null;
    }

    @Override
    public List<WebsiteCodeDetailResult> getMyWebsiteCode(Integer merchantId, String status, Date dateTime) throws ApiException {
        Date nextDate = null;
        if (dateTime != null) {
            nextDate = DateUtil.plusDays(dateTime, 1);
        }
        List<WebsiteCodeDetail> websiteCodeDetails = websiteCodeDetailMapper.selectList(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .like(WebsiteCodeDetail::getPidPath, merchantId)
                .eq(WebsiteCodeDetail::getIsActivate, status)
                .ge(dateTime != null, WebsiteCodeDetail::getCreateTime, dateTime)
                .lt(nextDate != null, WebsiteCodeDetail::getCreateTime, nextDate)
                .orderByDesc(WebsiteCodeDetail::getUpdateTime));
        return BeanUtil.convertList(websiteCodeDetails, WebsiteCodeDetailResult.class);
    }

    @Override
    public IPage<WebsiteCodeResult> getApplyWebsiteCode(Integer merchantId, String status, Integer pageIndex, Integer pageSize) {
        List<String> allStatus = new ArrayList<>();
        if ("ALL".equals(status)) {
            allStatus.add("WAIT");
            allStatus.add("DELIVERY");
            allStatus.add("SUCCESS");
        }
        LambdaQueryWrapper<WebsiteCode> lambdaQueryWrapper = Wrappers.<WebsiteCode>lambdaQuery()
                .like(WebsiteCode::getPidPath, merchantId)
                .in(CollectionUtil.isNotEmpty(allStatus), WebsiteCode::getOrderStatus, allStatus)
                .eq(StringUtils.isNotBlank(status) && !"ALL".equals(status), WebsiteCode::getOrderStatus, allStatus)
                .orderByDesc(WebsiteCode::getId);
        IPage<WebsiteCode> websiteCodeIPage = websiteCodeMapper.selectPage(new Page<>(pageIndex, pageSize), lambdaQueryWrapper);
        List<WebsiteCodeResult> results = BeanUtil.convertList(websiteCodeIPage.getRecords(), WebsiteCodeResult.class);
        return iPage;
    }

}
