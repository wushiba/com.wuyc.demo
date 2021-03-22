package com.yfshop.code.manager.impl;

import com.yfshop.code.model.Region;
import com.yfshop.code.mapper.RegionMapper;
import com.yfshop.code.manager.RegionManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 行政区域表 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Service
public class RegionManagerImpl extends ServiceImpl<RegionMapper, Region> implements RegionManager {

}
