package com.yfshop.admin.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.reflect.TypeToken;
import com.yfshop.admin.utils.ProxyUtil;
import com.yfshop.code.manager.ActCodeBatchManager;
import com.yfshop.code.manager.WxPushTaskDetailManager;
import com.yfshop.code.mapper.ActCodeBatchMapper;
import com.yfshop.code.mapper.WxPushTaskDetailMapper;
import com.yfshop.code.mapper.WxPushTaskMapper;
import com.yfshop.code.model.WxPushTask;
import com.yfshop.code.model.WxPushTaskDetail;
import com.yfshop.wx.api.service.MpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 生成商户码任务
 */
@Component
public class WxPushMsgTask {

    private static final Logger logger = LoggerFactory.getLogger(WxPushMsgTask.class);
    @Resource
    private WxPushTaskMapper wxPushTaskMapper;

    @Resource
    private WxPushTaskDetailMapper wxPushTaskDetailMapper;

    @Resource
    private WxPushTaskDetailManager wxPushTaskDetailManager;

    @DubboReference
    private MpService mpService;

    @Async
    public void doTask(){
        WxPushTask wxPushTask= wxPushTaskMapper.selectOne(Wrappers.lambdaQuery(WxPushTask.class).le(WxPushTask::getPushTime,LocalDateTime.now()).eq(WxPushTask::getStatus,"WAIT").orderByDesc(WxPushTask::getPushTime));
        if (wxPushTask!=null){
            wxPushTask.setStatus("DOING");
            wxPushTaskMapper.updateById(wxPushTask);
            sendWxMsg(wxPushTask);
        }

    }

    private void sendWxMsg(WxPushTask wxPushTask){
        Integer failCount=0;
        Integer successCount=0;
        List<WxPushTaskDetail> wxPushTaskDetails= wxPushTaskDetailMapper.selectList(Wrappers.lambdaQuery(WxPushTaskDetail.class)
                .eq(WxPushTaskDetail::getPushId,wxPushTask.getId())
                .eq(WxPushTaskDetail::getStatus,"WAIT"));
        List<WxMpTemplateData> templateData = WxMpGsonBuilder.create().fromJson(wxPushTask.getTemplateData(), new TypeToken<List<WxMpTemplateData>>() {
        }.getType());
        for (WxPushTaskDetail data:wxPushTaskDetails){
            try {
                WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                        .data(templateData)
                        .toUser(data.getOpenId())
                        .url(wxPushTask.getTemplateUrl())
                        .templateId(wxPushTask.getTemplateId())
                        .build();
                data.setPushTime(LocalDateTime.now());
                mpService.sendWxMsg(wxMpTemplateMessage);
                data.setStatus("SUCCESS");
                successCount++;
            }catch (Exception e){
                data.setStatus("FAIL");
                data.setErrorMsg(e.getMessage());
                failCount++;
            }
        }
        wxPushTask.setFailCount(failCount);
        wxPushTask.setSuccessCount(successCount);
        wxPushTask.setStatus("SUCCESS");
        wxPushTaskMapper.updateById(wxPushTask);
        wxPushTaskDetailManager.saveOrUpdateBatch(wxPushTaskDetails);

    }



}
