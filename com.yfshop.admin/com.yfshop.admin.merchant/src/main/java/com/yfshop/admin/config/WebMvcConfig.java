package com.yfshop.admin.config;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.yfshop.common.config.BaseWebMvcConfig;
import com.yfshop.common.log.CreateVisitLogReq;
import com.yfshop.common.log.LogService;
import com.yfshop.common.log.WebSystemOperateLogAspect;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;

/**
 * WebMVC的配置
 *
 * @author Xulg
 * Created in 2021-03-22 9:44
 */
@Configuration
public class WebMvcConfig extends BaseWebMvcConfig {
    @DubboReference
    LogService logService;

    @Bean
    public WebSystemOperateLogAspect webSystemOperateLogAspect() {
        return new WebSystemOperateLogAspect() {
            @Override
            protected void saveLog(VisitInfo visitInfo) {
                try {
                    visitInfo.setUserId(StpUtil.isLogin() ? StpUtil.getLoginIdAsInt() : null);
                } catch (Exception e) {

                }
                CompletableFuture.runAsync(() -> {
                    CreateVisitLogReq req = new CreateVisitLogReq();
                    req.setInterfaceClass(visitInfo.getMethodInfo());
                    req.setRequestUrl(visitInfo.getRequestUrl());
                    req.setVisitorClientIp(visitInfo.getVisitorClientIp());
                    req.setTimeConsume(visitInfo.getTimeConsume());
                    req.setParameterContent(JSON.toJSONString(visitInfo.getRequestParameter()));
                    req.setReturnResult(JSON.toJSONString(visitInfo.getReturnResult()));
                    req.setOperatorId(visitInfo.getUserId());
                    req.setPlatform("merchant");
                    logService.createVisitLog(req);
                });
            }
        };
    }

}
