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
                trace.setTraceNo(data[0].trim());
                trace.setBoxNo(data[1].trim());
                trace.setProductNo(data[2].trim());
                trace.setCreateTime(now);
                destList.add(trace);
            });
            traceManager.saveBatch(destList, 10000);
        }
    }

    private void syncStorage(List<String> storageReqList) {
        if (storageReqList != null) {
            List<TraceDetails> destList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            storageReqList.forEach(item -> {
                String[] data = item.split(",");
                TraceDetails details = new TraceDetails();
                details.setBoxNo(data[0].trim());
                details.setDealerNo(data[1].trim());
                details.setDealerMobile(data[2].trim());
                details.setDealerName(data[3].trim());
                details.setDealerAddress(data[4].trim());
                details.setCreateTime(now);
                destList.add(details);
            });
            traceDetailsManager.saveBatch(destList, 10000);
        }
    }
}
