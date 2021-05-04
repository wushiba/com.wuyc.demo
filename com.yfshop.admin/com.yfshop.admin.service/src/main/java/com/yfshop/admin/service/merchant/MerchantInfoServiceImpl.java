package com.yfshop.admin.service.merchant;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.yfshop.admin.api.merchant.MerchantInfoService;
import com.yfshop.admin.api.merchant.request.MerchantGroupReq;
import com.yfshop.admin.api.merchant.request.MerchantReq;
import com.yfshop.admin.api.merchant.result.MerchantGroupResult;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.WebsiteCodeTaskService;
import com.yfshop.admin.api.website.request.WebsiteCodeAddressReq;
import com.yfshop.admin.api.website.request.WebsiteCodeBindReq;
import com.yfshop.admin.api.website.request.WebsiteCodeDataReq;
import com.yfshop.admin.api.website.request.WebsiteCodePayReq;
import com.yfshop.admin.api.website.result.*;
import com.yfshop.admin.dao.WebsiteCodeDao;
import com.yfshop.admin.dao.WebsiteGoodsRecordDao;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.enums.PayPrefixEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.AddressUtil;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.DateUtil;
import com.yfshop.common.util.GeoUtils;
import com.yfshop.wx.api.request.WxPayOrderNotifyReq;
import com.yfshop.wx.api.service.MpPayService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    private WebsiteCodeAddressMapper websiteCodeAddressMapper;

    @Resource
    private WebsiteTypeMapper websiteTypeMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private WebsiteBillMapper websiteBillMapper;

    @DubboReference
    private WebsiteCodeTaskService websiteCodeTask;

    @DubboReference
    private MpPayService mpPayService;

    @Value("${wxPay.notifyUrl}")
    private String wxPayNotifyUrl;
    @Resource
    private WebsiteCodeDao websiteCodeDao;

    @Resource
    private WebsiteGoodsRecordDao websiteGoodsRecordDao;

    @Override
    public MerchantResult getWebsiteInfo(Integer merchantId) throws ApiException {
        Merchant merchant = merchantMapper.selectById(merchantId);
        Asserts.assertNonNull(merchant, 500, "网点信息不存在！");
        MerchantDetail merchantDetail = merchantDetailMapper.selectOne(Wrappers.<MerchantDetail>lambdaQuery()
                .eq(MerchantDetail::getMerchantId, merchantId));
        MerchantResult merchantResult = BeanUtil.convert(merchant, MerchantResult.class);
        if (merchantDetail != null) {
            BeanUtil.copyProperties(merchantDetail, merchantResult);
            merchantResult.setId(merchant.getId());
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
    public MerchantResult websiteCodeBind(WebsiteCodeBindReq websiteReq) throws ApiException {
        buildAddress(websiteReq);
        WebsiteCodeDetail websiteCodeDetail = websiteCodeDetailMapper.selectOne(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getAlias, websiteReq.getWebsiteCode())
                .eq(WebsiteCodeDetail::getIsActivate, 'N'));
        Asserts.assertNonNull(websiteCodeDetail, 500, "网点码已被绑定！");
        Merchant merchant = null;
        if (websiteReq.getId() == null) {
            merchant = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery()
                    .eq(Merchant::getMobile, websiteReq.getMobile()));
        } else {
            merchant = merchantMapper.selectById(websiteReq.getId());
        }
        Integer merchantId;
        String headImageUrl = null;
        if (StringUtils.isNotBlank(websiteReq.getOpenId())) {
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                    .eq(User::getOpenId, websiteReq.getOpenId()));
            if (user != null) {
                headImageUrl = user.getHeadImgUrl();
            }
        }
        if (merchant == null) {
            merchant = BeanUtil.convert(websiteReq, Merchant.class);
            merchant.setRoleAlias(GroupRoleEnum.WD.getCode());
            merchant.setRoleName(GroupRoleEnum.WD.getDescription());
            merchant.setPassword(SecureUtil.md5(SecureUtil.md5("123456")));
            merchant.setHeadImgUrl(headImageUrl);
            merchantMapper.insert(merchant);
            merchantId = merchant.getId();
            MerchantDetail merchantDetail = BeanUtil.convert(websiteReq, MerchantDetail.class);
            merchantDetail.setMerchantId(merchant.getId());
            merchantDetail.setGeoHash(GeoUtils.toBase32(websiteReq.getLatitude(), websiteReq.getLongitude(), 12));
            merchantDetailMapper.insert(merchantDetail);
        } else {
            merchantId = merchant.getId();
            merchant.setHeadImgUrl(headImageUrl);
            Asserts.assertEquals(merchant.getRoleAlias(), GroupRoleEnum.WD.getCode(), 500, "改手机号已经存在其他身份，请更换网点对应手机号！");
            Asserts.assertEquals(merchant.getMobile(), websiteReq.getMobile(), 500, "手机号不允许被修改！");
            String openId = merchant.getOpenId();
            merchant = BeanUtil.convert(websiteReq, Merchant.class);
            if (StringUtils.isNotBlank(openId)) {
                //只保存第一次的openId,不更新openId
                merchant.setOpenId(null);
            }
            merchant.setRoleAlias(GroupRoleEnum.WD.getCode());
            merchant.setRoleName(GroupRoleEnum.WD.getDescription());
            MerchantDetail merchantDetail = merchantDetailMapper.selectOne(Wrappers.<MerchantDetail>lambdaQuery()
                    .eq(MerchantDetail::getMerchantId, websiteReq.getId()));
            if (merchantDetail != null) {
                Integer merchantDetailId = merchantDetail.getId();
                merchantDetail = BeanUtil.convert(websiteReq, MerchantDetail.class);
                merchantDetail.setMerchantId(merchant.getId());
                merchantDetail.setId(merchantDetailId);
                merchantDetailMapper.updateById(merchantDetail);
            } else {
                merchantDetail = BeanUtil.convert(websiteReq, MerchantDetail.class);
                merchantDetail.setMerchantId(merchant.getId());
                merchantDetailMapper.insert(merchantDetail);
            }
        }
        Merchant merchantPid = merchantMapper.selectById(websiteCodeDetail.getPid());
        if (merchantPid != null) {
            merchant.setPid(merchantPid.getId());
            merchant.setPidPath(merchantPid.getPidPath() + merchant.getId() + ".");
            merchant.setPMerchantName(merchantPid.getMerchantName());
        } else {
            merchant.setPidPath(merchant.getId() + ".");
        }
        if (StringUtils.isNotBlank(websiteReq.getOpenId())) {
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                    .eq(User::getOpenId, websiteReq.getOpenId()));
            if (user != null) {
                merchant.setHeadImgUrl(user.getHeadImgUrl());
            }
        }
        merchant.setId(merchantId);
        merchantMapper.updateById(merchant);
        websiteCodeDetail.setMerchantId(merchant.getId());
        websiteCodeDetail.setActivityTime(LocalDateTime.now());
        websiteCodeDetail.setMerchantName(merchant.getMerchantName());
        websiteCodeDetail.setMobile(merchant.getMobile());
        websiteCodeDetail.setIsActivate("Y");
        websiteCodeDetailMapper.updateById(websiteCodeDetail);
        return BeanUtil.convert(merchant, MerchantResult.class);
    }


    public void buildAddress(WebsiteCodeBindReq websiteCodeBindReq) {
        if (StringUtils.isNotBlank(websiteCodeBindReq.getAddress())) {
            Map<String, String> maps = AddressUtil.addressResolution(websiteCodeBindReq.getAddress());
                websiteCodeBindReq.setProvince(maps.get("province"));
                Region province = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                        .eq(Region::getName, maps.get("province")));
                if (province != null) {
                    websiteCodeBindReq.setProvinceId(province.getId());
                    Region city = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                            .eq(Region::getName, maps.get("city"))
                            .eq(Region::getPid, province.getId()));
                    if (city != null) {
                        websiteCodeBindReq.setCityId(city.getId());
                        Region county = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                                .eq(Region::getName, maps.get("county"))
                                .eq(Region::getPid, city.getId()));
                        if (county != null) {
                            websiteCodeBindReq.setDistrictId(county.getId());
                        }
                    }
                }
                websiteCodeBindReq.setCity(maps.get("city"));
                websiteCodeBindReq.setDistrict(maps.get("county"));
                websiteCodeBindReq.setAddress(maps.get("town"));
            }
    }

    @Override
    public List<WebsiteCodeDetailResult> getMyWebsiteCode(Integer merchantId, String status, Date startTime,Date endTime) throws ApiException {
        List<WebsiteCodeDetail> websiteCodeDetails = websiteCodeDetailMapper.selectList(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .and(itemWrapper -> itemWrapper
                        .eq(WebsiteCodeDetail::getMerchantId, merchantId)
                        .or()
                        .like(WebsiteCodeDetail::getPidPath, merchantId + "."))
                .eq(WebsiteCodeDetail::getIsActivate, status)
                .ge(startTime != null, WebsiteCodeDetail::getCreateTime, startTime)
                .lt(endTime != null, WebsiteCodeDetail::getCreateTime, endTime)
                .orderByDesc(WebsiteCodeDetail::getUpdateTime));
        return BeanUtil.convertList(websiteCodeDetails, WebsiteCodeDetailResult.class);
    }

    @Override
    public IPage<WebsiteCodeResult> applyWebsiteCodeStatus(Integer merchantId, String status, Integer pageIndex, Integer pageSize) {
        List<String> allStatus = new ArrayList<>();
        if ("ALL".equals(status)) {
            allStatus.add("WAIT");
            allStatus.add("DELIVERY");
            allStatus.add("SUCCESS");
        }else if ("PENDING".equals(status)) {
            allStatus.add("PENDING");
            allStatus.add("PAYING");
        }else{
            allStatus.add("status");
        }
        LambdaQueryWrapper<WebsiteCode> lambdaQueryWrapper = Wrappers.<WebsiteCode>lambdaQuery()
                .and(itemWrapper -> itemWrapper
                        .eq(WebsiteCode::getMerchantId, merchantId)
                        .or()
                        .like(WebsiteCode::getPidPath, merchantId + "."))
                .in(CollectionUtil.isNotEmpty(allStatus), WebsiteCode::getOrderStatus, allStatus)
                .orderByDesc(WebsiteCode::getId);
        IPage<WebsiteCode> websiteCodeIPage = websiteCodeMapper.selectPage(new Page<>(pageIndex, pageSize), lambdaQueryWrapper);
        return BeanUtil.iPageConvert(websiteCodeIPage, WebsiteCodeResult.class);
    }

    @Override
    public Void updateApplyWebsiteCode(Integer id, String status) throws ApiException {
        WebsiteCode websiteCode = new WebsiteCode();
        websiteCode.setOrderStatus(status);
        websiteCode.setId(id);
        websiteCodeMapper.updateById(websiteCode);
        return null;
    }

    @Override
    public Integer applyWebsiteCode(Integer merchantId, Integer count, String email) throws ApiException {
        WebsiteCode last = websiteCodeMapper.selectOne(Wrappers.<WebsiteCode>lambdaQuery().eq(WebsiteCode::getMerchantId, merchantId).orderByDesc());
        if (last != null) {
            int sumCount = websiteCodeDao.sumWebsiteCodeByBeforeId(last.getId(), merchantId);
            sumCount = sumCount + count;
            Asserts.assertTrue(sumCount < 10000, 500, "提示网点码申请超限，无法提交申请!");
        }
        Merchant merchant = merchantMapper.selectById(merchantId);
        WebsiteCode websiteCode = new WebsiteCode();
        websiteCode.setCreateTime(LocalDateTime.now());
        websiteCode.setEmail(email);
        websiteCode.setMerchantId(merchant.getId());
        websiteCode.setMerchantName(merchant.getMerchantName());
        websiteCode.setMobile(merchant.getMobile());
        websiteCode.setPidPath(merchant.getPidPath());
        websiteCode.setQuantity(count);
        if (StringUtils.isBlank(email)) {
            websiteCode.setOrderAmount(new BigDecimal("0.35").multiply(new BigDecimal(count)));
            websiteCode.setPostage(new BigDecimal(8));
            float g = new BigDecimal(count).multiply(new BigDecimal("4.3")).floatValue();//每张大约4.3g
            if (g > 1000) {
                double amount = Math.ceil((g - 1000) / 1000f) * 2.5d;
                websiteCode.setPostage(websiteCode.getPostage().add(new BigDecimal(String.valueOf(amount))));
            }
        } else {
            websiteCode.setOrderStatus("SUCCESS");
        }
        websiteCode.setBatchNo(cn.hutool.core.date.DateUtil.format(new Date(), "yyMMddHHmmssSSS") + RandomUtil.randomNumbers(4));
        websiteCodeMapper.insert(websiteCode);
        if (StringUtils.isNotBlank(email)) {
            websiteCodeTask.buildWebSiteCode(websiteCode.getId());
        }
        return websiteCode.getId();
    }

    @Override
    public Void websiteCodeAddress(WebsiteCodeAddressReq websiteCodeAddressReq) throws ApiException {
        WebsiteCodeAddress websiteCodeAddress = BeanUtil.convert(websiteCodeAddressReq, WebsiteCodeAddress.class);
        if (websiteCodeAddress.getId() == null) {
            int count = websiteCodeAddressMapper.selectCount(Wrappers.<WebsiteCodeAddress>lambdaQuery()
                    .eq(WebsiteCodeAddress::getMerchantId, websiteCodeAddress.getMerchantId()));
            if (count == 0) {
                websiteCodeAddress.setIsDefault("Y");
            }
            buildAddress(websiteCodeAddress);
            websiteCodeAddressMapper.insert(websiteCodeAddress);
        } else {
            if ("N".equals(websiteCodeAddress.getIsDefault())) {
                WebsiteCodeAddress address = websiteCodeAddressMapper.selectById(websiteCodeAddress.getId());
                Asserts.assertEquals("N", address.getIsDefault(), 500, "不允许取消默认地址");
            }
            buildAddress(websiteCodeAddress);
            websiteCodeAddressMapper.updateById(websiteCodeAddress);
        }
        if ("Y".equals(websiteCodeAddress.getIsDefault())) {
            WebsiteCodeAddress newWebsiteCodeAddress = new WebsiteCodeAddress();
            newWebsiteCodeAddress.setIsDefault("N");
            websiteCodeAddressMapper.update(newWebsiteCodeAddress, Wrappers.<WebsiteCodeAddress>lambdaQuery()
                    .eq(WebsiteCodeAddress::getMerchantId, websiteCodeAddress.getMerchantId())
                    .ne(WebsiteCodeAddress::getId, websiteCodeAddress.getId()));
        }
        return null;
    }

    public void buildAddress(WebsiteCodeAddress websiteCodeAddress) throws ApiException {
        if (websiteCodeAddress.getDistrictId() == null) return;
        Region district = regionMapper.selectById(websiteCodeAddress.getDistrictId());
        if (district == null) return;
        websiteCodeAddress.setDistrict(district.getName());
        Region city = regionMapper.selectById(district.getPid());
        if (city == null) return;
        websiteCodeAddress.setCityId(city.getId());
        websiteCodeAddress.setCity(city.getName());
        Region province = regionMapper.selectById(city.getPid());
        if (province == null) return;
        websiteCodeAddress.setProvinceId(province.getId());
        websiteCodeAddress.setProvince(province.getName());
    }

    @Override
    public List<WebsiteCodeAddressResult> getWebsiteCodeAddress(Integer merchantId) throws ApiException {
        List<WebsiteCodeAddress> websiteCodeAddresses = websiteCodeAddressMapper.selectList(Wrappers.<WebsiteCodeAddress>lambdaQuery()
                .eq(WebsiteCodeAddress::getMerchantId, merchantId)
                .orderByDesc(WebsiteCodeAddress::getIsDefault, WebsiteCodeAddress::getId));
        return BeanUtil.convertList(websiteCodeAddresses, WebsiteCodeAddressResult.class);
    }

    @Override
    public Void deleteWebsiteCodeAddress(Integer id) {
        websiteCodeAddressMapper.deleteById(id);
        return null;
    }

    @Override
    public WebsiteCodeAmountResult applyWebsiteCodeAmount(List<Integer> ids) throws ApiException {
        WebsiteCodeAmountResult websiteCodeAmountResult = new WebsiteCodeAmountResult();
        websiteCodeAmountResult.setAmount(new BigDecimal(0));
        websiteCodeAmountResult.setPostage(new BigDecimal(0));
        websiteCodeAmountResult.setQuantity(0);
        if (!CollectionUtil.isEmpty(ids)) {
            List<WebsiteCode> websiteCodes = websiteCodeMapper.selectBatchIds(ids);
            websiteCodes.forEach(item -> {
                websiteCodeAmountResult.setAmount(websiteCodeAmountResult.getAmount().add(item.getOrderAmount()));
                websiteCodeAmountResult.setPostage(websiteCodeAmountResult.getPostage().add(item.getPostage()));
                websiteCodeAmountResult.setQuantity(websiteCodeAmountResult.getQuantity() + item.getQuantity());
            });
        }
        return websiteCodeAmountResult;
    }

    @Override
    public WebsiteCodeResult applyWebsiteCodeDetails(Integer id) throws ApiException {
        return BeanUtil.convert(websiteCodeMapper.selectById(id), WebsiteCodeResult.class);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxPayMpOrderResult applyWebsiteCodePay(WebsiteCodePayReq websiteCodePayReq) throws ApiException {
        WebsiteCodeAddress websiteCodeAddress = websiteCodeAddressMapper.selectById(websiteCodePayReq.getAddressId());
        WebsiteCode websiteCode = new WebsiteCode();
        websiteCode.setMobile(websiteCodeAddress.getMobile());
        websiteCode.setContracts(websiteCodeAddress.getContracts());
        websiteCode.setAddress(websiteCodeAddress.getProvince() + websiteCodeAddress.getCity() + websiteCodeAddress.getDistrict() + websiteCodeAddress.getAddress());
        websiteCode.setOrderNo(String.format("%06d", websiteCodeAddress.getMerchantId()) + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS")));
        websiteCode.setOrderStatus("PAYING");
        int count = websiteCodeMapper.update(websiteCode, Wrappers.<WebsiteCode>lambdaQuery()
                .in(WebsiteCode::getId, websiteCodePayReq.getIds())
                .in(WebsiteCode::getOrderStatus, "PENDING","WAIT"));
        Asserts.assertTrue(count > 0, 500, "没有要支付的订单！");
        WebsiteCodeAmountResult websiteCodeAmountResult = applyWebsiteCodeAmount(websiteCodePayReq.getIds());
        String fee = websiteCodeAmountResult.getAmount().add(websiteCodeAmountResult.getPostage()).toPlainString();
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody("网点码申请");
        orderRequest.setOutTradeNo(websiteCode.getOrderNo());
        orderRequest.setNotifyUrl(wxPayNotifyUrl + PayPrefixEnum.WEBSITE_CODE.getBizType());
        if ("pro".equalsIgnoreCase(SpringUtil.getActiveProfile())) {
            orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen(fee));
        } else {
            orderRequest.setTotalFee(1);
        }
        orderRequest.setOpenid(websiteCodePayReq.getOpenId());
        orderRequest.setTradeType("JSAPI");
        orderRequest.setSpbillCreateIp(websiteCodePayReq.getUserId());
        orderRequest.setTimeStart(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        orderRequest.setTimeExpire(DateFormatUtils.format(new Date(System.currentTimeMillis() + (1000 * 60 * 15)), "yyyyMMddHHmmss"));
        try {
            return mpPayService.createPayOrder(orderRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Asserts.fail(500, "调用支付接口失败！");
        }
        return null;
    }

    @Override
    public Integer checkWebsiteCode(String websiteCode) throws ApiException {
        return websiteCodeDetailMapper.selectCount(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getAlias, websiteCode)
                .eq(WebsiteCodeDetail::getIsActivate, "Y"));
    }

    @Override
    public MerchantResult getMerchantByWebsiteCode(String websiteCode) throws ApiException {
        WebsiteCodeDetail websiteCodeDetail = websiteCodeDetailMapper.selectOne(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getAlias, websiteCode));
        if (websiteCodeDetail != null) {
            try {
                return getWebsiteInfo(websiteCodeDetail.getMerchantId());
            } catch (Exception e) {

            }
        }
        return null;
    }

    @Override
    public MerchantResult getMerchantByOpenId(String openId) throws ApiException {
        MerchantResult merchantResult = null;
        Merchant merchant = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getOpenId, openId));
        if (merchant != null) {
            merchantResult = BeanUtil.convert(merchant, MerchantResult.class);
            MerchantDetail merchantDetail = merchantDetailMapper.selectOne(Wrappers.<MerchantDetail>lambdaQuery()
                    .eq(MerchantDetail::getMerchantId, merchant.getId()));
            if (merchantDetail != null) {
                BeanUtil.copyProperties(merchantDetail, merchantResult);
                merchantResult.setId(merchant.getId());
            }
        }
        return merchantResult;
    }

    @Override
    public void websitePayOrderNotify(WxPayOrderNotifyReq notifyResult) throws ApiException {
        WebsiteCode websiteCode = new WebsiteCode();
        websiteCode.setPayMethod("WxPay");
        websiteCode.setPayTime(LocalDateTime.now());
        websiteCode.setOrderStatus("WAIT");
        websiteCode.setBillno(notifyResult.getTransactionId());
        int count = websiteCodeMapper.update(websiteCode, Wrappers.<WebsiteCode>lambdaQuery()
                .eq(WebsiteCode::getOrderNo, notifyResult.getOutTradeNo())
                .eq(WebsiteCode::getOrderStatus, "PAYING"));
        if (count > 0) {
            websiteCodeTask.doWorkWebsiteCodeFile(notifyResult.getOutTradeNo());
        }
    }


    @Override
    public MerchantGroupResult merchantGroup(MerchantGroupReq merchantGroupReq) throws ApiException {
        MerchantGroupResult merchantGroupResult = new MerchantGroupResult();
        Integer myselfCount = getCurrentWebsiteCount(merchantGroupReq.getMerchantId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime());
        Integer count = getAllWebsiteCodeCount(merchantGroupReq.getMerchantId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime());
        Merchant merchant = merchantMapper.selectById(merchantGroupReq.getMerchantId());
        merchantGroupResult.setMerchantId(merchant.getId());
        merchantGroupResult.setMerchantName(merchant.getMerchantName());
        merchantGroupResult.setCount(count);
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getPid, merchantGroupReq.getMerchantId())
                .ne(Merchant::getRoleAlias,"wd")
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N");
        List<Merchant> merchantList = merchantMapper.selectList(lambdaQueryWrapper);
        List<MerchantGroupResult> merchantGroupResults = new ArrayList<>();
        if (myselfCount > 0) {
            MerchantGroupResult myself = new MerchantGroupResult();
            myself.setHaveWebsite(true);
            myself.setMerchantId(merchant.getId());
            myself.setMerchantName(merchant.getMerchantName());
            myself.setCurrentExchange(getCurrentExchange(merchant.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            myself.setTotalExchange(getCurrentExchange(merchant.getId(),null,null));
            myself.setCurrentGoodsRecord(websiteGoodsRecordDao.sumCurrentGoodsRecord(merchant.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            myself.setCount(count);
            merchantGroupResults.add(myself);
        }
        merchantList.forEach(item -> {
            MerchantGroupResult child = new MerchantGroupResult();
            child.setMerchantId(item.getId());
            child.setMerchantName(item.getMerchantName());
            child.setCurrentExchange(getCurrentExchange(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            child.setTotalExchange(getCurrentExchange(item.getId(),null,null));
            child.setCurrentGoodsRecord(websiteGoodsRecordDao.sumAllGoodsRecord(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            child.setCount(getAllWebsiteCodeCount(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            merchantGroupResults.add(child);
        });
        merchantGroupResult.setList(merchantGroupResults);
        return merchantGroupResult;
    }

    @Override
    public Void save(MerchantReq merchantReq) throws ApiException {
        Merchant m = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery().eq(Merchant::getMobile, merchantReq.getMobile()));
        if (merchantReq.getMerchantId() == null) {
            Asserts.assertNull(m, 500, "手机号已经被注册！");
            Merchant pMerchant = merchantMapper.selectById(merchantReq.getPId());
            Merchant merchant = new Merchant();
            merchant.setPid(merchantReq.getPId());
            merchant.setPMerchantName(pMerchant.getMerchantName());
            merchant.setRoleAlias(merchantReq.getRoleAlias());
            merchant.setRoleName(GroupRoleEnum.getByCode(merchantReq.getRoleAlias()).getDescription());
            merchant.setMerchantName(merchantReq.getMerchantName());
            merchant.setMobile(merchantReq.getMobile());
            merchant.setPassword(SecureUtil.md5(StringUtils.isBlank(merchantReq.getPassword()) ? "123456" : merchantReq.getPassword()));
            merchant.setContacts(merchantReq.getContacts());
            merchant.setProvince(pMerchant.getProvince());
            merchant.setCity(pMerchant.getCity());
            merchant.setDistrict(pMerchant.getDistrict());
            merchant.setProvinceId(pMerchant.getProvinceId());
            merchant.setCityId(pMerchant.getCityId());
            merchant.setDistrictId(pMerchant.getDistrictId());
            merchantMapper.insert(merchant);
            merchant.setPidPath(pMerchant.getPidPath() + merchant.getId() + ".");
            merchantMapper.updateById(merchant);
        } else {
            Asserts.assertEquals(m.getMobile(), merchantReq.getMobile(), 500, "手机号不能被修改！");
            Merchant merchant = new Merchant();
            merchant.setId(merchantReq.getMerchantId());
            merchant.setRoleAlias(merchantReq.getRoleAlias());
            merchant.setRoleName(GroupRoleEnum.getByCode(merchantReq.getRoleAlias()).getDescription());
            merchant.setMerchantName(merchantReq.getMerchantName());
            if (StringUtils.isNotBlank(merchantReq.getPassword())) {
                merchant.setPassword(SecureUtil.md5(merchantReq.getPassword()));
            }
            merchant.setContacts(merchantReq.getContacts());
            merchant.setIsEnable("Y");
            merchant.setIsDelete("N");
            merchantMapper.updateById(merchant);
        }
        return null;
    }

    @Override
    public List<MerchantResult> getChildMerchant(Integer merchantId) throws ApiException {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getPid, merchantId)
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N")
                .orderByDesc(Merchant::getId);
        List<Merchant> list = merchantMapper.selectList(lambdaQueryWrapper);
        return BeanUtil.convertList(list, MerchantResult.class);
    }

    @Override
    public Integer checkActivate(String websiteCode) {
        WebsiteCodeDetail websiteCodeDetail = websiteCodeDetailMapper.selectOne(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getAlias, websiteCode)
                .eq(WebsiteCodeDetail::getIsActivate, "Y"));
        return websiteCodeDetail == null ? 0 : 1;
    }

    @Override
    public void cancelWebsiteCodePay(WebsiteCodePayReq websiteCodePayReq) {
        WebsiteCode websiteCode = new WebsiteCode();
        websiteCode.setOrderStatus("PENDING");
        websiteCodeMapper.update(websiteCode, Wrappers.<WebsiteCode>lambdaQuery()
                .in(WebsiteCode::getId, websiteCodePayReq.getIds())
                .eq(WebsiteCode::getOrderStatus, "PAYING"));

    }

    @Override
    public MerchantGroupResult getWebsiteList(MerchantGroupReq merchantGroupReq) {
        MerchantGroupResult merchantGroupResult=new MerchantGroupResult();
        AtomicInteger count= new AtomicInteger();
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getPid, merchantGroupReq.getMerchantId())
                .eq(Merchant::getRoleAlias, "wd")
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N");
        List<Merchant> merchantList = merchantMapper.selectList(lambdaQueryWrapper);
        List<MerchantGroupResult> merchantGroupResults = new ArrayList<>();
        merchantList.forEach(item -> {
            MerchantGroupResult child = new MerchantGroupResult();
            child.setMerchantId(item.getId());
            child.setMerchantName(item.getMerchantName());
            child.setCurrentExchange(getCurrentExchangeByPid(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            child.setTotalExchange(getCurrentExchangeByPid(item.getId(),null,null));
            child.setCurrentGoodsRecord(websiteGoodsRecordDao.sumGoodsRecordByMerchantId(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            child.setCount(getCurrentWebsiteCodeCount(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            count.addAndGet(child.getCount());
            merchantGroupResults.add(child);
        });
        merchantGroupResult.setCount(count.get());
        merchantGroupResult.setList(merchantGroupResults);
        return merchantGroupResult;
    }


    @Override
    public WebsiteCodeGroupResult getWebsiteCodeData(WebsiteCodeDataReq websiteCodeDataReq) {
        WebsiteCodeGroupResult websiteCodeGroupResult = new WebsiteCodeGroupResult();
        List<WebsiteCodeDataResult> websiteCodeDataResults = new ArrayList<>();
        List<WebsiteCodeDetail> websiteCodeDetailList = websiteCodeDetailMapper.selectList(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getMerchantId, websiteCodeDataReq.getMerchantId())
                .eq(WebsiteCodeDetail::getIsActivate, "Y"));
        AtomicInteger currentCurrentExchange = new AtomicInteger();
        AtomicInteger totalExchange = new AtomicInteger();
        websiteCodeDetailList.forEach(item -> {
            WebsiteCodeDataResult websiteCodeDataResult = new WebsiteCodeDataResult();
            websiteCodeDataResult.setAlias(item.getAlias());
            websiteCodeDataResult.setMerchantName(item.getMerchantName());
            websiteCodeDataResult.setActivityTime(item.getActivityTime());
            websiteCodeDataResult.setMerchantId(item.getMerchantId());
            websiteCodeDataResult.setCurrentExchange(getCurrentExchangeByWebsiteCode(item.getAlias(), websiteCodeDataReq.getStartTime(), websiteCodeDataReq.getEndTime()));
            websiteCodeDataResult.setTotalExchange(getCurrentExchangeByWebsiteCode(item.getAlias(),null,null));
            currentCurrentExchange.addAndGet(websiteCodeDataResult.getCurrentExchange());
            totalExchange.addAndGet(websiteCodeDataResult.getTotalExchange());
            websiteCodeDataResults.add(websiteCodeDataResult);
        });
        websiteCodeGroupResult.setList(websiteCodeDataResults);
        websiteCodeGroupResult.setCount(websiteCodeDataResults.size());
        websiteCodeGroupResult.setCurrentGoodsRecord(websiteGoodsRecordDao.sumGoodsRecordByMerchantId(websiteCodeDataReq.getMerchantId(), websiteCodeDataReq.getStartTime(), websiteCodeDataReq.getEndTime()));
        websiteCodeGroupResult.setCurrentExchange(currentCurrentExchange.get());
        websiteCodeGroupResult.setTotalExchange(totalExchange.get());
        return websiteCodeGroupResult;
    }




    private Integer getCurrentWebsiteCodeCount(Integer merchantId, Date startTime, Date endTime) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getMerchantId, merchantId)
                .eq(WebsiteCodeDetail::getIsActivate, "Y")
                .ge(startTime != null, WebsiteCodeDetail::getActivityTime, startTime)
                .lt(endTime != null, WebsiteCodeDetail::getActivityTime, endTime);
        return websiteCodeDetailMapper.selectCount(lambdaQueryWrapper);
    }


    private Integer getAllWebsiteCodeCount(Integer merchantId, Date startTime, Date endTime) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .like(WebsiteCodeDetail::getPidPath, merchantId + ".")
                .eq(WebsiteCodeDetail::getIsActivate, "Y");
//                .ge(startTime != null, WebsiteCodeDetail::getActivityTime, startTime)
//                .lt(endTime != null, WebsiteCodeDetail::getActivityTime, endTime);
        return websiteCodeDetailMapper.selectCount(lambdaQueryWrapper);
    }

    private Integer getCurrentWebsiteCount(Integer merchantId, Date startTime, Date endTime) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getPid, merchantId)
                .eq(Merchant::getRoleAlias, "wd")
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N");
//                .ge(startTime != null, Merchant::getCreateTime, startTime)
//                .lt(endTime != null, Merchant::getCreateTime, endTime);
        return merchantMapper.selectCount(lambdaQueryWrapper);
    }


    private Integer getAllWebsiteCount(Integer merchantId, Date startTime, Date endTime) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
                .like(Merchant::getPidPath, merchantId + ".")
                .eq(Merchant::getRoleAlias, "wd")
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N")
                .ge(startTime != null, Merchant::getCreateTime, startTime)
                .lt(endTime != null, Merchant::getCreateTime, endTime);
        return merchantMapper.selectCount(lambdaQueryWrapper);
    }


    private Integer getCurrentExchange(Integer merchantId, Date startTime, Date endTime) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<WebsiteBill>lambdaQuery()
                .like(WebsiteBill::getPidPath, merchantId + ".")
                .ge(startTime != null, WebsiteBill::getCreateTime, startTime)
                .lt(endTime != null, WebsiteBill::getCreateTime, endTime);
        return websiteBillMapper.selectCount(lambdaQueryWrapper);
    }


    private Integer getCurrentExchangeByPid(Integer merchantId, Date startTime, Date endTime) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getMerchantId, merchantId)
                .ge(startTime != null, WebsiteBill::getCreateTime, startTime)
                .lt(endTime != null, WebsiteBill::getCreateTime, endTime);
        return websiteBillMapper.selectCount(lambdaQueryWrapper);
    }

    private Integer getCurrentExchangeByWebsiteCode(String websiteCode, Date startTime, Date endTime) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getWebsiteCode, websiteCode)
                .ge(startTime != null, WebsiteBill::getCreateTime, startTime)
                .lt(endTime != null, WebsiteBill::getCreateTime, endTime);
        return websiteBillMapper.selectCount(lambdaQueryWrapper);
    }

}
