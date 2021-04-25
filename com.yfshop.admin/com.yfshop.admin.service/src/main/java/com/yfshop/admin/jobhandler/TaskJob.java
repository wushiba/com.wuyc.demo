package com.yfshop.admin.jobhandler;

import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.yfshop.admin.task.OrderTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class TaskJob {
    private static Logger logger = LoggerFactory.getLogger(TaskJob.class);
    @Value("${xxl.job.executor.logpath}")
    String logPath;
    @Autowired
    OrderTask orderTask;

    @PostConstruct
    public void init() {
        XxlJobFileAppender.initLogPath(logPath);
    }


    @XxlJob("syncWebsiteCodeOrder")
    public void syncWebsiteCodeOrder() throws Exception {
        orderTask.syncWebsiteCodeOrder();
    }

    @XxlJob("syncShopOrder")
    public void syncShopOrder() throws Exception {
        orderTask.syncShopOrder();
    }
}
