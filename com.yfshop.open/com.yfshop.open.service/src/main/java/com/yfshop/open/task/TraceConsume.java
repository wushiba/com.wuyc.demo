package com.yfshop.open.task;

import cn.hutool.json.JSONUtil;
import com.yfshop.code.manager.TraceDetailsManager;
import com.yfshop.code.manager.TraceManager;
import com.yfshop.code.model.Trace;
import com.yfshop.code.model.TraceDetails;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.open.api.trace.request.StorageReq;
import com.yfshop.open.api.trace.request.TraceReq;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TraceConsume {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TraceManager traceManager;
    @Autowired
    private TraceDetailsManager traceDetailsManager;


    @Async
    public void doWork(String key) {
        while (true) {
            String value = stringRedisTemplate.opsForList().rightPop(key);
            if (StringUtils.isBlank(value)) {
                break;
            }
            if (key.startsWith("Task:Trace")) {
                try {
                    List<TraceReq> codes = JSONUtil.toList(value, TraceReq.class);
                    syncTrace(codes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (key.startsWith("Task:Storage")) {
                try {
                    List<StorageReq> codes = JSONUtil.toList(value, StorageReq.class);
                    syncStorage(codes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void syncTrace(List<TraceReq> traceReqList) {
        if (traceReqList != null) {
            List<Trace> destList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            traceReqList.forEach(item -> {
                Trace trace = BeanUtil.convert(item, Trace.class);
                trace.setCreateTime(now);
                destList.add(trace);
            });
            traceManager.saveBatch(destList);
        }
    }

    private void syncStorage(List<StorageReq> storageReqList) {
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
