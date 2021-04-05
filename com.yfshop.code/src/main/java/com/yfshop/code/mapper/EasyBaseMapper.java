package com.yfshop.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * 扩展通用 Mapper，支持数据批量插入
 */
public interface EasyBaseMapper<T> extends BaseMapper<T> {

    Integer insertBatchSomeColumn(Collection<T> entityList);
}
