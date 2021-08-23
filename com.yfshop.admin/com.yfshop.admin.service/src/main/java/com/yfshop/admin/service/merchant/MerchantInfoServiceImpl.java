package com.yfshop.admin.service.merchant;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.yfshop.admin.api.merchant.MerchantInfoService;
import com.yfshop.admin.api.merchant.request.MerchantGroupReq;
import com.yfshop.admin.api.merchant.request.MerchantReq;
import com.yfshop.admin.api.merchant.request.QueryGoodsRecordReq;
import com.yfshop.admin.api.merchant.result.GoodsRecordResult;
import com.yfshop.admin.api.merchant.result.MerchantGroupResult;
import com.yfshop.admin.api.merchant.result.MerchantMyselfResult;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.WebsiteCodeTaskService;
import com.yfshop.admin.api.website.request.WebsiteCodeAddressReq;
import com.yfshop.admin.api.website.request.WebsiteCodeBindReq;
import com.yfshop.admin.api.website.request.WebsiteCodeDataReq;
import com.yfshop.admin.api.website.request.WebsiteCodePayReq;
import com.yfshop.admin.api.website.result.WebsiteCodeAddressResult;
import com.yfshop.admin.api.website.result.WebsiteCodeAmountResult;
import com.yfshop.admin.api.website.result.WebsiteCodeDataResult;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
import com.yfshop.admin.api.website.result.WebsiteCodeGroupResult;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
import com.yfshop.admin.api.website.result.WebsiteTypeResult;
import com.yfshop.admin.dao.WebsiteCodeDao;
import com.yfshop.admin.dao.WebsiteGoodsRecordDao;
import com.yfshop.admin.utils.BaiduMapGeocoderUtil;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.enums.PayPrefixEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.AddressUtil;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.GeoUtils;
import com.yfshop.wx.api.service.MpPayService;
import com.yfshop.wx.api.service.MpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户用户服务
 *
 * @author youshenghui
 * Created in 2021-03-23 9:10
 */
@Validated
@DubboService
public class MerchantInfoServiceImpl implements MerchantInfoService {

    // 单价
    private static final String PIECE_PRICE = "0.3";
    // 每张大约4.3g
    private static final String PIECE_WEIGHT = "4.3";
    // 基础运费
    private static final int DEFAULT_POSTAGE = 8;
    // 每一公斤超重运费
    private static final double OVERWEIGHT_POSTAGE = 2.5D;

    @Resource
    private RedisService redisService;

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

