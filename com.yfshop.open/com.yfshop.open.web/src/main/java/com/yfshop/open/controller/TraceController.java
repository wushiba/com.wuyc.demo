package com.yfshop.open.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.exception.Asserts;
import com.yfshop.open.api.trace.request.StorageReq;
import com.yfshop.open.api.trace.request.TraceReq;
import com.yfshop.open.api.trace.service.TraceService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
     * @return
     */
    @RequestMapping(value = "/importTrace", method = {RequestMethod.POST})
    public CommonResult importTrace(@RequestParam("file") MultipartFile file) throws IOException {
        List<String> list = IoUtil.readUtf8Lines(file.getInputStream(), new ArrayList<>());
        List<TraceReq> traceReqs = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotBlank(item)) {
                String[] data = item.split(",");
                if (data.length == 6) {
                    TraceReq traceReq = new TraceReq();
                    traceReq.setTraceNo(data[0]);
                    traceReq.setBoxNo(data[1]);
                    traceReq.setProductNo(data[2]);
                    traceReqs.add(traceReq);
                    if (traceReqs.size() == 20000) {
                        traceService.syncTrace(traceReqs);
                        traceReqs.clear();
                    }
                }
            }
        });
        if (CollectionUtil.isNotEmpty(traceReqs)) {
            traceService.syncTrace(traceReqs);
        }
        return CommonResult.success(1, "接收成功");
    }

    /**
     * 保存入库数据
     *
     * @return
     */
    @RequestMapping(value = "/importStorage", method = {RequestMethod.POST})
    public CommonResult importStorage(@RequestParam("file") MultipartFile file) throws IOException {
        List<String> list = IoUtil.readUtf8Lines(file.getInputStream(), new ArrayList<>());
        List<StorageReq> storageReqs = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotBlank(item)) {
                String[] data = item.split(",");
                if (data.length == 5) {
                    StorageReq storageReq = new StorageReq();
                    storageReq.setBoxNo(data[0]);
                    storageReq.setDealerNo(data[1]);
                    storageReq.setDealerMobile(data[2]);
                    storageReq.setDealerName(data[3]);
                    storageReq.setDealerAddress(data[4]);
                    storageReqs.add(storageReq);
                    if (storageReqs.size() == 20000) {
                        traceService.syncStorage(storageReqs);
                        storageReqs.clear();
                    }
                }
            }
        });
        if (CollectionUtil.isNotEmpty(storageReqs)) {
            traceService.syncStorage(storageReqs);
        }
        return CommonResult.success(1, "接收成功");
    }
}
