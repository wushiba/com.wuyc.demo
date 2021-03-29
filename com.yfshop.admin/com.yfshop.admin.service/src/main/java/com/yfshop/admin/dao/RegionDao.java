package com.yfshop.admin.dao;

import com.yfshop.code.model.Region;
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
public interface RegionDao {
    // 根据名称模糊查询
    List<Region> queryByFuzzyRegionName(@Param("list") List<String> fuzzyRegionNames);
}
