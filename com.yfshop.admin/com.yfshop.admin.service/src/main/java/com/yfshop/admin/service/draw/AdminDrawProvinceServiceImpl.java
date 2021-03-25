package com.yfshop.admin.service.draw;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.draw.request.QueryProvinceRateReq;
import com.yfshop.admin.api.draw.result.YfDrawProvinceResult;
import com.yfshop.admin.api.draw.service.AdminDrawProvinceService;
import com.yfshop.code.mapper.DrawProvinceRateMapper;
import com.yfshop.code.model.DrawProvinceRate;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title:抽奖省份定制化中奖几率Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 11:13:23
 * @Version:1.1.0
 */
@Service(dynamic = true)
public class AdminDrawProvinceServiceImpl implements AdminDrawProvinceService {

    @Resource
    private DrawProvinceRateMapper drawProvinceRateMapper;

    @Override
    public YfDrawProvinceResult getYfDrawProvinceById(Integer id) throws ApiException {
        if (id == null || id <= 0) return null;
        YfDrawProvinceResult yfDrawProvinceResult = null;
        DrawProvinceRate provinceRate = this.drawProvinceRateMapper.selectById(id);
        if (provinceRate != null) {
            yfDrawProvinceResult = new YfDrawProvinceResult();
            BeanUtil.copyProperties(provinceRate, yfDrawProvinceResult);
        }
        return yfDrawProvinceResult;
    }

    @Override
    public List<YfDrawProvinceResult> getAll(QueryProvinceRateReq req) throws ApiException {
        LambdaQueryWrapper<DrawProvinceRate> queryWrapper = Wrappers.lambdaQuery(DrawProvinceRate.class)
                .eq(req.getActId() != null, DrawProvinceRate::getActId, req.getActId())
                .eq(req.getFirstPrizeId() != null, DrawProvinceRate::getFirstPrizeId, req.getFirstPrizeId())
                .eq(req.getSecondPrizeId() != null, DrawProvinceRate::getSecondPrizeId, req.getSecondPrizeId())
                .eq(req.getThirdPrizeId() != null, DrawProvinceRate::getThirdPrizeId, req.getThirdPrizeId())
                .eq(req.getProvinceId() != null, DrawProvinceRate::getProvinceId, req.getProvinceId())
                .eq(StringUtils.isNotBlank(req.getProvinceName()), DrawProvinceRate::getProvinceName, req.getProvinceName())
                .orderByDesc(DrawProvinceRate::getId);

        List<DrawProvinceRate> dataList = drawProvinceRateMapper.selectList(queryWrapper);
        return BeanUtil.convertList(dataList, YfDrawProvinceResult.class);
    }

}

