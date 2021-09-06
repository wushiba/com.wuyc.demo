package com.yfshop.admin.jobhandler;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.yfshop.admin.dao.DrawRecordDao;
import com.yfshop.admin.dao.UserCouponDao;
import com.yfshop.admin.task.ActCodeTask;
import com.yfshop.admin.task.WxPushMsgTask;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.service.RedisService;
import com.yfshop.wx.api.service.MpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    StringRedisTemplate stringRedisTemplate;


    @DubboReference
    private MpService mpService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private VisitLogMapper visitLogMapper;

    @Resource
    private DrawRecordMapper drawRecordMapper;

    @Resource
    private DrawRecordDao drawRecordDao;

    @Resource
    private TraceDrawMapper traceDrawMapper;

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


    @XxlJob("syncTraceData")
    public void syncTraceData() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String value = stringRedisTemplate.opsForValue().get("TraceDataIndex");
        Integer id = 0;
        if (value != null) {
            id = Integer.valueOf(value);
        }
        IPage<DrawRecord> iPage = drawRecordMapper.selectPage(new Page<>(1, 1000), Wrappers.lambdaQuery(DrawRecord.class).select(DrawRecord::getId, DrawRecord::getUpdateTime, DrawRecord::getTraceNo).gt(DrawRecord::getId, id).eq(DrawRecord::getUseStatus, "HAS_USE").isNull(DrawRecord::getDealerName));
        List<DrawRecord> list = iPage.getRecords();
        if (CollectionUtils.isEmpty(iPage.getRecords())) return;
        id = list.get(list.size() - 1).getId();
        stringRedisTemplate.opsForValue().set("TraceDataIndex", id + "");
        list.forEach(item -> {
            if (!item.getTraceNo().startsWith("yf")) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("traceNo", item.getTraceNo());
                String result = HttpUtil.post("http://yf.sma12315.com/ajax/search", paramMap);
                logger.info("traceNo={},result={}", item.getTraceNo(), result);
                try {
                    JSONObject jsonObject = JSONUtil.parseObj(result);
                    Boolean flag = jsonObject.getBool("flag");
                    if (flag != null && true == flag) {
                        String dealerName = jsonObject.getStr("dealer_name");
                        String dealerAddress = jsonObject.getStr("dealer_address");
                        item.setTraceNo(null);
                        item.setDealerAddress(dealerAddress);
                        item.setUpdateTime(item.getUpdateTime().plusSeconds(1));
                        item.setDealerName(dealerName);
                        drawRecordDao.updateTrace(item.getId(), item.getUpdateTime().format(dateTimeFormatter), dealerAddress, dealerName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @XxlJob("syncTraceData")
    public void syncTraceNewData() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String value = stringRedisTemplate.opsForValue().get("syncTraceNewData");
        Long id = 0L;
        if (value != null) {
            id = Long.valueOf(value);
        }
        List<Map<String, String>> list = drawRecordDao.getTractNo(id);
        if (CollectionUtils.isEmpty(list)) return;
        id = Long.valueOf(list.get(list.size() - 1).get("id"));
        stringRedisTemplate.opsForValue().set("syncTraceNewData", id + "");
        list.forEach(item -> {
            String traceNo = item.get("traceNo");
            if (StringUtils.isNotBlank(traceNo) && !traceNo.startsWith("yf")) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("traceNo", traceNo);
                String result = HttpUtil.post("http://yf.sma12315.com/ajax/search", paramMap);
                logger.info("traceNo={},result={}", traceNo, result);
                try {
                    JSONObject jsonObject = JSONUtil.parseObj(result);
                    Boolean flag = jsonObject.getBool("flag");
                    if (flag != null && true == flag) {
                        String dealerNo = jsonObject.getStr("dealer_no");
                        TraceDraw traceDraw = new TraceDraw();
                        traceDraw.setTraceNo(traceNo);
                        traceDraw.setNo(dealerNo);
                        try {
                            traceDrawMapper.insert(traceDraw);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


}
