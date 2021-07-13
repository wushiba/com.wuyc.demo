package com.yfshop.admin.service.coupon;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.coupon.request.CreateCouponReq;
import com.yfshop.admin.api.coupon.request.QueryCouponReq;
import com.yfshop.admin.api.coupon.result.CouponRulesResult;
import com.yfshop.admin.api.coupon.result.YfCouponResult;
import com.yfshop.admin.api.coupon.service.AdminCouponService;
import com.yfshop.code.mapper.CouponMapper;
import com.yfshop.code.mapper.CouponRulesMapper;
import com.yfshop.code.mapper.ItemMapper;
import com.yfshop.code.mapper.UserCouponMapper;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.UserCouponStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.DateUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Title:平台优惠券Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 13:47:17
 * @Version:1.1.0
 */
@DubboService
public class YfCouponServiceImpl implements AdminCouponService {

    @Resource
    private CouponMapper couponMapper;
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private UserCouponMapper userCouponMapper;
    @Resource
    private CouponRulesMapper couponRulesMapper;

    @Override
    public YfCouponResult getYfCouponById(Integer id) throws ApiException {
        if (id == null || id <= 0) return null;
        YfCouponResult yfCouponResult = null;
        Coupon coupon = couponMapper.selectById(id);
        if (coupon != null) {
            yfCouponResult = new YfCouponResult();
            BeanUtil.copyProperties(coupon, yfCouponResult);
        }
        return yfCouponResult;
    }

    @Override
    public Page<YfCouponResult> findYfCouponListByPage(QueryCouponReq req) throws ApiException {
        Page<Coupon> page = new Page<>(req.getPageIndex(), req.getPageSize());
        LambdaQueryWrapper<Coupon> queryWrapper = Wrappers.<Coupon>lambdaQuery()
                .eq(StringUtils.isNotBlank(req.getIsEnable()), Coupon::getIsEnable, req.getIsEnable())
                .like(StringUtils.isNotBlank(req.getCouponTitle()), Coupon::getCouponTitle, req.getCouponTitle());
        Page<Coupon> pageData = couponMapper.selectPage(page, queryWrapper);
        Page<YfCouponResult> data = new Page<>(req.getPageIndex(), req.getPageSize(), page.getTotal());
        data.setRecords(getCouponResultList(pageData.getRecords()));
        return data;
    }

