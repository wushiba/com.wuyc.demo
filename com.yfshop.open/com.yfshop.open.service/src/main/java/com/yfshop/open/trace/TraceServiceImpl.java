package com.yfshop.open.trace;

import cn.hutool.json.JSONUtil;
import com.yfshop.open.api.trace.request.StorageReq;
import com.yfshop.open.api.trace.request.TraceReq;
import com.yfshop.open.api.trace.service.TraceService;
import com.yfshop.open.task.TraceConsume;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@DubboService
public class TraceServiceImpl implements TraceService {
    @Autowired
    private TraceConsume traceConsume;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    //@Async
    public void syncTrace(String no,List<TraceReq> traceReqList, boolean finish) {
        String key = "Task:Trace:"+no;
        if (!CollectionUtils.isEmpty(traceReqList)) {
            stringRedisTemplate.opsForList().leftPush(key, JSONUtil.toJsonStr(traceReqList));
        }
        if (finish) {
            stringRedisTemplate.expire(key, 1, TimeUnit.HOURS);
            traceConsume.doWork(key);
        }
    }

    @Override
    //@Async
    public void syncStorage(String no,List<StorageReq> storageReqList, boolean finish) {
        String key = "Task:Storage"+no;
        if (!CollectionUtils.isEmpty(storageReqList)) {
            stringRedisTemplate.opsForList().leftPush(key, JSONUtil.toJsonStr(storageReqList));
        }
        if (finish) {
            stringRedisTemplate.expire(key, 1, TimeUnit.HOURS);
            traceConsume.doWork(key);
        }
    }
}
