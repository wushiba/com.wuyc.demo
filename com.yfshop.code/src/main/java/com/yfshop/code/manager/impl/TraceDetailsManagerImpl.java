package com.yfshop.code.manager.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.yfshop.code.model.Trace;
import com.yfshop.code.model.TraceDetails;
import com.yfshop.code.mapper.TraceDetailsMapper;
import com.yfshop.code.manager.TraceDetailsManager;
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
public class TraceDetailsManagerImpl extends ServiceImpl<TraceDetailsMapper, TraceDetails> implements TraceDetailsManager {
    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public boolean saveBatch(Collection<TraceDetails> entityList, int batchSize) {
        CollectionUtil.split(entityList, batchSize).forEach(item -> {
            getBaseMapper().insertBatchSomeColumn(item);
        });
        return true;
    }
}
