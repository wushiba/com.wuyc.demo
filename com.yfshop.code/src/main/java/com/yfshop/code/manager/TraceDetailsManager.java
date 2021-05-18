package com.yfshop.code.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.yfshop.code.model.ActCodeBatchDetail;
import com.yfshop.code.model.Trace;
import com.yfshop.code.model.TraceDetails;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author yoush
 * @since 2021-05-18
 */
public interface TraceDetailsManager extends IService<TraceDetails> {

}
