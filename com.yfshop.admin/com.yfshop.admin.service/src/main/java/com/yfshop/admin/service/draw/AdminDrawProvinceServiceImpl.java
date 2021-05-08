package com.yfshop.admin.service.draw;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.draw.request.QueryProvinceRateReq;
import com.yfshop.admin.api.draw.request.SaveProvinceRateReq;
import com.yfshop.admin.api.draw.result.DrawProvinceResult;
import com.yfshop.admin.api.draw.service.AdminDrawProvinceService;
import com.yfshop.code.mapper.DrawProvinceRateMapper;
import com.yfshop.code.model.DrawProvinceRate;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

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
    private DrawProvinceRateMapper drawProvinceRateMapper;

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
    public List<DrawProvinceResult> getProvinceRate() {
        List<DrawProvinceRate> dataList = drawProvinceRateMapper.selectList(Wrappers.emptyWrapper());
        return BeanUtil.convertList(dataList, DrawProvinceResult.class);
    }


    @Override
    public Void saveProvinceRate(List<SaveProvinceRateReq> req) throws ApiException {
        req.forEach(item -> {
            DrawProvinceRate drawProvinceRate = BeanUtil.convert(item, DrawProvinceRate.class);
            if (item.getId() == null) {
                drawProvinceRateMapper.insert(drawProvinceRate);
            } else {
                drawProvinceRateMapper.updateById(drawProvinceRate);
            }
        });
        return null;
    }

    @Override
    public Void deleteProvinceRate(Integer id) {
        drawProvinceRateMapper.deleteById(id);
        return null;
    }

}

