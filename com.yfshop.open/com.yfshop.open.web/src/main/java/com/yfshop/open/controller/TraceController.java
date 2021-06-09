package com.yfshop.open.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
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
        String no= DateUtil.format(new Date(),"yyMMddHH");
        List<String> list = IoUtil.readUtf8Lines(file.getInputStream(), new ArrayList<>());
        List<String> traceReqs = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotBlank(item)) {
                String[] data = item.split(",");
                if (data.length == 6) {
                    traceReqs.add(item);
                    if (traceReqs.size() == 10000) {
                        traceService.syncTrace(no,traceReqs, false);
                        traceReqs.clear();
                    }
                }
            }
        });
        traceService.syncTrace(no,traceReqs, true);
        return CommonResult.success(1, "接收成功");
    }

    /**
     * 保存入库数据
     *
     * @return
     */
    @RequestMapping(value = "/importStorage", method = {RequestMethod.POST})
    public CommonResult importStorage(@RequestParam("file") MultipartFile file) throws IOException {
        String no= DateUtil.format(new Date(),"yyMMddHH");
        List<String> list = IoUtil.readUtf8Lines(file.getInputStream(), new ArrayList<>());
        List<String> storageReqs = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotBlank(item)) {
                String[] data = item.split(",");
                if (data.length == 5) {
                    storageReqs.add(item);
                    if (storageReqs.size() == 10000) {
                        traceService.syncStorage(no,storageReqs, false);
                        storageReqs.clear();
                    }
                }
            }
        });
        traceService.syncStorage(no,storageReqs, true);
        return CommonResult.success(1, "接收成功");
    }
}
