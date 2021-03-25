package com.yfshop.admin.service.sourcefactory;

import com.yfshop.admin.api.sourcefactory.AdminSourceFactoryManageService;
import com.yfshop.admin.api.sourcefactory.req.CreateSourceFactoryReq;
import com.yfshop.admin.api.sourcefactory.req.UpdateSourceFactoryReq;
import com.yfshop.code.mapper.RegionMapper;
import com.yfshop.code.mapper.SourceFactoryMapper;
import com.yfshop.code.model.Region;
import com.yfshop.code.model.SourceFactory;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Xulg
 * Created in 2021-03-25 18:59
 */
@DubboService
@Validated
public class AdminSourceFactoryManageServiceImpl implements AdminSourceFactoryManageService {

    @Resource
    private RegionMapper regionMapper;
    @Resource
    private SourceFactoryMapper sourceFactoryMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createSourceFactory(@NotNull CreateSourceFactoryReq req) throws ApiException {
        Region province = regionMapper.selectById(req.getProvinceId());
        Asserts.assertNonNull(province, 500, "省份信息不存在");
        Region city = regionMapper.selectById(req.getCityId());
        Asserts.assertNonNull(city, 500, "市信息不存在");
        Region district = regionMapper.selectById(req.getDistrictId());
        Asserts.assertNonNull(district, 500, "区信息不存在");
        SourceFactory bean = new SourceFactory();
        BeanUtil.copyProperties(req, bean);
        bean.setProvince(province.getName());
        bean.setCity(city.getName());
        bean.setDistrict(district.getName());
        bean.setCreateTime(LocalDateTime.now());
        bean.setUpdateTime(LocalDateTime.now());
        sourceFactoryMapper.insert(bean);
        return null;
    }

    @Override
    public Void updateSourceFactory(@NotNull UpdateSourceFactoryReq req) throws ApiException {
        return null;
    }

}
