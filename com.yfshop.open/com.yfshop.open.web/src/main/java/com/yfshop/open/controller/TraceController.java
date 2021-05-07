package com.yfshop.open.controller;

import com.yfshop.common.api.CommonResult;
import com.yfshop.open.api.blpshop.request.OrderReq;
import com.yfshop.open.api.trace.request.StorageReq;
import com.yfshop.open.api.trace.request.TraceReq;
import com.yfshop.open.api.trace.service.TraceService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("open/trace")
public class TraceController {
    private static final Logger logger = LoggerFactory.getLogger(TraceController.class);
    @DubboReference
    TraceService traceService;

    /**
     * 保存溯源码数据
     *
     * @param traceReqs
     * @return
     */
    @RequestMapping(value = "/saveTrace", method = {RequestMethod.POST})
    public CommonResult saveTrace(@RequestBody List<TraceReq> traceReqs) {
        logger.info("saveTrace->{}", traceReqs.toString());
        traceService.syncTrace(traceReqs);
        return CommonResult.success(1, "接收成功");
    }

    /**
     * 保存入库数据
     *
     * @param storageReqs
     * @return
     */
    @RequestMapping(value = "/saveStorage", method = {RequestMethod.POST})
    public CommonResult saveStorage(@RequestBody List<StorageReq> storageReqs) {
        logger.info("saveStorage->{}", storageReqs.toString());
        traceService.syncStorage(storageReqs);
        return CommonResult.success(1, "接收成功");
    }
}
