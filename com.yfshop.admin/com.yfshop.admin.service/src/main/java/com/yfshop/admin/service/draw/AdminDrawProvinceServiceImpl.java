package com.yfshop.admin.service.draw;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.draw.request.CreateDrawActivityReq;
import com.yfshop.admin.api.draw.request.QueryProvinceRateReq;
import com.yfshop.admin.api.draw.request.SaveProvinceRateReq;
import com.yfshop.admin.api.draw.result.DrawProvinceResult;
import com.yfshop.admin.api.draw.service.AdminDrawProvinceService;
import com.yfshop.code.mapper.DrawPrizeMapper;
import com.yfshop.code.mapper.DrawProvinceRateMapper;
import com.yfshop.code.mapper.RegionMapper;
import com.yfshop.code.model.DrawPrize;
import com.yfshop.code.model.DrawProvinceRate;
import com.yfshop.code.model.Region;
import com.yfshop.code.model.RlItemHotpot;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Title:抽奖省份定制化中奖几率Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 11:13:23
 * @Version:1.1.0
 */
@DubboService
public class AdminDrawProvinceServiceImpl implements AdminDrawProvinceService {
    @Resource
    private DrawPrizeMapper drawPrizeMapper;
    @Resource
    private DrawProvinceRateMapper drawProvinceRateMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private RegionMapper regionMapper;

    @Override
    public DrawProvinceResult getYfDrawProvinceById(Integer id) throws ApiException {
        if (id == null || id <= 0) return null;
        DrawProvinceResult yfDrawProvinceResult = null;
        DrawProvinceRate provinceRate = this.drawProvinceRateMapper.selectById(id);
        if (provinceRate != null) {
            yfDrawProvinceResult = new DrawProvinceResult();
            BeanUtil.copyProperties(provinceRate, yfDrawProvinceResult);
        }
        return yfDrawProvinceResult;
    }

    @Override
    public List<DrawProvinceResult> getAll(QueryProvinceRateReq req) throws ApiException {
        LambdaQueryWrapper<DrawProvinceRate> queryWrapper = Wrappers.lambdaQuery(DrawProvinceRate.class)
                .eq(req.getActId() != null, DrawProvinceRate::getActId, req.getActId())
                .eq(req.getFirstPrizeId() != null, DrawProvinceRate::getFirstPrizeId, req.getFirstPrizeId())
                .eq(req.getSecondPrizeId() != null, DrawProvinceRate::getSecondPrizeId, req.getSecondPrizeId())
                .eq(req.getThirdPrizeId() != null, DrawProvinceRate::getThirdPrizeId, req.getThirdPrizeId())
                .eq(req.getProvinceId() != null, DrawProvinceRate::getProvinceId, req.getProvinceId())
                .eq(StringUtils.isNotBlank(req.getProvinceName()), DrawProvinceRate::getProvinceName, req.getProvinceName())
                .orderByDesc(DrawProvinceRate::getId);

        List<DrawProvinceRate> dataList = drawProvinceRateMapper.selectList(queryWrapper);
        return BeanUtil.convertList(dataList, DrawProvinceResult.class);
    }


    @Override
    public List<DrawProvinceResult> getProvinceRate(Integer id) {
        List<DrawProvinceRate> dataList = drawProvinceRateMapper.selectList(Wrappers.lambdaQuery(DrawProvinceRate.class).eq(DrawProvinceRate::getActId, id));
        return BeanUtil.convertList(dataList, DrawProvinceResult.class);
    }


    @Override
    public Void saveProvinceRate(List<SaveProvinceRateReq> req) throws ApiException {
        if (CollectionUtils.isEmpty(req)) return null;
        Asserts.assertFalse(req.size() > 34, 500, "省份数量不正确");

        req.forEach(provinceRate -> {
            Asserts.assertNonNull(provinceRate.getProvinceId(), 500, "省份id不可以为空");
            Asserts.assertStringNotBlank(provinceRate.getProvinceName(), 500, "省份名称不可以为空");
            Asserts.assertNonNull(provinceRate.getFirstWinRate(), 500, "一等奖中奖概率不可以为空");
            Asserts.assertNonNull(provinceRate.getSecondWinRate(), 500, "二等奖大瓶中奖概率不可以为空");
            Asserts.assertNonNull(provinceRate.getSecondSmallBoxWinRate(), 500, "二等奖小瓶中奖概率不可以为空");

            int bigWinRate = provinceRate.getFirstWinRate() + provinceRate.getSecondWinRate();
            int smallWinRate = provinceRate.getFirstWinRate() + provinceRate.getSecondSmallBoxWinRate();
            Asserts.assertFalse(bigWinRate > 1000000 || smallWinRate > 1000000,
                    500, provinceRate.getProvinceName() + "一等奖品加二等奖品概率之和不能大于100");
        });
        List<DrawProvinceRate> drawProvinceRateList = new ArrayList<>();
        Integer actId = req.get(0).getActId();
        drawProvinceRateMapper.delete(Wrappers.lambdaQuery(DrawProvinceRate.class).eq(DrawProvinceRate::getActId, actId));
        Map<Integer,DrawPrize> drawPrizeMap = drawPrizeMapper.selectList(Wrappers.lambdaQuery(DrawPrize.class).eq(DrawPrize::getActId, actId)).stream().collect(Collectors.toMap(DrawPrize::getPrizeLevel, Function.identity()));
        req.forEach(item -> {
            DrawProvinceRate drawProvinceRate = BeanUtil.convert(item, DrawProvinceRate.class);
                if (drawPrizeMap.get(1) != null) {
                    drawProvinceRate.setFirstPrizeId(drawPrizeMap.get(1).getId());
                }
                if (drawPrizeMap.get(2)  != null) {
                    drawProvinceRate.setSecondPrizeId(drawPrizeMap.get(2).getId());
                }
                if (drawPrizeMap.get(3) != null) {
                    drawProvinceRate.setThirdPrizeId(drawPrizeMap.get(3).getId());
                }
            drawProvinceRateMapper.insert(drawProvinceRate);
            drawProvinceRateList.add(drawProvinceRate);
        });
        redisService.set(CacheConstants.DRAW_PROVINCE_RATE_PREFIX + actId,
                JSON.toJSONString(drawProvinceRateList), 60 * 60 * 24 * 30);
        return null;
    }

    @Override
    public Void deleteProvinceRate(Integer id) {
        DrawProvinceRate drawProvinceRate = drawProvinceRateMapper.selectById(id);
        if (drawProvinceRate == null) return null;
        drawProvinceRateMapper.deleteById(id);
        List<DrawProvinceRate> drawProvinceRateList = drawProvinceRateMapper.selectList(Wrappers.lambdaQuery(DrawProvinceRate.class)
                .eq(DrawProvinceRate::getActId, drawProvinceRate.getActId()));
        redisService.set(CacheConstants.DRAW_PROVINCE_RATE_PREFIX + drawProvinceRate.getActId(),
                JSON.toJSONString(drawProvinceRateList), 60 * 60 * 24 * 30);
        return null;
    }

}

