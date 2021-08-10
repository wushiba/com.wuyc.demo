package com.yfshop.admin.jobhandler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.yfshop.admin.api.spread.AdminSpreadService;
import com.yfshop.admin.task.OrderTask;
import com.yfshop.code.mapper.HealthyItemMapper;
import com.yfshop.code.mapper.ItemMapper;
import com.yfshop.code.model.HealthyItem;
import com.yfshop.code.model.Item;
import com.yfshop.common.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Component
public class TaskJob {
    private static Logger logger = LoggerFactory.getLogger(TaskJob.class);
    @Value("${xxl.job.executor.logpath}")
    String logPath;
    @Autowired
    OrderTask orderTask;
    @Autowired
    RedisService redisService;
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private HealthyItemMapper healthyItemMapper;
    @Resource
    private AdminSpreadService adminSpreadService;

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

    @XxlJob("syncShopTimeOutOrder")
    public void syncShopTimeOutOrder() throws Exception {
        orderTask.syncShopTimeOutOrder();
    }


    @XxlJob("syncJdOrder")
    public void syncJdOrder() throws Exception {
        try {
            adminSpreadService.doOrderTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
