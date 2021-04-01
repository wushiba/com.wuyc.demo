package com.yfshop.admin.service.sourcefactory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.sourcefactory.AdminSourceFactoryManageService;
import com.yfshop.admin.api.sourcefactory.excel.SourceFactoryExcel;
import com.yfshop.admin.api.sourcefactory.req.CreateSourceFactoryReq;
import com.yfshop.admin.api.sourcefactory.req.ImportSourceFactoryReq;
import com.yfshop.admin.api.sourcefactory.req.QuerySourceFactoriesReq;
import com.yfshop.admin.api.sourcefactory.req.UpdateSourceFactoryReq;
import com.yfshop.admin.api.sourcefactory.result.SourceFactoryResult;
import com.yfshop.code.mapper.RegionMapper;
import com.yfshop.code.mapper.SourceFactoryMapper;
import com.yfshop.code.model.Region;
import com.yfshop.code.model.SourceFactory;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Void createSourceFactory(@Valid @NotNull CreateSourceFactoryReq req) throws ApiException {
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void updateSourceFactory(@Valid @NotNull UpdateSourceFactoryReq req) throws ApiException {
        SourceFactory existSourceFactory = sourceFactoryMapper.selectById(req.getSourceFactoryId());
        Asserts.assertNonNull(existSourceFactory, 500, "编辑工厂不存在");
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
        bean.setUpdateTime(LocalDateTime.now());
        sourceFactoryMapper.updateById(bean);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void importSourceFactory(@Valid @NotNull ImportSourceFactoryReq req) throws ApiException {
        List<SourceFactoryExcel> excels = req.getExcels();
        // find all regions
        Set<RegionWrapper> regionWrappers = new HashSet<>();
        for (SourceFactoryExcel excel : excels) {
            regionWrappers.add(new RegionWrapper(excel.getProvince(), 1));
            regionWrappers.add(new RegionWrapper(excel.getCity(), 2));
            regionWrappers.add(new RegionWrapper(excel.getDistrict(), 3));
        }
        Map<String, Region> regionIndexMap = regionWrappers.parallelStream()
                .collect(Collectors.toMap(RegionWrapper::getRegionName, rw -> this.findRegion(rw.getRegionName(), rw.getType())));

        // batch create
        List<CreateSourceFactoryReq> reqs = excels.stream().map(excel -> {
            CreateSourceFactoryReq temp = new CreateSourceFactoryReq();
            BeanUtil.copyProperties(excel, temp);
            temp.setProvinceId(regionIndexMap.get(excel.getProvince()).getId());
            temp.setCityId(regionIndexMap.get(excel.getCity()).getId());
            temp.setDistrictId(regionIndexMap.get(excel.getDistrict()).getId());
            return temp;
        }).collect(Collectors.toList());
        reqs.parallelStream().forEach(this::createSourceFactory);
        return null;
    }

    @Override
    public IPage<SourceFactoryResult> pageQuerySourceFactories(QuerySourceFactoriesReq req) {
        if (req == null) {
            return BeanUtil.emptyPageData(1, 10);
        }
        LambdaQueryWrapper<SourceFactory> queryWrapper = Wrappers.lambdaQuery(SourceFactory.class)
                .eq(StringUtils.isNotBlank(req.getFactoryName()), SourceFactory::getFactoryName, req.getFactoryName());
        Page<SourceFactory> page = sourceFactoryMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), queryWrapper);
        return BeanUtil.iPageConvert(page, SourceFactoryResult.class);
    }

    private Region findRegion(String regionName, int type) {
        return regionMapper.selectList(Wrappers.lambdaQuery(Region.class)
                .eq(Region::getType, type).likeRight(Region::getName, regionName))
                .stream().findFirst().orElseThrow(() -> new ApiException(500, "未能查询" + regionName + "信息"));
    }

    @Data
    @AllArgsConstructor
    private static class RegionWrapper {
        private String regionName;
        private int type;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RegionWrapper that = (RegionWrapper) o;
            if (type != that.type) {
                return false;
            }
            return Objects.equals(regionName, that.regionName);
        }

        @Override
        public int hashCode() {
            int result = regionName != null ? regionName.hashCode() : 0;
            result = 31 * result + type;
            return result;
        }
    }
}
