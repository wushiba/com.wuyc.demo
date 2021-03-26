package com.yfshop.admin.service.draw;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.draw.request.CreateDrawActivityReq;
import com.yfshop.admin.api.draw.request.QueryDrawActivityReq;
import com.yfshop.admin.api.draw.result.YfDrawActivityResult;
import com.yfshop.admin.api.draw.service.AdminDrawActivityService;
import com.yfshop.code.mapper.DrawActivityMapper;
import com.yfshop.code.mapper.DrawPrizeMapper;
import com.yfshop.code.mapper.DrawProvinceRateMapper;
import com.yfshop.code.model.DrawActivity;
import com.yfshop.code.model.DrawPrize;
import com.yfshop.code.model.DrawProvinceRate;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.DateUtil;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Title:抽奖活动Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 11:12:29
 * @Version:1.1.0
 */
@Service(dynamic = true)
public class AdminDrawActivityServiceImpl implements AdminDrawActivityService {

    @Resource
    private RedisService redisService;

    @Resource
    private DrawPrizeMapper drawPrizeMapper;

    @Resource
    private DrawActivityMapper drawActivityMapper;

    @Resource
    private DrawProvinceRateMapper drawProvinceRateMapper;

    @Override
    public YfDrawActivityResult getYfDrawActivityById(Integer id) throws ApiException {
        if (id == null || id <= 0) return null;
        YfDrawActivityResult yfDrawActivityResult = null;
        DrawActivity yfDrawActivity = this.drawActivityMapper.selectById(id);
        if (yfDrawActivity != null) {
            yfDrawActivityResult = new YfDrawActivityResult();
            BeanUtil.copyProperties(yfDrawActivity, yfDrawActivityResult);
        }
        return yfDrawActivityResult;
    }

