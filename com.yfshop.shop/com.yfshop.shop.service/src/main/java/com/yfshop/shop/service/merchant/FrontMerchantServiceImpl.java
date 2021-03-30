package com.yfshop.shop.service.merchant;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.Merchant;
import com.yfshop.code.model.MerchantDetail;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.exception.ApiException;
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
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DubboService
public class FrontMerchantServiceImpl implements FrontMerchantService {

    private static final Logger logger = LoggerFactory.getLogger(FrontMerchantServiceImpl.class);

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private MerchantDetailMapper merchantDetailMapper;

    @Resource
    private RedisService redisService;

    /**
     * 根据当前位置查询附近门店
     * @param longitude     经度
     * @param latitude      纬度
     * @return
     * @throws ApiException
     */
    @Override
    public List<MerchantResult> findNearMerchantList(Double longitude, Double latitude) throws ApiException {
        List<MerchantResult> merchantResultList = initWdMerchantList();
        if (CollectionUtils.isEmpty(merchantResultList)) {
            return null;
        }

        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults = redisService.findNearDataList(CacheConstants.MERCHANT_GRO_DATA,
                longitude, latitude, CacheConstants.USER_MERCHANT_DISTANCE, CacheConstants.USER_MERCHANT_DISTANCE_UNIT);
        if (geoResults == null) {
            return null;
        }

        List<MerchantResult> resultList = new ArrayList<>();
        Map<Integer, List<MerchantResult>> merchantMap = merchantResultList.stream().collect(Collectors.groupingBy(MerchantResult::getId));
        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> locationGeoResult : geoResults) {
            Distance dist = locationGeoResult.getDistance();
            RedisGeoCommands.GeoLocation<Object> content = locationGeoResult.getContent();
            Object merchantId = content.getName();
            Point point = content.getPoint();
            System.out.println("merchantId=" + merchantId.toString() + "&distance=" + dist.toString() + "&coordinate=" + point.toString());
            resultList.addAll(merchantMap.get(Integer.valueOf(merchantId.toString())));
        }
        return resultList;
    }

    /**
     * 初始化网点
     */
    private List<MerchantResult> initWdMerchantList() {
        Object merchantListObject = redisService.get(CacheConstants.MERCHANT_INFO_DATA);
        if (merchantListObject != null) {
            return JSON.parseArray(merchantListObject.toString(), MerchantResult.class);
        }

        List<Merchant> merchantList = merchantMapper.selectList(Wrappers.lambdaQuery(Merchant.class)
                .eq(Merchant::getRoleAlias, GroupRoleEnum.WD.getCode())
                .orderByDesc(Merchant::getId));
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
        redisService.set(CacheConstants.MERCHANT_INFO_DATA, JSON.toJSONString(merchantResultList));

        // 更新缓存中的商户经纬度，先删除后更新
        Long aLong = redisService.zRemove(CacheConstants.MERCHANT_GRO_DATA);
        logger.info("======zSetRemoveSize=" + aLong);

        detailList.forEach(merchantDetail -> {
            redisService.geoAdd(CacheConstants.MERCHANT_GRO_DATA, merchantDetail.getLongitude(), merchantDetail.getLatitude(), merchantDetail.getMerchantId());
        });
        return merchantResultList;
    }
}
