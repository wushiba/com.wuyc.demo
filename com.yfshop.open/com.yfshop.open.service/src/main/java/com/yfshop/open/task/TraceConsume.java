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
                    List<String> codes = JSONUtil.toList(value, String.class);
                    syncTrace(codes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (key.startsWith("Task:Storage")) {
                try {
                    List<String> codes = JSONUtil.toList(value, String.class);
                    syncStorage(codes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void syncTrace(List<String> traceReqList) {
        if (traceReqList != null) {
            List<Trace> destList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            traceReqList.forEach(item -> {
                String[] data = item.split(",");
                Trace trace = new Trace();
                trace.setTraceNo(data[0]);
                trace.setBoxNo(data[1]);
                trace.setProductNo(data[2]);
                trace.setCreateTime(now);
                destList.add(trace);
            });
            traceManager.saveBatch(destList, 30000);
        }
    }

    private void syncStorage(List<String> storageReqList) {
        if (storageReqList != null) {
            List<TraceDetails> destList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            storageReqList.forEach(item -> {
                TraceDetails details = new TraceDetails();
                String[] data = item.split(",");
                StorageReq storageReq = new StorageReq();
                storageReq.setBoxNo(data[0]);
                storageReq.setDealerNo(data[1]);
                storageReq.setDealerMobile(data[2]);
                storageReq.setDealerName(data[3]);
                storageReq.setDealerAddress(data[4]);
                details.setCreateTime(now);
                destList.add(details);
            });
            traceDetailsManager.saveBatch(destList, 30000);
        }
    }


}
