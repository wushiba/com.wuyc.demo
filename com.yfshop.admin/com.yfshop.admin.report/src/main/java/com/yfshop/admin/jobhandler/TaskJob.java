package com.yfshop.admin.jobhandler;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.yfshop.admin.dao.UserCouponDao;
import com.yfshop.admin.task.ActCodeTask;
import com.yfshop.admin.task.WxPushMsgTask;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.wx.api.service.MpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Component
public class TaskJob {
    private static Logger logger = LoggerFactory.getLogger(TaskJob.class);

    @Resource
    private ActCodeBatchMapper actCodeBatchMapper;

    @Resource
    private CouponExpiredConfigMapper couponExpiredConfigMapper;

    @Resource
    private UserCouponDao userCouponDao;

    @Autowired
    private ActCodeTask actCodeTask;

    @Autowired
    private WxPushMsgTask wxPushMsgTask;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private MpService mpService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private VisitLogMapper visitLogMapper;


    @Value("${xxl.job.executor.logpath}")
    String logPath;
    @Value("${shop.url}")
    private String shopUrl;

    @PostConstruct
    public void init() {
        XxlJobFileAppender.initLogPath(logPath);
    }


    @XxlJob("generateActCode")
    public void generateActCode() throws Exception {
        if (!actCodeTask.isFlag()) {
            ActCodeBatch actCodeBatch = actCodeBatchMapper.selectOne(Wrappers.lambdaQuery(ActCodeBatch.class).eq(ActCodeBatch::getFileStatus, "WAIT"));
            if (actCodeBatch != null) {
                actCodeBatch.setFileStatus("DOING");
                actCodeBatchMapper.updateById(actCodeBatch);
                if (actCodeBatch.getType() == 0) {
                    actCodeTask.downLoadFile(actCodeBatch);
                } else {
                    actCodeTask.build(actCodeBatch);
                }
            } else {
                logger.debug("暂无溯源码任务");
            }
        } else {
            logger.debug("当前有溯源码任务正则执行");
        }
    }

    /**
     * 每隔1小时查询快过期的消息
     *
     * @throws Exception
     */
    @XxlJob("expiredCoupon")
    public void expiredCoupon() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now();
        //消息发送时间未8-22点如果这个时间之外进入待发送消息队列
        boolean flag = localDateTime.getHour() >= 8 && localDateTime.getHour() < 22;
        List<CouponExpiredConfig> couponExpiredConfigList = couponExpiredConfigMapper.selectList(Wrappers.emptyWrapper());
        couponExpiredConfigList.forEach(couponExpiredConfig -> {
            List<UserCoupon> userCoupons = userCouponDao.getUserCouponExpired(couponExpiredConfig.getDay());
            if (!CollectionUtils.isEmpty(userCoupons)) {
                if (flag) {
                    userCoupons.forEach(userCoupon -> {
                        sendExpiredCouponMsg(userCoupon);
                    });
                } else {
                    String key = "ExpiredCoupon:" + couponExpiredConfig.getDay();
                    redisTemplate.opsForList().leftPushAll(key, userCoupons);
                    redisTemplate.expire(key, 1, TimeUnit.DAYS);
                }
            }
        });
    }

    /**
     * 每天12点查询未发送的消息
     *
     * @throws Exception
     */
    @XxlJob("expiredCouponOther")
    public void expiredCouponOther() throws Exception {
        List<CouponExpiredConfig> couponExpiredConfigList = couponExpiredConfigMapper.selectList(Wrappers.emptyWrapper());
        couponExpiredConfigList.forEach(couponExpiredConfig -> {
            String key = "ExpiredCoupon:" + couponExpiredConfig.getDay();
            while (true) {
                UserCoupon userCoupon = (UserCoupon) redisTemplate.opsForList().rightPop(key);
                if (userCoupon != null) {
                    sendExpiredCouponMsg(userCoupon);
                } else {
                    break;
                }
            }

        });
    }


    /**
     * 发送卡券即将过期消息
     *
     * @param userCoupon
     */
    public void sendExpiredCouponMsg(UserCoupon userCoupon) {
        User user = userMapper.selectById(userCoupon.getUserId());
        if (user != null) {
            List<WxMpTemplateData> data = new ArrayList<>();
            WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                    .toUser(user.getOpenId())
                    .templateId("")
                    .url(String.format("%s#/CouponList", shopUrl))
                    .data(data)
                    .build();
            mpService.sendWxMpTemplateMsg(wxMpTemplateMessage);
        }
    }


    @XxlJob("deleteExpiredLogs")
    public void deleteExpiredLogs() throws Exception {
        VisitLog visitLog = visitLogMapper.selectOne(Wrappers.lambdaQuery(VisitLog.class).select(VisitLog::getId).apply("create_time >= DATE_SUB(CURRENT_DATE,interval 1 day)"));
        if (visitLog != null && visitLog.getId() != null) {
            visitLogMapper.delete(Wrappers.lambdaQuery(VisitLog.class).le(VisitLog::getId, visitLog.getId()));
        }
    }

    @XxlJob("wxPushTask")
    public void wxPushTask() {

        wxPushMsgTask.doTask();
    }

}
