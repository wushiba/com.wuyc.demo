package com.yfshop.open.api.trace.service;

import com.yfshop.open.api.trace.request.StorageReq;
import com.yfshop.open.api.trace.request.TraceReq;

import java.util.List;

public interface TraceService {

    void syncTrace(String no,List<TraceReq> traceReqList,boolean finish);

    void syncStorage(String no,List<StorageReq> traceReqList,boolean finish);

}
