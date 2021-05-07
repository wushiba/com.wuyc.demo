package com.yfshop.open.api.trace.service;

import com.yfshop.open.api.trace.request.StorageReq;
import com.yfshop.open.api.trace.request.TraceReq;

import java.util.List;

public interface TraceService {

    void syncTrace(List<TraceReq> traceReqList);

    void syncStorage(List<StorageReq> traceReqList);

}
