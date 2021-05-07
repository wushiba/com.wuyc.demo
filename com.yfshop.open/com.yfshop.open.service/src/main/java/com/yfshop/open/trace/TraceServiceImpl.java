package com.yfshop.open.trace;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.TraceMapper;
import com.yfshop.code.model.Trace;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.open.api.trace.request.StorageReq;
import com.yfshop.open.api.trace.request.TraceReq;
import com.yfshop.open.api.trace.service.TraceService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@DubboService
public class TraceServiceImpl implements TraceService {
    @Autowired
    private TraceMapper traceMapper;

    @Override
    @Async
    public void syncTrace(List<TraceReq> traceReqList) {
        traceReqList.forEach(item -> {
            try {
                traceMapper.insert(BeanUtil.convert(item, Trace.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    @Async
    public void syncStorage(List<StorageReq> traceReqList) {
        traceReqList.forEach(item -> {
            try {
                traceMapper.update(BeanUtil.convert(item, Trace.class), Wrappers.<Trace>lambdaQuery().eq(Trace::getBoxNo, item.getBoxNo()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
