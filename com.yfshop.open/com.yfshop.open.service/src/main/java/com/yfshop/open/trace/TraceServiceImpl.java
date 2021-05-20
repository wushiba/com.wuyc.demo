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

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        if (traceReqList != null) {
            List<Trace> destList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            traceReqList.forEach(item -> {
                Trace trace = BeanUtil.convert(item, Trace.class);
                trace.setCreateTime(now);
                destList.add(trace);
            });
            traceMapper.saveBatch(destList);
        }
    }

    @Override
    @Async
    public void syncStorage(List<StorageReq> storageReqList) {
        if (storageReqList != null) {
            List<TraceDetails> destList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            storageReqList.forEach(item -> {
                TraceDetails details = BeanUtil.convert(item, TraceDetails.class);
                details.setCreateTime(now);
                destList.add(details);
            });
            traceDetailsManager.saveBatch(destList);
        }
    }
}
