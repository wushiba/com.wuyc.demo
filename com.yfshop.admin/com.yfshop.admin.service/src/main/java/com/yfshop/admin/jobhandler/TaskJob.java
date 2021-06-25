package com.yfshop.admin.jobhandler;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.yfshop.admin.task.OrderTask;
import com.yfshop.code.mapper.ItemMapper;
import com.yfshop.code.model.Item;
import com.yfshop.common.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;


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

    @XxlJob("buyGoods")
    public void buyGoods() {
        List<Item> items = itemMapper.selectList(Wrappers.lambdaQuery(Item.class).eq(Item::getIsEnable, "Y").eq(Item::getIsDelete, "N"));
        items.forEach(item -> {
            redisService.incr("BuyGoods:" + item.getId(), RandomUtil.randomInt(1, 10));
        });

    }
}