    @DubboReference
    private MpService mpService;
    @Value("${wxPay.notifyUrl}")
    private String wxPayNotifyUrl;
    @Resource
    private WebsiteCodeDao websiteCodeDao;
    @Resource
    private WebsiteGoodsRecordDao websiteGoodsRecordDao;
    @Resource
    private WebsiteGoodsRecordMapper websiteGoodsRecordMapper;
    @Value("${merchant.url}")
    private String merchantUrl;
    @Resource
    private MerchantLogMapper merchantLogMapper;
    @Resource
    private WebsiteCodeGroupMapper websiteCodeGroupMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public MerchantResult getWebsiteInfo(Integer merchantId) throws ApiException {
        Merchant merchant = merchantMapper.selectById(merchantId);
        Asserts.assertNonNull(merchant, 500, "网点信息不存在！");
        MerchantDetail merchantDetail = merchantDetailMapper.selectOne(Wrappers.<MerchantDetail>lambdaQuery()
                .eq(MerchantDetail::getMerchantId, merchantId));
        MerchantResult merchantResult = BeanUtil.convert(merchant, MerchantResult.class);
        if (merchantDetail != null) {
            merchantResult.setWebsiteTypeName(merchantDetail.getWebsiteTypeName());
            merchantResult.setIsRefrigerator(merchantDetail.getIsRefrigerator());
            merchantResult.setLatitude(merchantDetail.getLatitude());
            merchantResult.setLongitude(merchantDetail.getLongitude());
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
        String roleAlias;
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
            merchantDetail.setMerchantId(merchantId);
            merchantDetail.setGeoHash(GeoUtils.toBase32(websiteReq.getLatitude(), websiteReq.getLongitude(), 12));
            merchantDetailMapper.insert(merchantDetail);
            roleAlias = GroupRoleEnum.WD.getCode();
            if (merchantDetail.getLongitude() != null && merchantDetail.getLatitude() != null) {
                Point point = new Point(merchantDetail.getLongitude(), merchantDetail.getLatitude());
                redisTemplate.opsForGeo().add(CacheConstants.MERCHANT_GRO_DATA, point, merchantDetail.getMerchantId());
            }
        } else {
            String pidPath = merchant.getPidPath();
            merchantId = merchant.getId();
            roleAlias = merchant.getRoleAlias();
            if (GroupRoleEnum.WD.getCode().equals(roleAlias)) {
                Asserts.assertTrue(websiteCodeDetail.getPid().equals(merchant.getPid()), 500, "该网点码不是你的上级申请的，无法绑定！");
            } else {
                Asserts.assertTrue(getWebsiteCodeBindCount(merchantId) == 0, 500, "您已经绑定过网点码了！");
            }
            Asserts.assertTrue(merchant.getPidPath().contains(websiteCodeDetail.getPidPath()), 500, "该网点码不是你或你的上级申请的，无法绑定！");
            Asserts.assertEquals(merchant.getMobile(), websiteReq.getMobile(), 500, "手机号不允许被修改！");
            String openId = merchant.getOpenId();
            merchant = BeanUtil.convert(websiteReq, Merchant.class);
            merchant.setHeadImgUrl(headImageUrl);
            merchant.setPidPath(pidPath);
            if (StringUtils.isNotBlank(openId)) {
                //只保存第一次的openId,不更新openId
                merchant.setOpenId(null);
            }
            MerchantDetail merchantDetail = merchantDetailMapper.selectOne(Wrappers.<MerchantDetail>lambdaQuery()
                    .eq(MerchantDetail::getMerchantId, merchantId));
            if (merchantDetail != null) {
                Integer merchantDetailId = merchantDetail.getId();
                merchantDetail = BeanUtil.convert(websiteReq, MerchantDetail.class);
                merchantDetail.setMerchantId(merchantId);
                merchantDetail.setId(merchantDetailId);
                merchantDetailMapper.updateById(merchantDetail);
            } else {
                merchantDetail = BeanUtil.convert(websiteReq, MerchantDetail.class);
                merchantDetail.setGeoHash(GeoUtils.toBase32(websiteReq.getLatitude(), websiteReq.getLongitude(), 12));
                merchantDetail.setMerchantId(merchantId);
                merchantDetailMapper.insert(merchantDetail);
            }
            if (merchantDetail.getLongitude() != null && merchantDetail.getLatitude() != null) {
                Point point = new Point(merchantDetail.getLongitude(), merchantDetail.getLatitude());
                redisTemplate.opsForGeo().add(CacheConstants.MERCHANT_GRO_DATA, point, merchantDetail.getMerchantId());
            }
        }
        if (GroupRoleEnum.WD.getCode().equals(roleAlias)) {
            Merchant merchantPid = merchantMapper.selectById(websiteCodeDetail.getPid());
            if (merchantPid != null) {
                merchant.setPid(merchantPid.getId());
                merchant.setPidPath(merchantPid.getPidPath() + merchantId + ".");
                merchant.setPMerchantName(merchantPid.getMerchantName());
            } else {
                merchant.setPidPath(merchantId + ".");
            }
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
        websiteCodeDetail.setMerchantId(merchantId);
        websiteCodeDetail.setActivityTime(LocalDateTime.now());
        websiteCodeDetail.setMerchantName(merchant.getMerchantName());
        websiteCodeDetail.setMerchantPidPath(merchant.getPidPath());
        websiteCodeDetail.setMobile(merchant.getMobile());
        websiteCodeDetail.setIsActivate("Y");
        websiteCodeDetailMapper.updateById(websiteCodeDetail);
        return BeanUtil.convert(merchant, MerchantResult.class);
    }


    private void buildAddress(WebsiteCodeBindReq websiteCodeBindReq) {
        if (StringUtils.isNotBlank(websiteCodeBindReq.getAddress())) {
            Map<String, String> maps = AddressUtil.addressResolution(websiteCodeBindReq.getAddress());
            websiteCodeBindReq.setProvince(maps.get("province"));
            Region province = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                    .eq(Region::getType, 1)
                    .eq(Region::getName, maps.get("province")));
            if (province != null) {
                websiteCodeBindReq.setProvinceId(province.getId());
                Region city = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                        .eq(Region::getType, 2)
                        .eq(Region::getName, maps.get("city"))
                        .eq(Region::getPid, province.getId()));
                if (city != null) {
                    websiteCodeBindReq.setCityId(city.getId());
                    Region county = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                            .eq(Region::getType, 3)
                            .eq(Region::getName, maps.get("county"))
                            .eq(Region::getPid, city.getId()));
                    if (county != null) {
                        websiteCodeBindReq.setDistrictId(county.getId());
                    }
                }
                websiteCodeBindReq.setCity(maps.get("city"));
                websiteCodeBindReq.setDistrict(maps.get("county"));
            } else {
                if (websiteCodeBindReq.getLatitude() != null && websiteCodeBindReq.getLongitude() != null) {
                    Map<String, String> temp = BaiduMapGeocoderUtil.getAddressInfoByLngAndLat(websiteCodeBindReq.getLongitude() + "", websiteCodeBindReq.getLatitude() + "");
                    if (temp != null) {
                        websiteCodeBindReq.setProvince(temp.get("province"));
                        province = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                                .eq(Region::getType, 1)
                                .eq(Region::getName, temp.get("province")));
                        if (province != null) {
                            websiteCodeBindReq.setProvinceId(province.getId());
                            Region city = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                                    .eq(Region::getType, 2)
                                    .eq(Region::getName, temp.get("city"))
                                    .eq(Region::getPid, province.getId()));
                            if (city != null) {
                                websiteCodeBindReq.setCityId(city.getId());
                                Region county = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                                        .eq(Region::getType, 3)
                                        .eq(Region::getName, temp.get("district"))
                                        .eq(Region::getPid, city.getId()));
                                if (county != null) {
                                    websiteCodeBindReq.setDistrictId(county.getId());
                                }
                            }
                            websiteCodeBindReq.setCity(temp.get("city"));
                            websiteCodeBindReq.setDistrict(temp.get("district"));
                        }
                    }
                }
            }
            String address = maps.get("town");
            if (address != null && websiteCodeBindReq.getDistrict() != null) {
                try {
                    if (!websiteCodeBindReq.getDistrict().contains("(")) {
                        address = address.replaceFirst(websiteCodeBindReq.getDistrict(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            websiteCodeBindReq.setAddress(address);
        }
    }

    @Override
    public List<WebsiteCodeDetailResult> getMyWebsiteCode(Integer merchantId, String status, Date startTime, Date
            endTime) throws ApiException {
        List<WebsiteCodeDetail> websiteCodeDetails = websiteCodeDetailMapper.selectList(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .and(itemWrapper -> itemWrapper
                        .eq(WebsiteCodeDetail::getMerchantId, merchantId)
                        .or()
                        .like(WebsiteCodeDetail::getPidPath, "." + merchantId + "."))
                .eq(WebsiteCodeDetail::getIsActivate, status)
                .ge(startTime != null, WebsiteCodeDetail::getCreateTime, startTime)
                .lt(endTime != null, WebsiteCodeDetail::getCreateTime, endTime)
                .orderByDesc(WebsiteCodeDetail::getUpdateTime));
        return BeanUtil.convertList(websiteCodeDetails, WebsiteCodeDetailResult.class);
    }

    @Override
    public IPage<WebsiteCodeResult> applyWebsiteCodeStatus(Integer merchantId, String status, Integer
            pageIndex, Integer pageSize) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        List<String> allStatus = new ArrayList<>();
        if ("ALL".equals(status)) {
            if (!GroupRoleEnum.JXS.getCode().equals(merchant.getRoleAlias())) {
                allStatus.add("PENDING");
                allStatus.add("PAYING");
            }
            allStatus.add("WAIT");
            allStatus.add("DELIVERY");
            allStatus.add("SUCCESS");
        } else if ("PENDING".equals(status)) {
            allStatus.add("PENDING");
            allStatus.add("PAYING");
        } else {
            allStatus.add(status);
        }
        LambdaQueryWrapper<WebsiteCode> lambdaQueryWrapper = Wrappers.<WebsiteCode>lambdaQuery()
                .and(itemWrapper -> itemWrapper
                        .eq(WebsiteCode::getMerchantId, merchantId)
                        .or()
                        .like(WebsiteCode::getPidPath, "." + merchantId + "."))
                .in(CollectionUtil.isNotEmpty(allStatus), WebsiteCode::getOrderStatus, allStatus)
                .orderByDesc(WebsiteCode::getPayTime);
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
        if (StringUtils.isNotBlank(email)) {
            Asserts.assertTrue(Validator.isMatchRegex(PatternPool.EMAIL, email), 500, "请正确填写邮箱地址");
        }
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
        websiteCode.setContracts(merchant.getContacts());
        websiteCode.setPidPath(merchant.getPidPath());
        websiteCode.setQuantity(count);
        if (StringUtils.isBlank(email)) {
            websiteCode.setOrderAmount(new BigDecimal(PIECE_PRICE).multiply(new BigDecimal(count)));
            websiteCode.setPostage(getTotalPostage(count));
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
        /*
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
        */
        WebsiteCodeAmountResult websiteCodeAmountResult = new WebsiteCodeAmountResult();
        if (CollectionUtil.isEmpty(ids)) {
            websiteCodeAmountResult.setAmount(new BigDecimal(0));
            websiteCodeAmountResult.setPostage(new BigDecimal(0));
            websiteCodeAmountResult.setQuantity(0);
        } else {
            List<WebsiteCode> websiteCodes = websiteCodeMapper.selectBatchIds(ids);
            int totalQuantity = websiteCodes.stream().mapToInt(WebsiteCode::getQuantity).sum();
            websiteCodeAmountResult.setAmount(new BigDecimal(PIECE_PRICE).multiply(new BigDecimal(totalQuantity)));
            websiteCodeAmountResult.setPostage(getTotalPostage(totalQuantity));
            websiteCodeAmountResult.setQuantity(totalQuantity);
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
        Asserts.assertCollectionNotEmpty(websiteCodePayReq.getIds(), 500, "请选择你要付款的订单！");
        WebsiteCodeAddress websiteCodeAddress = websiteCodeAddressMapper.selectById(websiteCodePayReq.getAddressId());
        WebsiteCode websiteCode = new WebsiteCode();
//        websiteCode.setMobile(websiteCodeAddress.getMobile());
//        websiteCode.setContracts(websiteCodeAddress.getContracts());
        websiteCode.setAddress(websiteCodeAddress.getProvince() + websiteCodeAddress.getCity() + websiteCodeAddress.getDistrict() + websiteCodeAddress.getAddress() + "," + websiteCodeAddress.getContracts() + "," + websiteCodeAddress.getMobile());
        websiteCode.setOrderNo(String.format("%06d", websiteCodeAddress.getMerchantId()) + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS")));
        websiteCode.setOrderStatus("PAYING");
        WebsiteCodeAmountResult websiteCodeAmountResult = applyWebsiteCodeAmount(websiteCodePayReq.getIds());
        websiteCode.setPostage(websiteCodeAmountResult.getPostage().divide(new BigDecimal(websiteCodePayReq.getIds().size()), 2, BigDecimal.ROUND_CEILING));
        int count = websiteCodeMapper.update(websiteCode, Wrappers.<WebsiteCode>lambdaQuery()
                .in(WebsiteCode::getId, websiteCodePayReq.getIds())
                .in(WebsiteCode::getOrderStatus, "PENDING"));
        Asserts.assertTrue(count > 0, 500, "没有要支付的订单！");
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
        orderRequest.setSpbillCreateIp(websiteCodePayReq.getIp());
        orderRequest.setTimeStart(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        orderRequest.setTimeExpire(DateFormatUtils.format(new Date(System.currentTimeMillis() + (1000 * 60 * 15)), "yyyyMMddHHmmss"));
        try {
            WebsiteCodeGroup websiteCodeGroup = new WebsiteCodeGroup();
            websiteCodeGroup.setMerchantId(websiteCodePayReq.getMerchantId());
            Merchant merchant = merchantMapper.selectById(websiteCodePayReq.getMerchantId());
            if (merchant != null) {
                websiteCodeGroup.setMerchantName(merchant.getMerchantName());
            }
            websiteCodeGroup.setContracts(websiteCodeAddress.getContracts());
            websiteCodeGroup.setAddress(websiteCode.getAddress());
            websiteCodeGroup.setOrderNo(websiteCode.getOrderNo());
            websiteCodeGroup.setMobile(websiteCodeAddress.getMobile());
            websiteCodeGroup.setOrderStatus("PAYING");
            websiteCodeGroup.setQuantity(websiteCodeAmountResult.getQuantity());
            websiteCodeGroupMapper.insert(websiteCodeGroup);
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
                .eq(Merchant::getOpenId, openId)
                .orderByDesc(Merchant::getUpdateTime));
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
    public void websitePayOrderNotify(String transactionId, String outTradeNo) throws ApiException {
        WebsiteCode websiteCode = new WebsiteCode();
        websiteCode.setPayMethod("WxPay");
        websiteCode.setPayTime(LocalDateTime.now());
        websiteCode.setOrderStatus("WAIT");
        websiteCode.setBillno(transactionId);
        int count = websiteCodeMapper.update(websiteCode, Wrappers.<WebsiteCode>lambdaQuery()
                .eq(WebsiteCode::getOrderNo, outTradeNo)
                .eq(WebsiteCode::getOrderStatus, "PAYING"));
        if (count > 0) {
            WebsiteCodeGroup websiteCodeGroup = new WebsiteCodeGroup();
            websiteCodeGroup.setOrderStatus("WAIT");
            websiteCodeGroup.setPayMethod("WxPay");
            websiteCodeGroup.setPayTime(LocalDateTime.now());
            websiteCodeGroup.setBillno(transactionId);
            websiteCodeGroupMapper.update(websiteCodeGroup, Wrappers.<WebsiteCodeGroup>lambdaQuery()
                    .eq(WebsiteCodeGroup::getOrderNo, outTradeNo));
            websiteCodeTask.doWorkWebsiteCodeFile(outTradeNo);
        }
    }

//    @Override
//    public MerchantGroupResult merchantGroup(MerchantGroupReq merchantGroupReq) throws ApiException {
//        MerchantGroupResult merchantGroupResult = new MerchantGroupResult();
//        //Integer myselfCount = getCurrentWebsiteCount(merchantGroupReq.getMerchantId());
//        Merchant merchant = merchantMapper.selectById(merchantGroupReq.getMerchantId());
//        Integer count = getAllWebsiteCodeCount(merchantGroupReq.getMerchantId());
//        merchantGroupResult.setMerchantId(merchant.getId());
//        merchantGroupResult.setMerchantName(merchant.getMerchantName());
//        merchantGroupResult.setContacts(merchant.getContacts());
//        merchantGroupResult.setMobile(merchant.getMobile());
//        merchantGroupResult.setCount(count);
//        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
//                .eq(Merchant::getPid, merchantGroupReq.getMerchantId())
//                .ne(Merchant::getRoleAlias, "wd")
//                .eq(Merchant::getIsEnable, "Y")
//                .eq(Merchant::getIsDelete, "N");
//        List<Merchant> merchantList = merchantMapper.selectList(lambdaQueryWrapper);
//        List<MerchantGroupResult> merchantGroupResults = new ArrayList<>();
//        MerchantMyselfResult myselfResult = getMyselfWebsite(merchant.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime());
//        if (myselfResult.getCount() > 0) {
//            MerchantGroupResult myself = new MerchantGroupResult();
//            myself.setHaveWebsite(true);
//            myself.setMerchantId(merchant.getId());
//            myself.setMerchantName(merchant.getMerchantName());
//            myself.setContacts(merchant.getContacts());
//            myself.setMobile(merchant.getMobile());
//            myself.setCurrentExchange(myselfResult.getCurrentExchange());
//            myself.setTotalExchange(myselfResult.getTotalExchange());
//            myself.setCurrentGoodsRecord(myselfResult.getCurrentGoodsRecord());
//            myself.setCount(myselfResult.getCount());
//            merchantGroupResults.add(myself);
//        }
//        merchantList.forEach(item -> {
//            MerchantGroupResult child = new MerchantGroupResult();
//            child.setMerchantId(item.getId());
//            child.setMerchantName(item.getMerchantName());
//            child.setContacts(item.getContacts());
//            child.setMobile(item.getMobile());
//            child.setCurrentExchange(getCurrentExchange(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
//            child.setTotalExchange(getCurrentExchange(item.getId(), null, null));
//            child.setCurrentGoodsRecord(websiteGoodsRecordDao.sumAllGoodsRecord(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
//            child.setCount(getAllWebsiteCodeCount(item.getId()));
//            merchantGroupResults.add(child);
//        });
//        merchantGroupResult.setList(merchantGroupResults);
//        return merchantGroupResult;
//    }

    @Override
    public MerchantGroupResult merchantGroup(MerchantGroupReq merchantGroupReq) throws ApiException {
        MerchantGroupResult merchantGroupResult = new MerchantGroupResult();
        Merchant merchant = merchantMapper.selectById(merchantGroupReq.getMerchantId());
        merchantGroupResult.setMerchantId(merchant.getId());
        merchantGroupResult.setMerchantName(merchant.getMerchantName());
        merchantGroupResult.setContacts(merchant.getContacts());
        merchantGroupResult.setMobile(merchant.getMobile());
        merchantGroupResult.setCount(getAllWebsiteCodeCount(merchant.getPidPath()));
        merchantGroupResult.setTotalExchange(getCurrentExchange(merchant.getPidPath(), null, null));
        merchantGroupResult.setCurrentGoodsRecord((websiteGoodsRecordDao.sumAllGoodsRecord(merchant.getPidPath(), null, null)));
        LambdaQueryWrapper<Merchant> lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getPid, merchantGroupReq.getMerchantId())
                .ne(Merchant::getRoleAlias, "wd")
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N");
        lambdaQueryWrapper.and(StringUtils.isNotBlank(merchantGroupReq.getKey()), wrapper -> {
            wrapper.like(Merchant::getMerchantName, merchantGroupReq.getKey())
                    .or()
                    .like(Merchant::getContacts, merchantGroupReq.getKey())
                    .or()
                    .like(Merchant::getMobile, merchantGroupReq.getKey());
        });
        IPage<Merchant> iPage = merchantMapper.selectPage(new Page<>(merchantGroupReq.getPageIndex(), merchantGroupReq.getPageSize()), lambdaQueryWrapper);
        List<MerchantGroupResult> merchantGroupResults = new ArrayList<>();
        if (merchantGroupReq.getPageIndex() == 1) {
            MerchantMyselfResult myselfResult = getMyselfWebsite(merchant.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime());
            if (myselfResult.getCount() > 0) {
                MerchantGroupResult myself = new MerchantGroupResult();
                myself.setHaveWebsite(true);
                myself.setMerchantId(merchant.getId());
                myself.setMerchantName(merchant.getMerchantName());
                myself.setContacts(merchant.getContacts());
                myself.setMobile(merchant.getMobile());
                myself.setCurrentExchange(myselfResult.getCurrentExchange());
                myself.setTotalExchange(myselfResult.getTotalExchange());
                myself.setCurrentGoodsRecord(myselfResult.getCurrentGoodsRecord());
                myself.setCount(myselfResult.getCount());
                merchantGroupResults.add(myself);
            }
        }
        iPage.getRecords().forEach(item -> {
            MerchantGroupResult child = new MerchantGroupResult();
            child.setMerchantId(item.getId());
            child.setMerchantName(item.getMerchantName());
            child.setContacts(item.getContacts());
            child.setMobile(item.getMobile());
            child.setCurrentExchange(getCurrentExchange(item.getPidPath(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            child.setTotalExchange(getCurrentExchange(item.getPidPath(), null, null));
            child.setCurrentGoodsRecord(websiteGoodsRecordDao.sumAllGoodsRecord(item.getPidPath(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            child.setCount(getAllWebsiteCodeCount(item.getPidPath()));
            merchantGroupResults.add(child);
        });
        merchantGroupResult.setList(merchantGroupResults);
        merchantGroupResult.setTotal(iPage.getTotal());
        merchantGroupResult.setCurrent(iPage.getCurrent());
        merchantGroupResult.setSize(iPage.getSize());
        merchantGroupResult.setPages(iPage.getPages());
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
            merchant.setPMerchantName(StringUtils.isNotBlank(pMerchant.getMerchantName()) ? pMerchant.getMerchantName() : merchantReq.getContacts());
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
            Merchant newM = merchantMapper.selectById(m.getId());
            MerchantLog merchantLog = new MerchantLog();
            merchantLog.setMerchantId(m.getId());
            merchantLog.setOperatorId(merchantReq.getOperatorId());
            merchantLog.setBeforeData(JSONUtil.toJsonStr(m));
            merchantLog.setAfterData(JSONUtil.toJsonStr(newM));
            merchantLogMapper.insert(merchantLog);
        }
        return null;
    }

    @Override
    public List<MerchantResult> getChildMerchant(Integer merchantId) throws ApiException {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getPid, merchantId)
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N")
                .ne(Merchant::getRoleAlias, GroupRoleEnum.WD.getCode())
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
        if (CollectionUtils.isNotEmpty(websiteCodePayReq.getIds())) {
            int count = websiteCodeMapper.update(websiteCode, Wrappers.<WebsiteCode>lambdaQuery()
                    .in(WebsiteCode::getId, websiteCodePayReq.getIds())
                    .eq(WebsiteCode::getOrderStatus, "PAYING"));
            if (count > 0) {
                WebsiteCode w = websiteCodeMapper.selectById(websiteCodePayReq.getIds().get(0));
                if (w != null && StringUtils.isNotBlank(w.getOrderNo())) {
                    WebsiteCodeGroup websiteCodeGroup = new WebsiteCodeGroup();
                    websiteCodeGroup.setOrderStatus("CANCEL");
                    websiteCodeGroupMapper.update(websiteCodeGroup, Wrappers.<WebsiteCodeGroup>lambdaQuery()
                            .eq(WebsiteCodeGroup::getOrderNo, w.getOrderNo()));
                }
            }
        }

    }

//    @Override
//    public MerchantGroupResult getWebsiteList(Integer merchantId, MerchantGroupReq merchantGroupReq) {
//        MerchantGroupResult merchantGroupResult = new MerchantGroupResult();
//        AtomicInteger count = new AtomicInteger();
//        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
//                .eq(Merchant::getPid, merchantGroupReq.getMerchantId())
//                .eq(Merchant::getRoleAlias, "wd")
//                .eq(Merchant::getIsEnable, "Y")
//                .eq(Merchant::getIsDelete, "N");
//        List<Merchant> merchantList = merchantMapper.selectList(lambdaQueryWrapper);
//        /**
//         * 判断自己有没有绑定网点码，如果有将自己插入到第一个
//         */
//        if (getWebsiteCodeBindCount(merchantGroupReq.getMerchantId()) > 0) {
//            Merchant merchant = merchantMapper.selectById(merchantGroupReq.getMerchantId());
//            merchantList.add(0, merchant);
//        }
//        List<MerchantGroupResult> merchantGroupResults = new ArrayList<>();
//        merchantList.forEach(item -> {
//            MerchantGroupResult child = new MerchantGroupResult();
//            child.setMerchantId(item.getId());
//            child.setMerchantName(item.getMerchantName());
//            child.setMobile(item.getMobile());
//            child.setContacts(item.getContacts());
//            child.setCurrentExchange(getCurrentExchangeByPid(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
//            child.setTotalExchange(getCurrentExchangeByPid(item.getId(), null, null));
//            child.setCurrentGoodsRecord(websiteGoodsRecordDao.sumGoodsRecordByMerchantId(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
//            child.setCount(getCurrentWebsiteCodeCount(item.getId(), merchantId));
//            count.addAndGet(child.getCount());
//            merchantGroupResults.add(child);
//        });
//        merchantGroupResult.setCount(count.get());
//        merchantGroupResult.setList(merchantGroupResults);
//        return merchantGroupResult;
//    }

    @Override
    public MerchantGroupResult getWebsiteList(Integer merchantId, MerchantGroupReq merchantGroupReq) {
        List<Merchant> merchantList = new ArrayList<>();
        MerchantGroupResult merchantGroupResult = new MerchantGroupResult();
        //AtomicInteger count = new AtomicInteger();
        LambdaQueryWrapper<Merchant> lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getPid, merchantGroupReq.getMerchantId())
                .eq(Merchant::getRoleAlias, "wd")
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N");
        lambdaQueryWrapper.and(StringUtils.isNotBlank(merchantGroupReq.getKey()), wrapper -> {
            wrapper.like(Merchant::getMerchantName, merchantGroupReq.getKey())
                    .or()
                    .like(Merchant::getContacts, merchantGroupReq.getKey())
                    .or()
                    .like(Merchant::getMobile, merchantGroupReq.getKey());

        });

        IPage<Merchant> iPage = merchantMapper.selectPage(new Page<>(merchantGroupReq.getPageIndex(), merchantGroupReq.getPageSize()), lambdaQueryWrapper);
        /**
         * 判断自己有没有绑定网点码，如果有将自己插入到第一个
         */
        Merchant merchant = merchantMapper.selectById(merchantGroupReq.getMerchantId());
        if (merchantGroupReq.getPageIndex() == 1) {
            if (getWebsiteCodeBindCount(merchantGroupReq.getMerchantId()) > 0) {
                merchantList.add(merchant);
            }
        }
        merchantList.addAll(iPage.getRecords());
        List<MerchantGroupResult> merchantGroupResults = new ArrayList<>();
        merchantList.forEach(item -> {
            MerchantGroupResult child = new MerchantGroupResult();
            child.setMerchantId(item.getId());
            child.setMerchantName(item.getMerchantName());
            child.setMobile(item.getMobile());
            child.setContacts(item.getContacts());
            child.setCurrentExchange(getCurrentExchangeByPid(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            child.setTotalExchange(getCurrentExchangeByPid(item.getId(), null, null));
            child.setCurrentGoodsRecord(websiteGoodsRecordDao.sumGoodsRecordByMerchantId(item.getId(), merchantGroupReq.getStartTime(), merchantGroupReq.getEndTime()));
            child.setCount(getCurrentWebsiteCodeCount(item.getId(), merchantId));
            //count.addAndGet(child.getCount());
            merchantGroupResults.add(child);
        });
        merchantGroupResult.setCount(getAllWebsiteCodeCount(merchant.getPidPath()));
        merchantGroupResult.setTotalExchange(getCurrentExchange(merchant.getPidPath(), null, null));
        merchantGroupResult.setCurrentGoodsRecord((websiteGoodsRecordDao.sumAllGoodsRecord(merchant.getPidPath(), null, null)));
        merchantGroupResult.setList(merchantGroupResults);
        merchantGroupResult.setTotal(iPage.getTotal());
        merchantGroupResult.setCurrent(iPage.getCurrent());
        merchantGroupResult.setSize(iPage.getSize());
        merchantGroupResult.setPages(iPage.getPages());
        return merchantGroupResult;
    }


    @Override
    public WebsiteCodeGroupResult getWebsiteCodeData(Integer merchantId, WebsiteCodeDataReq websiteCodeDataReq) {
        Integer currentMerchantId = websiteCodeDataReq.getMerchantId() == null ? merchantId : websiteCodeDataReq.getMerchantId();
        WebsiteCodeGroupResult websiteCodeGroupResult = new WebsiteCodeGroupResult();
        List<WebsiteCodeDataResult> websiteCodeDataResults = new ArrayList<>();
        List<WebsiteCodeDetail> websiteCodeDetailList = websiteCodeDetailMapper.selectList(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getMerchantId, currentMerchantId)
                .like(websiteCodeDataReq.getMerchantId() != null, WebsiteCodeDetail::getMerchantPidPath, "." + merchantId + ".")
                .eq(WebsiteCodeDetail::getIsActivate, "Y"));
        AtomicInteger currentCurrentExchange = new AtomicInteger();
        AtomicInteger totalExchange = new AtomicInteger();
        websiteCodeDetailList.forEach(item -> {
            WebsiteCodeDataResult websiteCodeDataResult = new WebsiteCodeDataResult();
            websiteCodeDataResult.setAlias(item.getAlias());
            websiteCodeDataResult.setMerchantName(item.getMerchantName());
            websiteCodeDataResult.setActivityTime(item.getActivityTime());
            websiteCodeDataResult.setMerchantId(item.getMerchantId());
            websiteCodeDataResult.setMobile(item.getMobile());
            websiteCodeDataResult.setCurrentExchange(getCurrentExchangeByWebsiteCode(item.getAlias(), websiteCodeDataReq.getStartTime(), websiteCodeDataReq.getEndTime()));
            websiteCodeDataResult.setTotalExchange(getCurrentExchangeByWebsiteCode(item.getAlias(), null, null));
            currentCurrentExchange.addAndGet(websiteCodeDataResult.getCurrentExchange());
            totalExchange.addAndGet(websiteCodeDataResult.getTotalExchange());
            websiteCodeDataResults.add(websiteCodeDataResult);
        });
        websiteCodeGroupResult.setList(websiteCodeDataResults);
        websiteCodeGroupResult.setCount(totalExchange.get());
        websiteCodeGroupResult.setCurrentGoodsRecord(websiteGoodsRecordDao.sumGoodsRecordByMerchantId(currentMerchantId, websiteCodeDataReq.getStartTime(), websiteCodeDataReq.getEndTime()));
        websiteCodeGroupResult.setCurrentExchange(currentCurrentExchange.get());
        websiteCodeGroupResult.setTotalExchange(totalExchange.get());
        return websiteCodeGroupResult;
    }

    @Override
    public List<MerchantResult> findNearMerchantList(String key, Integer merchantId, Integer districtId, Double
            longitude, Double latitude) {
        List<MerchantResult> resultList = new ArrayList<>();
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (StringUtils.isNotBlank(key)) {
            String search = key.trim();
            List<Merchant> merchantList = merchantMapper.selectList(Wrappers.lambdaQuery(Merchant.class)
                    .eq(Merchant::getIsDelete, 'N')
                    .eq(Merchant::getIsEnable, 'Y')
                    .likeRight(Merchant::getPidPath, merchant.getPidPath())
                    .and(wrapper -> wrapper
                            .like(Merchant::getMerchantName, search)
                            .or()
                            .like(Merchant::getContacts, search)
                            .or()
                            .like(Merchant::getMobile, search)));
            List<Integer> ids = merchantList.stream().map(Merchant::getId).collect(Collectors.toList());
            Map<Integer, MerchantDetail> detailMaps = merchantDetailMapper.selectList(Wrappers.lambdaQuery(MerchantDetail.class).in(MerchantDetail::getMerchantId, ids)).stream().collect(Collectors.toMap(MerchantDetail::getMerchantId, Function.identity()));
            merchantList.forEach(item -> {
                MerchantDetail merchantDetail = detailMaps.get(item.getId());
                if (merchantDetail != null) {
                    MerchantResult result = BeanUtil.convert(item, MerchantResult.class);
                    result.setLongitude(result.getLongitude());
                    result.setLatitude(result.getLatitude());
                    resultList.add(result);
                }
            });
        } else {
            if (longitude == null || longitude == null) return resultList;
            int limit = 100;
            // 中心位置半径100km内的前100个门店
            Circle circle = new Circle(new Point(longitude, latitude), new Distance(50, RedisGeoCommands.DistanceUnit.KILOMETERS));
            RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates().includeDistance().sortAscending().limit(limit);
            GeoResults<RedisGeoCommands.GeoLocation<Object>> geoLocationGeoResults = redisTemplate.opsForGeo().radius(CacheConstants.MERCHANT_GRO_DATA, circle, args);
            List<Integer> merchantIds = new ArrayList<>();
            Map<Integer, GeoResult<RedisGeoCommands.GeoLocation<Object>>> mapDistance = new HashMap<>();
            if (geoLocationGeoResults != null) {
                for (GeoResult<RedisGeoCommands.GeoLocation<Object>> locationGeoResult : geoLocationGeoResults) {
                    RedisGeoCommands.GeoLocation<Object> content = locationGeoResult.getContent();
                    Object id = content.getName();
                    merchantIds.add(Integer.valueOf(id.toString()));
                    mapDistance.put(Integer.valueOf(id.toString()), locationGeoResult);
                }
            }
            if (CollectionUtils.isEmpty(merchantIds)) {
                return resultList;
            }
            List<Merchant> list = merchantMapper.selectList(Wrappers.lambdaQuery(Merchant.class)
                    .in(Merchant::getId, merchantIds)
                    .eq(Merchant::getIsDelete, 'N')
                    .eq(Merchant::getIsEnable, 'Y'));
            list = list.stream().filter(data -> data.getPidPath().startsWith(merchant.getPidPath()))
                    .collect(Collectors.toList());
            list.forEach(item -> {
                MerchantResult result = BeanUtil.convert(item, MerchantResult.class);
                GeoResult<RedisGeoCommands.GeoLocation<Object>> geoResult = mapDistance.get(item.getId());
                Point point = geoResult.getContent().getPoint();
                Distance distance = geoResult.getDistance();
                result.setDistanceValue(distance.getValue());
                if ("km".equals(distance.getUnit()) && distance.getValue() < 0) {
                    result.setDistance(String.format("%.1f千米", distance.getValue()));
                } else {
                    result.setDistance(String.format("%.1f米", (distance.getValue() * 1000)));
                }
                result.setLongitude(point.getX());
                result.setLatitude(point.getY());
                resultList.add(result);
            });
            resultList.sort(Comparator.comparing(MerchantResult::getDistanceValue));
        }
        return resultList;

    }

    @Override
    public Void websiteAddGoods(Integer merchantId, String mobile, Integer count) throws ApiException {
        Merchant merchant = merchantMapper.selectById(merchantId);
        Merchant websiteCode = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getMobile, mobile));
        Asserts.assertNonNull(websiteCode, 500, "网点不存在!");
        Asserts.assertTrue(count > 0, 500, "补货数量要大于0");
        WebsiteGoodsRecord websiteGoodsRecord = new WebsiteGoodsRecord();
        websiteGoodsRecord.setPidPath(websiteCode.getPidPath());
        websiteGoodsRecord.setQuantity(count);
        websiteGoodsRecord.setWebsiteId(websiteCode.getId());
        websiteGoodsRecord.setMerchantId(merchantId);
        websiteGoodsRecordMapper.insert(websiteGoodsRecord);
        if (websiteCode.getOpenId() != null) {
            List<WxMpTemplateData> data = new ArrayList<>();
            data.add(new WxMpTemplateData("first", "二等奖135ml椰岛鹿龟酒已经完成补货！"));
            data.add(new WxMpTemplateData("keyword1", "135ml椰岛鹿龟酒"));
            data.add(new WxMpTemplateData("keyword2", count + "瓶"));
            data.add(new WxMpTemplateData("keyword3", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")));
            data.add(new WxMpTemplateData("keyword4", merchant.getMerchantName()));
            data.add(new WxMpTemplateData("remark", "二等奖135ml椰岛鹿龟酒已经完成补货！"));
            WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                    .toUser(merchant.getOpenId())
                    .templateId("jzGtPNFoz6lzKi1c7q6ELj3BinPBFdtujMVeM4lSobs")
                    .data(data)
                    //.url(String.format("%s#/MerchantReplenishDetail", merchantUrl))
                    .build();
            mpService.sendWxMpTemplateMsg(wxMpTemplateMessage);
        }
        return null;
    }


    @Override
    public MerchantResult getWebsiteByMobile(String mobile) throws ApiException {
        Merchant merchant = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getMobile, mobile));
        Asserts.assertNonNull(merchant, 500, "网点不存在!");
        return BeanUtil.convert(merchant, MerchantResult.class);
    }


    @Override
    public List<GoodsRecordResult> websiteGoodsList(QueryGoodsRecordReq recordReq) {
        List<GoodsRecordResult> result = new ArrayList<>();
        List<WebsiteGoodsRecord> websiteGoodsRecords = websiteGoodsRecordMapper.selectList(Wrappers.lambdaQuery(WebsiteGoodsRecord.class)
                .eq(WebsiteGoodsRecord::getWebsiteId, recordReq.getMerchantId())
                .ge(recordReq.getStartTime() != null, WebsiteGoodsRecord::getCreateTime, recordReq.getStartTime())
                .lt(recordReq.getEndTime() != null, WebsiteGoodsRecord::getCreateTime, recordReq.getEndTime()).orderByDesc(WebsiteGoodsRecord::getId));
        if (CollectionUtils.isEmpty(websiteGoodsRecords)) return result;
        List<Integer> merchantIds = websiteGoodsRecords.stream().map(WebsiteGoodsRecord::getMerchantId).distinct().collect(Collectors.toList());
        List<Merchant> merchantList = merchantMapper.selectBatchIds(merchantIds);
        Map<Integer, Merchant> merchantMap = merchantList.stream().collect(Collectors.toMap(Merchant::getId, Function.identity()));
        websiteGoodsRecords.forEach(item -> {
            GoodsRecordResult goodsRecordResult = new GoodsRecordResult();
            Merchant merchant = merchantMap.get(item.getMerchantId());
            if (merchant != null) {
                goodsRecordResult.setMerchantName(merchant.getMerchantName());
            }
            goodsRecordResult.setQuantity(item.getQuantity());
            goodsRecordResult.setCreateTime(item.getCreateTime());
            result.add(goodsRecordResult);
        });
        return result;
    }

    @Override
    public Integer getWebsiteCodeBindCount(Integer merchantId) {
        Integer count = getCurrentWebsiteCodeCount(merchantId, null, null);
        return count == null ? 0 : count;
    }

    @Override
    public void updateOpenId(Integer merchantId, String openId) {
        if (merchantId != null) {
            Merchant merchant = new Merchant();
            merchant.setId(merchantId);
            merchant.setOpenId(openId);
            merchant.setUpdateTime(LocalDateTime.now());
            merchantMapper.updateById(merchant);
        }
    }


    @Override
    public Void modifyPwd(Integer currentAdminUserId, String oldPwd, String newPwd) {
        Asserts.assertStringNotBlank(oldPwd, 500, "请输入旧密码！");
        Asserts.assertStringNotBlank(oldPwd, 500, "请输入新密码！");
        Merchant merchant = merchantMapper.selectById(currentAdminUserId);
        oldPwd = SecureUtil.md5(oldPwd);
        newPwd = SecureUtil.md5(newPwd);
        Asserts.assertEquals(oldPwd, merchant.getPassword(), 500, "旧密码验证错误！");
        merchant.setPassword(newPwd);
        merchantMapper.updateById(merchant);
        return null;
    }

    public MerchantMyselfResult getMyselfWebsite(Integer merchantId, Date stareTime, Date endTime) {
        MerchantMyselfResult myselfResult = new MerchantMyselfResult();
        AtomicReference<Integer> count = new AtomicReference<>(0);
        AtomicReference<Integer> currentExchange = new AtomicReference<>(0);
        AtomicReference<Integer> totalExchange = new AtomicReference<>(0);
        AtomicReference<Integer> currentGoodsRecord = new AtomicReference<>(0);
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getPid, merchantId)
                .eq(Merchant::getRoleAlias, "wd")
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N");
        List<Merchant> merchantList = merchantMapper.selectList(lambdaQueryWrapper);
        /**
         * 判断自己有没有绑定网点码，如果有将自己插入到第一个
         */
        if (getWebsiteCodeBindCount(merchantId) > 0) {
            Merchant merchant = merchantMapper.selectById(merchantId);
            merchantList.add(0, merchant);
        }
        merchantList.forEach(item -> {
            count.updateAndGet(v -> v + getWebsiteCodeBindCount(item.getId()));
            currentExchange.updateAndGet(v -> v + getCurrentExchangeByPid(item.getId(), stareTime, endTime));
            totalExchange.updateAndGet(v -> v + getCurrentExchangeByPid(item.getId(), null, null));
            currentGoodsRecord.updateAndGet(v -> v + websiteGoodsRecordDao.sumGoodsRecordByMerchantId(item.getId(), stareTime, endTime));
        });
        myselfResult.setCurrentExchange(currentExchange.get());
        myselfResult.setTotalExchange(totalExchange.get());
        myselfResult.setCurrentGoodsRecord(currentGoodsRecord.get());
        myselfResult.setCount(count.get());
        return myselfResult;
    }

    private Integer getCurrentWebsiteCodeCount(Integer merchantId, Date startTime, Date endTime) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getMerchantId, merchantId)
                .eq(WebsiteCodeDetail::getIsActivate, "Y")
                .ge(startTime != null, WebsiteCodeDetail::getActivityTime, startTime)
                .lt(endTime != null, WebsiteCodeDetail::getActivityTime, endTime);
        return websiteCodeDetailMapper.selectCount(lambdaQueryWrapper);
    }

    private Integer getCurrentWebsiteCodeCount(Integer merchantId, Integer currentMerchantId) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getMerchantId, merchantId)
                .like(WebsiteCodeDetail::getMerchantPidPath, "." + currentMerchantId + ".")
                .eq(WebsiteCodeDetail::getIsActivate, "Y");
        return websiteCodeDetailMapper.selectCount(lambdaQueryWrapper);
    }


    private Integer getAllWebsiteCodeCount(String merchantPidPath) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .likeRight(WebsiteCodeDetail::getMerchantPidPath, merchantPidPath)
                .eq(WebsiteCodeDetail::getIsActivate, "Y");
        return websiteCodeDetailMapper.selectCount(lambdaQueryWrapper);
    }

    private Integer getCurrentWebsiteCount(Integer merchantId) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getPid, merchantId)
                .eq(Merchant::getRoleAlias, "wd")
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N");
        return merchantMapper.selectCount(lambdaQueryWrapper);
    }


    private Integer getCurrentExchange(String pidPath, Date startTime, Date endTime) {
        LambdaQueryWrapper lambdaQueryWrapper = Wrappers.<WebsiteBill>lambdaQuery()
                .likeRight(WebsiteBill::getPidPath, pidPath)
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

    private BigDecimal getTotalPostage(int totalCount) {
        BigDecimal totalPostage = new BigDecimal(String.valueOf(DEFAULT_POSTAGE));
        float g = new BigDecimal(totalCount).multiply(new BigDecimal(PIECE_WEIGHT)).floatValue();//每张大约4.3g
        if (g > 1000) {
            double amount = Math.ceil((g - 1000) / 1000f) * OVERWEIGHT_POSTAGE;
            totalPostage = totalPostage.add(new BigDecimal(String.valueOf(amount)));
        }
        return totalPostage;
    }

}
