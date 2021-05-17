package com.yfshop.shop.service.merchant;

import java.time.LocalDateTime;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.enums.ReceiveWayEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.merchant.result.MerchantResult;
import com.yfshop.shop.service.merchant.service.FrontMerchantService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DubboService
public class FrontMerchantServiceImpl implements FrontMerchantService {

    private static final Logger logger = LoggerFactory.getLogger(FrontMerchantServiceImpl.class);

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private WebsiteBillMapper websiteBillMapper;

    @Resource
    private OrderAddressMapper orderAddressMapper;

    @Resource
    private MerchantDetailMapper merchantDetailMapper;

    @Resource
    private WebsiteCodeDetailMapper websiteCodeDetailMapper;

    /**
     * 根据当前位置查询附近门店
     *
     * @param districtId 区id
     * @param longitude  经度
     * @param latitude   纬度
     * @return
     * @throws ApiException
     */
    @Override
    public List<MerchantResult> findNearMerchantList(Integer districtId, Double longitude, Double latitude) throws ApiException {
        List<MerchantResult> merchantResultList = initWdMerchantList();
        if (CollectionUtils.isEmpty(merchantResultList)) {
            return null;
        }

        List<MerchantResult> resultList = new ArrayList<>();
        if (districtId != null) {
            resultList = merchantResultList.stream().filter(data -> data.getDistrictId().intValue() == districtId)
                    .collect(Collectors.toList());
            return resultList;
        }

        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults = redisService.findNearDataList(CacheConstants.MERCHANT_GRO_DATA,
                longitude, latitude, CacheConstants.USER_MERCHANT_DISTANCE, CacheConstants.USER_MERCHANT_DISTANCE_UNIT);
        if (geoResults == null) {
            return resultList;
        }

        Map<Integer, List<MerchantResult>> merchantMap = merchantResultList.stream().collect(Collectors.groupingBy(MerchantResult::getId));
        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> locationGeoResult : geoResults) {
            Distance dist = locationGeoResult.getDistance();
            RedisGeoCommands.GeoLocation<Object> content = locationGeoResult.getContent();
            Point point = content.getPoint();
            Object merchantId = content.getName();
            System.out.println("merchantId=" + merchantId.toString() + "&distance=" + dist.toString() + "&coordinate=" + point.toString());
            MerchantResult merchantData = merchantMap.get(Integer.valueOf(merchantId.toString())).get(0);
            merchantData.setDistance(dist.toString());
            resultList.add(merchantData);
        }
        return resultList;
    }

    /**
     * 根据网点码查询商户信息
     *
     * @param websiteCode 网点码
     * @return MerchantResult
     * @throws ApiException
     */
    @Override
    public MerchantResult getMerchantByWebsiteCode(String websiteCode) throws ApiException {
        Asserts.assertStringNotBlank(websiteCode, 500, "网点码不可以为空");

        WebsiteCodeDetail websiteCodeDetail;
        Object websiteCodeObject = redisService.get(CacheConstants.MERCHANT_WEBSITE_CODE + websiteCode);
        if (websiteCodeObject != null) {
            websiteCodeDetail = JSON.parseObject(websiteCodeObject.toString(), WebsiteCodeDetail.class);
        } else {
            websiteCodeDetail = websiteCodeDetailMapper.selectOne(Wrappers.lambdaQuery(WebsiteCodeDetail.class)
                    .eq(WebsiteCodeDetail::getAlias, websiteCode));
            redisService.set(CacheConstants.MERCHANT_WEBSITE_CODE + websiteCode, JSON.toJSONString(websiteCodeDetail), 60 * 20);

        }
        Asserts.assertFalse(websiteCodeDetail == null || websiteCodeDetail.getId() == null, 500, "请扫描正确的网点码");

        Object merchantObject = redisService.get(CacheConstants.MERCHANT_INFO_DATA + websiteCodeDetail.getMerchantId());
        if (merchantObject != null) {
            return JSON.parseObject(merchantObject.toString(), MerchantResult.class);
        }
        Merchant merchant = merchantMapper.selectOne(Wrappers.lambdaQuery(Merchant.class)
                .eq(Merchant::getId, websiteCodeDetail.getMerchantId()));
        MerchantResult merchantResult = BeanUtil.convert(merchant, MerchantResult.class);
        redisService.set(CacheConstants.MERCHANT_INFO_DATA + websiteCodeDetail.getMerchantId(), JSON.toJSONString(merchantResult), 60 * 20);
        return merchantResult;
    }

    /**
     * 用户自提二等奖成功后，生成网点记账单
     *
     * @param orderId 用户主订单id
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void insertWebsiteBill(Long orderId) throws ApiException {
        Asserts.assertNonNull(orderId, 500, "主订单id不可以为空");
        Order order = orderMapper.selectById(orderId);
        Asserts.assertNonNull(order, 500, "订单不存在");
        Asserts.assertNotEquals(order.getReceiveWay(), ReceiveWayEnum.ZT.getCode(), 500, "订单不存在");
        Asserts.assertNonNull(order, 500, "只有二等奖自提订单才可以生成记账单");

        OrderAddress orderAddress = orderAddressMapper.selectOne(Wrappers.lambdaQuery(OrderAddress.class)
                .eq(OrderAddress::getOrderId, orderId));

        List<OrderDetail> detailList = orderDetailMapper.selectList(Wrappers.lambdaQuery(OrderDetail.class)
                .eq(OrderDetail::getOrderId, orderId));

        detailList.forEach(detail -> {
            WebsiteBill websiteBill = new WebsiteBill();
            websiteBill.setCreateTime(LocalDateTime.now());
            websiteBill.setUpdateTime(LocalDateTime.now());
            websiteBill.setMerchantId(detail.getMerchantId());
            websiteBill.setPidPath(detail.getPidPath());
            websiteBill.setUserId(detail.getUserId());
            websiteBill.setNickname(orderAddress.getRealname());
            websiteBill.setOrderId(orderId);
            websiteBill.setItemTitle(detail.getItemTitle());
            websiteBill.setPayPrice(detail.getPayPrice());
            websiteBill.setBillNo(order.getBillNo());
            websiteBill.setIsConfirm("N");
            websiteBill.setWebsiteCode("");
            websiteBillMapper.insert(websiteBill);
        });
        return null;
    }


    //-------------------------------------------------------- privete-method-----------------------------------------------------------------//

    /**
     * 初始化网点
     */
    private List<MerchantResult> initWdMerchantList() {
        Object merchantListObject = redisService.get(CacheConstants.MERCHANT_LIST_INFO_DATA);
        if (merchantListObject != null) {
            return JSON.parseArray(merchantListObject.toString(), MerchantResult.class);
        }
        List<Integer> ids = websiteCodeDetailMapper.selectList(Wrappers.lambdaQuery(WebsiteCodeDetail.class)
                .eq(WebsiteCodeDetail::getIsActivate, "Y"))
                .stream()
                .map(WebsiteCodeDetail::getMerchantId)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        List<Merchant> merchantList = merchantMapper.selectList(Wrappers.lambdaQuery(Merchant.class)
                .in(Merchant::getId, ids)
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N"));
        if (CollectionUtils.isEmpty(merchantList)) {
            return null;
        }
        List<Integer> idList = merchantList.stream().map(Merchant::getId).collect(Collectors.toList());
        List<MerchantDetail> detailList = merchantDetailMapper.selectList(Wrappers.lambdaQuery(MerchantDetail.class)
                .in(MerchantDetail::getMerchantId, idList)
                .orderByDesc(MerchantDetail::getMerchantId));

        Map<Integer, List<MerchantDetail>> detailMap = detailList.stream().collect(
                Collectors.groupingBy(MerchantDetail::getMerchantId));
        List<MerchantResult> merchantResultList = BeanUtil.convertList(merchantList, MerchantResult.class);
        merchantResultList.forEach(merchant -> {
            List<MerchantDetail> merchantDetailList = detailMap.get(merchant.getId());
            if (CollectionUtils.isNotEmpty(merchantDetailList)) {
                merchant.setLongitude(merchantDetailList.get(0).getLongitude());
                merchant.setLatitude(merchantDetailList.get(0).getLatitude());
                merchant.setGeoHash(merchantDetailList.get(0).getGeoHash());
            }
        });

        // 将商户信息存入redis
        redisService.set(CacheConstants.MERCHANT_LIST_INFO_DATA, JSON.toJSONString(merchantResultList), 60 * 10);

        // 更新缓存中的商户经纬度，先删除后更新
        detailList.forEach(merchantDetail -> {
            redisService.zRemove(CacheConstants.MERCHANT_GRO_DATA, merchantDetail.getMerchantId() + "");
            redisService.geoAdd(CacheConstants.MERCHANT_GRO_DATA, merchantDetail.getLongitude(), merchantDetail.getLatitude(), merchantDetail.getMerchantId());
        });

        return merchantResultList;
    }
}