    @Override
    public Page<YfDrawActivityResult> findYfDrawActivityListByPage(QueryDrawActivityReq req) throws ApiException {
        LambdaQueryWrapper<DrawActivity> queryWrapper = Wrappers.lambdaQuery(DrawActivity.class)
                .eq(req.getIsEnable() != null, DrawActivity::getIsEnable, req.getIsEnable())
                .eq(req.getActTitle() != null, DrawActivity::getActTitle, req.getActTitle())
                .orderByDesc(DrawActivity::getId);

        Page<DrawActivity> itemPage = drawActivityMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), queryWrapper);
        Page<YfDrawActivityResult> page = new Page<>(itemPage.getCurrent(), itemPage.getSize(), itemPage.getTotal());
        page.setRecords(BeanUtil.convertList(itemPage.getRecords(), YfDrawActivityResult.class));
        return page;
    }

    @Override
    public List<YfDrawActivityResult> getAll(QueryDrawActivityReq req) throws ApiException {
        LambdaQueryWrapper<DrawActivity> queryWrapper = Wrappers.lambdaQuery(DrawActivity.class)
                .eq(req.getIsEnable() != null, DrawActivity::getIsEnable, req.getIsEnable())
                .eq(req.getActTitle() != null, DrawActivity::getActTitle, req.getActTitle())
                .orderByDesc(DrawActivity::getId);

        List<DrawActivity> dataList = drawActivityMapper.selectList(queryWrapper);
        return BeanUtil.convertList(dataList, YfDrawActivityResult.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertYfDrawActivity(CreateDrawActivityReq req) throws ApiException {
        checkUpdateParams(req);

        // 创建活动
        LocalDateTime localDateTime = LocalDateTime.now();
        DrawActivity drawActivity = BeanUtil.convert(req, DrawActivity.class);
        drawActivity.setCreateTime(localDateTime);
        drawActivity.setUpdateTime(localDateTime);
        drawActivity.setStartTime(DateUtil.dateToLocalDateTime(req.getStartTime()));
        drawActivity.setEndTime(DateUtil.dateToLocalDateTime(req.getEndTime()));
        drawActivity.setIsEnable("N");
        drawActivityMapper.insert(drawActivity);

        // 创建奖品
        Map<Integer, DrawPrize> prizeMap = new HashMap<>(6);
        List<DrawPrize> drawPrizeList = new ArrayList<>();
        List<CreateDrawActivityReq.DrawPrizeReq> prizeList = req.getPrizeList();
        prizeList.forEach(prize -> {
            DrawPrize drawPrize = BeanUtil.convert(prize, DrawPrize.class);
            drawPrize.setCreateTime(localDateTime);
            drawPrize.setUpdateTime(localDateTime);
            drawPrize.setActId(drawActivity.getId());
            drawPrizeMapper.insert(drawPrize);
            drawPrizeList.add(drawPrize);
            prizeMap.put(prize.getPrizeLevel(), drawPrize);
        });

        // 特殊省份的特殊中奖概率
        List<DrawProvinceRate> provinceRateList = new ArrayList<>();
        List<CreateDrawActivityReq.ProvinceRateReq> provinceList = req.getProvinceRateList();
        if (CollectionUtils.isEmpty(provinceList)) return;
        provinceList.forEach(provinceRateReq -> {
            DrawProvinceRate provinceRate = BeanUtil.convert(provinceRateReq, DrawProvinceRate.class);
            provinceRate.setCreateTime(localDateTime);
            provinceRate.setUpdateTime(localDateTime);
            provinceRate.setFirstPrizeId(prizeMap.get(1).getId());
            provinceRate.setSecondPrizeId(prizeMap.get(2).getId());
            provinceRate.setThirdPrizeId(prizeMap.get(3).getId());
            drawProvinceRateMapper.insert(provinceRate);
            provinceRateList.add(provinceRate);
        });

        // 存入缓存，设置半年有效期有效
        redisService.set(CacheConstants.DRAW_ACTIVITY_PREFIX + drawActivity.getId(),
                JSON.toJSONString(drawActivity), 180 * 24 * 60 * 1000);
        redisService.set(CacheConstants.DRAW_PRIZE_NAME_PREFIX + drawActivity.getId(),
                JSON.toJSONString(drawPrizeList), 180 * 24 * 60 * 1000);
        redisService.set(CacheConstants.DRAW_PROVINCE_RATE_PREFIX + drawActivity.getId(),
                JSON.toJSONString(provinceRateList), 180 * 24 * 60 * 1000);
    }

    @Override
    public void updateYfDrawActivity(CreateDrawActivityReq req) throws ApiException {
        Asserts.assertNonNull(req.getId(), 500, "活动id不可以为空");
        checkUpdateParams(req);
        LocalDateTime localDateTime = LocalDateTime.now();

        // 编辑活动
        DrawActivity drawActivity = BeanUtil.convert(req, DrawActivity.class);
        drawActivity.setUpdateTime(localDateTime);
        drawActivity.setStartTime(DateUtil.dateToLocalDateTime(req.getStartTime()));
        drawActivity.setEndTime(DateUtil.dateToLocalDateTime(req.getEndTime()));
        drawActivityMapper.updateById(drawActivity);

        // 编辑奖品
        Map<Integer, DrawPrize> prizeMap = new HashMap<>(6);
        List<DrawPrize> drawPrizeList = new ArrayList<>();
        List<CreateDrawActivityReq.DrawPrizeReq> prizeList = req.getPrizeList();
        prizeList.forEach(prize -> {
            DrawPrize drawPrize = BeanUtil.convert(prize, DrawPrize.class);
            drawPrizeMapper.updateById(drawPrize);
            drawPrizeList.add(drawPrize);
            prizeMap.put(prize.getPrizeLevel(), drawPrize);
        });


        List<CreateDrawActivityReq.ProvinceRateReq> provinceList = req.getProvinceRateList();
        if (CollectionUtils.isEmpty(provinceList)) return;
        // 特殊省份的中奖概率 先删除后新增
        drawProvinceRateMapper.delete(Wrappers.lambdaQuery(DrawProvinceRate.class)
                .eq(DrawProvinceRate::getActId, req.getId()));

        List<DrawProvinceRate> provinceRateList = new ArrayList<>();
        provinceList.forEach(provinceRateReq -> {
            DrawProvinceRate provinceRate = BeanUtil.convert(provinceRateReq, DrawProvinceRate.class);
            provinceRate.setUpdateTime(localDateTime);
            provinceRate.setCreateTime(localDateTime);
            provinceRate.setFirstPrizeId(prizeMap.get(1).getId());
            provinceRate.setSecondPrizeId(prizeMap.get(2).getId());
            provinceRate.setThirdPrizeId(prizeMap.get(3).getId());
            drawProvinceRateMapper.insert(provinceRate);
            provinceRateList.add(provinceRate);
        });

        // 存入缓存，设置半年有效期有效
        redisService.set(CacheConstants.DRAW_ACTIVITY_PREFIX + drawActivity.getId(),
                JSON.toJSONString(drawActivity), 180 * 24 * 60 * 1000);
        redisService.set(CacheConstants.DRAW_PRIZE_NAME_PREFIX + drawActivity.getId(),
                JSON.toJSONString(drawPrizeList), 180 * 24 * 60 * 1000);
        redisService.set(CacheConstants.DRAW_PROVINCE_RATE_PREFIX + drawActivity.getId(),
                JSON.toJSONString(provinceRateList), 180 * 24 * 60 * 1000);
    }

    @Override
    public void updateYfDrawActivityStatus(Integer id, String isEnable) throws ApiException {
        DrawActivity drawActivity = drawActivityMapper.selectById(id);
        Asserts.assertNonNull(drawActivity, 500, "抽奖活动不存在");
        drawActivity.setIsEnable(isEnable);
        drawActivityMapper.updateById(drawActivity);

        redisService.set(CacheConstants.DRAW_ACTIVITY_PREFIX + drawActivity.getId(),
                JSON.toJSONString(drawActivity), 180 * 24 * 60 * 1000);
    }

    @Override
    public void deleteYfDrawActivityById(Integer id) throws ApiException {
        DrawActivity drawActivity = drawActivityMapper.selectById(id);
        Asserts.assertNonNull(drawActivity, 500, "抽奖活动不存在");
        drawActivityMapper.deleteById(id);

        List<String> keyList = new ArrayList<>();
        keyList.add(CacheConstants.DRAW_ACTIVITY_PREFIX + id);
        keyList.add(CacheConstants.DRAW_PRIZE_NAME_PREFIX + id);
        keyList.add(CacheConstants.DRAW_PROVINCE_RATE_PREFIX + id);
        redisService.del(keyList);
    }

    public void checkUpdateParams(CreateDrawActivityReq req) throws ApiException {
        // 校验奖品
        List<CreateDrawActivityReq.DrawPrizeReq> prizeList = req.getPrizeList();
        Integer levelSize = prizeList.stream().collect(Collectors.groupingBy(CreateDrawActivityReq.DrawPrizeReq::getPrizeLevel)).size();
        Asserts.assertFalse(levelSize != 3, 500, "奖品数量不正确或者等级不能重复");

        int bigBoxRate = 0, smallBoxRate = 0;
        for (CreateDrawActivityReq.DrawPrizeReq prizeReq : prizeList) {
            if (prizeReq.getPrizeLevel() == 1) {
                bigBoxRate = bigBoxRate + prizeReq.getWinRate();
                smallBoxRate = smallBoxRate + prizeReq.getWinRate();
            } else if (prizeReq.getPrizeLevel() == 2) {
                bigBoxRate = bigBoxRate + prizeReq.getWinRate();
                smallBoxRate = smallBoxRate + prizeReq.getSmallBoxRate();
            }
        }
        Asserts.assertFalse(bigBoxRate >= 10000 || smallBoxRate >= 10000, 500, "一等奖品加二等奖品概率之和不能大于100");

        // 校验特殊省份的特殊中奖概率
        List<CreateDrawActivityReq.ProvinceRateReq> provinceRateList = req.getProvinceRateList();
        if (CollectionUtils.isEmpty(provinceRateList)) return;
        Asserts.assertFalse(provinceRateList.size() > 34, 500, "省份数量不正确");

        provinceRateList.forEach(provinceRate -> {
            Asserts.assertNonNull(provinceRate.getProvinceId(), 500, "省份id不可以为空");
            Asserts.assertStringNotBlank(provinceRate.getProvinceName(), 500, "省份名称不可以为空");
            Asserts.assertNonNull(provinceRate.getFirstWinRate(), 500, "一等奖中奖概率不可以为空");
            Asserts.assertNonNull(provinceRate.getSecondWinRate(), 500, "二等奖大瓶中奖概率不可以为空");
            Asserts.assertNonNull(provinceRate.getSecondSmallBoxWinRate(), 500, "二等奖小瓶中奖概率不可以为空");

            int bigWinRate = provinceRate.getFirstWinRate() + provinceRate.getSecondWinRate();
            int smallWinRate = provinceRate.getFirstWinRate() + provinceRate.getSecondSmallBoxWinRate();
            Asserts.assertFalse(bigWinRate > 10000 || smallWinRate > 10000 ,
                    500, provinceRate.getProvinceName() + "一等奖品加二等奖品概率之和不能大于100");
        });
    }
}

