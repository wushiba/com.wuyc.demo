package com.yfshop.shop.service.merchant;

import java.time.LocalDateTime;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.enums.ReceiveWayEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.merchant.req.QueryMerchant;
import com.yfshop.shop.service.merchant.result.MerchantResult;
import com.yfshop.shop.service.merchant.service.FrontMerchantService;
import com.yfshop.shop.utils.Ip2regionUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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

    @Resource
    private RegionMapper regionMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


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
        List<MerchantResult> resultList = new ArrayList<>();
        int limit = 100;
        // 中心位置半径100km内的前100个门店
        Circle circle = new Circle(new Point(longitude, latitude), new Distance(CacheConstants.USER_MERCHANT_DISTANCE, RedisGeoCommands.DistanceUnit.KILOMETERS));
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates().includeDistance().sortAscending().limit(limit);
        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoLocationGeoResults = redisTemplate.opsForGeo().radius(CacheConstants.MERCHANT_GRO_DATA, circle, args);
        List<Integer> merchantIds = new ArrayList<>();
        Map<Integer, Distance> mapDistance = new HashMap<>();
        if (geoLocationGeoResults != null) {
            for (GeoResult<RedisGeoCommands.GeoLocation<Object>> locationGeoResult : geoLocationGeoResults) {
                RedisGeoCommands.GeoLocation<Object> content = locationGeoResult.getContent();
                Distance dist = locationGeoResult.getDistance();
                Object merchantId = content.getName();
                merchantIds.add(Integer.valueOf(merchantId.toString()));
                mapDistance.put(Integer.valueOf(merchantId.toString()), dist);
            }
        }
        if (CollectionUtils.isEmpty(merchantIds)) {
            return resultList;
        }
        Map<Integer, MerchantDetail> merchantDetailMap = merchantDetailMapper.selectList(Wrappers.lambdaQuery(MerchantDetail.class).in(MerchantDetail::getMerchantId, merchantIds)).stream().collect(Collectors.toMap((item) -> item.getMerchantId(), item -> item));
        List<Merchant> list = merchantMapper.selectList(Wrappers.lambdaQuery(Merchant.class)
                .in(Merchant::getId, merchantIds)
                .eq(Merchant::getIsDelete, 'N')
                .eq(Merchant::getIsEnable, 'Y'));
        list.forEach(item -> {
            MerchantResult result = BeanUtil.convert(item, MerchantResult.class);
            Distance distance = mapDistance.get(item.getId());
            result.setDistanceValue(distance.getValue());
            if ("km".equals(distance.getUnit()) && distance.getValue() < 0) {
                result.setDistance(String.format("%.1f千米", distance.getValue()));
            } else {
                result.setDistance(String.format("%d米", distance.getValue() * 1000));
            }
            MerchantDetail merchantDetail = merchantDetailMap.get(distance.getValue());
            if (merchantDetail != null) {
                result.setLatitude(merchantDetail.getLatitude());
                result.setLongitude(merchantDetail.getLongitude());
            }
            resultList.add(result);
        });
        resultList.sort(Comparator.comparing(MerchantResult::getDistanceValue));
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

    @Override
    public IPage<MerchantResult> findMerchantList(QueryMerchant queryMerchant) throws ApiException {
        if (queryMerchant.getProvinceId() == null) {
            try {
                String city = Ip2regionUtil.getRegionByIp(queryMerchant.getIpStr()).split("\\|")[3];
                Region region = regionMapper.selectOne(Wrappers.lambdaQuery(Region.class).eq(Region::getType, 2)
                        .like(Region::getName, city));
                if (region != null) {
                    queryMerchant.setCityId(region.getId());
                }
            } catch (Exception e) {

            }
        }
        Page<Merchant> page = merchantMapper.selectPage(new Page<>(queryMerchant.getPageIndex(), queryMerchant.getPageSize()),
                Wrappers.lambdaQuery(Merchant.class)
                        .eq(Merchant::getRoleAlias, "wd")
                        .eq(Merchant::getIsEnable, "Y")
                        .eq(Merchant::getIsDelete, "N")
                        .eq(queryMerchant.getProvinceId() != null, Merchant::getProvinceId, queryMerchant.getProvinceId())
                        .eq(queryMerchant.getCityId() != null, Merchant::getCityId, queryMerchant.getCityId())
                        .eq(queryMerchant.getDistrictId() != null, Merchant::getDistrictId, queryMerchant.getDistrictId()));
        return BeanUtil.iPageConvert(page, MerchantResult.class);
    }

}
