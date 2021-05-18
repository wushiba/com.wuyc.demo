package com.yfshop.open.trace;

import com.yfshop.code.manager.TraceDetailsManager;
import com.yfshop.code.manager.TraceManager;
import com.yfshop.code.model.Trace;
import com.yfshop.code.model.TraceDetails;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.open.api.trace.request.StorageReq;
import com.yfshop.open.api.trace.request.TraceReq;
import com.yfshop.open.api.trace.service.TraceService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;

import java.util.List;

@DubboService
public class TraceServiceImpl implements TraceService {
    @Autowired
    private TraceManager traceMapper;
    @Autowired
    private TraceDetailsManager traceDetailsManager;

    @Override
    @Async
    public void syncTrace(List<TraceReq> traceReqList) {
        List<Trace> traces = BeanUtil.convertList(traceReqList, Trace.class);
        if (!CollectionUtils.isEmpty(traces)) {
            traceMapper.saveBatch(traces);
        }
    }

    @Override
    @Async
    public void syncStorage(List<StorageReq> traceReqList) {
        List<TraceDetails> traces = BeanUtil.convertList(traceReqList, TraceDetails.class);
        if (!CollectionUtils.isEmpty(traces)) {
            traceDetailsManager.saveBatch(traces);
        }
    }
}
