package com.yfshop.code.manager.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.yfshop.code.model.Trace;
import com.yfshop.code.mapper.TraceMapper;
import com.yfshop.code.manager.TraceManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-05-18
 */
@Service
public class TraceManagerImpl extends ServiceImpl<TraceMapper, Trace> implements TraceManager {
    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public boolean saveBatch(Collection<Trace> entityList,int size) {
        getBaseMapper().insertBatchSomeColumn(entityList);
        return true;
    }
}
