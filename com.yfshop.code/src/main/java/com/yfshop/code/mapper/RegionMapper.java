package com.yfshop.code.mapper;

import com.yfshop.code.model.Region;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 行政区域表 Mapper 接口
 * </p>
 *
 * @author yoush
 * @since 2021-03-26
 */
public interface RegionMapper extends BaseMapper<Region> {
    // 根据名称模糊查询
    List<Region> queryByFuzzyRegionName(@Param("list") List<String> fuzzyRegionNames);
}