    @Override
    public List<YfCouponResult> getAll(QueryCouponReq req) throws ApiException {
        LambdaQueryWrapper<Coupon> queryWrapper = Wrappers.<Coupon>lambdaQuery()
                .eq(StringUtils.isNotBlank(req.getIsEnable()), Coupon::getIsEnable, req.getIsEnable())
                .like(StringUtils.isNotBlank(req.getCouponTitle()), Coupon::getCouponTitle, req.getCouponTitle());
        List<Coupon> dataList = couponMapper.selectList(queryWrapper);
        return getCouponResultList(dataList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertYfCoupon(CreateCouponReq couponReq) throws ApiException {
        checkCouponParams(couponReq);
        Coupon coupon = BeanUtil.convert(couponReq, Coupon.class);
        coupon.setCreateTime(LocalDateTime.now());
        coupon.setIsDelete("N");
        coupon.setIsEnable("N");
        couponMapper.insert(coupon);
        if (!"draw".equalsIgnoreCase(coupon.getCouponResource())) {
            CouponRules couponRules = new CouponRules();
            couponRules.setCreateTime(LocalDateTime.now());
            couponRules.setUpdateTime(LocalDateTime.now());
            couponRules.setCouponId(coupon.getId());
            couponRules.setConditions(couponReq.getCouponRulesConditions());
            couponRules.setItemIds(couponReq.getCouponRulesItemIds());
            couponRules.setIsEnable("N");
            couponRules.setLimitCount(couponReq.getLimitAmount());
            couponRules.setCouponId(coupon.getId());
            couponRulesMapper.insert(couponRules);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateYfCoupon(CreateCouponReq couponReq) throws ApiException {
        checkCouponParams(couponReq);
        Coupon coupon = BeanUtil.convert(couponReq, Coupon.class);
        couponMapper.updateById(coupon);
        if (!"draw".equalsIgnoreCase(coupon.getCouponResource())) {
            CouponRules couponRules = new CouponRules();
            couponRules.setCouponId(coupon.getId());
            couponRules.setConditions(couponReq.getCouponRulesConditions());
            couponRules.setItemIds(couponReq.getCouponRulesItemIds());
            couponRules.setLimitCount(couponReq.getLimitAmount());
            couponRules.setCouponId(coupon.getId());
            couponRulesMapper.update(couponRules, Wrappers.lambdaQuery(CouponRules.class)
                    .eq(CouponRules::getCouponId, coupon.getId()));
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteYfCoupon(Integer couponId) throws ApiException {
        Coupon coupon = couponMapper.selectById(couponId);
        Asserts.assertNonNull(coupon, 500, "优惠券不存在");
        couponMapper.deleteById(couponId);
        couponRulesMapper.delete(Wrappers.lambdaQuery(CouponRules.class).eq(CouponRules::getCouponId, coupon.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCouponStatus(Integer couponId, String isEnable) throws ApiException {
        Coupon coupon = couponMapper.selectById(couponId);
        Asserts.assertNonNull(coupon, 500, "优惠券不存在");
        coupon.setIsEnable(isEnable);
        couponMapper.updateById(coupon);
        CouponRules couponRules = new CouponRules();
        couponRules.setIsEnable(coupon.getIsEnable());
        couponRulesMapper.update(couponRules, Wrappers.lambdaQuery(CouponRules.class).eq(CouponRules::getCouponId, coupon.getId()));
    }


    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_COUPON_RULES_NAME,
            key = "'" + CacheConstants.MALL_COUPON_RULES_KEY_PREFIX + "'")
    @Override
    public List<CouponRulesResult> getCouponRulesList() {
        List<CouponRules> couponRulesList = couponRulesMapper.selectList(Wrappers.lambdaQuery(CouponRules.class).eq(CouponRules::getIsEnable, "Y"));
        return BeanUtil.convertList(couponRulesList, CouponRulesResult.class);
    }

    public void checkCouponParams(CreateCouponReq couponReq) {
        // DATE_RANGE(日期范围), TODAY(领取当天), FIX_DAY(固定天数)
        String validType = couponReq.getValidType();
        if ("DATE_RANGE".equalsIgnoreCase(validType)) {
            Asserts.assertNonNull(couponReq.getValidStartTime(), 500, "开始日期不可以为空");
            Asserts.assertNonNull(couponReq.getValidEndTime(), 500, "结束日期不可以为空");
            couponReq.setValidDay(null);
        } else if ("FIX_DAY".equalsIgnoreCase(validType)) {
            Asserts.assertFalse(couponReq.getValidDay() == null || couponReq.getValidDay() <= 0,
                    500, "领取后有效日期不可以为空");
            couponReq.setValidStartTime(null);
            couponReq.setValidEndTime(null);
        } else {
            couponReq.setValidStartTime(null);
            couponReq.setValidEndTime(null);
            couponReq.setValidDay(null);
        }

        String useRangeType = couponReq.getUseRangeType();
        if ("ALL".equalsIgnoreCase(useRangeType)) {
            couponReq.setCanUseItemIds(null);
        } else if ("ITEM".equalsIgnoreCase(useRangeType)) {
            Asserts.assertStringNotBlank(couponReq.getCanUseItemIds(), 500, "请选择可使用的商品");
        }
    }


    private List<YfCouponResult> getCouponResultList(List<Coupon> dataList) {
        List<YfCouponResult> list = BeanUtil.convertList(dataList, YfCouponResult.class);
        List<Integer> ids = dataList.stream()
                .filter(item -> StringUtils.isNotBlank(item.getCanUseItemIds()))
                .flatMap((item) -> Arrays.stream(item.getCanUseItemIds().split(","))
                        .map(Integer::valueOf))
                .distinct().collect(Collectors.toList());
        Map<Integer, String> itemMaps = itemMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(Item::getId, Item::getItemTitle));
        list.forEach(item -> {
            if (StringUtils.isNotBlank(item.getCanUseItemIds())) {
                List<Integer> itemIds = Arrays.stream(item.getCanUseItemIds().split(",")).map(Integer::valueOf).collect(Collectors.toList());
                List<String> titles = new ArrayList<>();
                itemIds.forEach(i -> {
                    String title = itemMaps.get(i);
                    if (title != null) {
                        titles.add(title);
                    }
                });
                item.setCanUseItemNames(org.apache.commons.lang.StringUtils.join(titles, ","));
            }
            Integer receiveAmount = userCouponMapper.selectCount(Wrappers.lambdaQuery(UserCoupon.class)
                    .eq(UserCoupon::getCouponId, item.getId()));
            Integer useAmount = userCouponMapper.selectCount(Wrappers.lambdaQuery(UserCoupon.class)
                    .eq(UserCoupon::getCouponId, item.getId())
                    .in(UserCoupon::getUseStatus, UserCouponStatusEnum.HAS_USE.getCode(), UserCouponStatusEnum.IN_USE.getCode()));
            item.setUseAmount(useAmount);
            item.setReceiveAmount(receiveAmount);
            if (!"draw".equalsIgnoreCase(item.getCouponResource())) {
                CouponRules rules = couponRulesMapper.selectOne(Wrappers.lambdaQuery(CouponRules.class).eq(CouponRules::getCouponId, item.getId()));
                if (rules != null) {
                    item.setCouponRulesConditions(rules.getConditions());
                    item.setCouponRulesItemIds(rules.getItemIds());
                }
            }
        });
        return list;
    }


}

