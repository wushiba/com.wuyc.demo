package com.yfshop.code.manager.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.yfshop.code.model.ActCodeBatchDetail;
import com.yfshop.code.mapper.ActCodeBatchDetailMapper;
import com.yfshop.code.manager.ActCodeBatchDetailManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * <p>
 * 活动码详情 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-04-02
 */
@Service
public class ActCodeBatchDetailManagerImpl extends ServiceImpl<ActCodeBatchDetailMapper, ActCodeBatchDetail> implements ActCodeBatchDetailManager {

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public boolean saveBatch(Collection<ActCodeBatchDetail> entityList, int batchSize) {
        CollectionUtil.split(entityList, batchSize).forEach(item -> {
            getBaseMapper().insertBatchSomeColumn(item);
        });
        return true;
    }
}
