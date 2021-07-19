package com.yfshop.admin.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.push.WxPushTaskService;
import com.yfshop.admin.api.push.request.WxPushTaskReq;
import com.yfshop.admin.api.push.result.*;
import com.yfshop.admin.dao.WxPushTaskDao;
import com.yfshop.admin.tool.poster.kernal.oss.OssDownloader;
import com.yfshop.code.manager.WxPushTaskDetailManager;
import com.yfshop.code.mapper.WxPushTaskDetailMapper;
import com.yfshop.code.mapper.WxPushTaskMapper;
import com.yfshop.code.mapper.WxPushTemplateMapper;
import com.yfshop.code.model.WxPushTask;
import com.yfshop.code.model.WxPushTaskDetail;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.ExcelUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DubboService
public class AdminWxPushTaskImplService implements WxPushTaskService {
    @Resource
    private WxPushTaskMapper wxPushTaskMapper;

    @Resource
    private WxPushTemplateMapper wxPushTemplateMapper;
    @Resource
    private WxPushTaskDetailMapper wxPushTaskDetailMapper;
    @Resource
    private WxPushTaskDetailManager wxPushTaskDetailManager;
    @Resource
    private WxPushTaskDao wxPushTaskDao;
    @Value("${websiteCode.dir}")
    private String dirPath = "/home/www/web-deploy/yufan-admin-report/websiteCode/";
    @Autowired
    private OssDownloader ossDownloader;
    @Override
    public Void createPushTask(WxPushTaskReq wxPushTaskReq) throws ApiException {
        List<WxPushTaskData> wxPushTaskDataList=new ArrayList<>();
        if ("EXCEL".equals(wxPushTaskReq.getSource())&& HttpUtil.isHttp(wxPushTaskReq.getFileUrl())){
            File dir = new File(dirPath, "excel");
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            File file = new File(dir, "push-"+DateUtil.format(new Date(), "yyMMddHHmmss") + ".xls");
            String fileUrl = ossDownloader.privateDownloadUrl(wxPushTaskReq.getFileUrl(), 60 * 5, null);
            HttpUtil.downloadFileFromUrl(fileUrl, file);
            wxPushTaskDataList=ExcelUtils.importExcel(file.getPath(),1,1,WxPushTaskData.class);
        }else{
            wxPushTaskDataList=wxPushTaskDao.getWxPushTaskData(wxPushTaskReq);
        }
        if (CollectionUtil.isNotEmpty(wxPushTaskDataList)) {
            List<List<WxPushTaskData>> lists = ListUtil.split(wxPushTaskDataList, 100000);
            for (int i = 0; i < lists.size(); i++) {
                WxPushTask wxPushTask = BeanUtil.convert(wxPushTaskReq, WxPushTask.class);
                wxPushTask.setPushTime(wxPushTask.getPushTime().plusHours(i));
                List<WxPushTaskData> list = lists.get(i);
                wxPushTask.setPushCount(list.size());
                Integer pushId = wxPushTaskMapper.insert(wxPushTask);
                List<WxPushTaskDetail> pushTaskDetails = new ArrayList<>();
                list.forEach(date -> {
                    WxPushTaskDetail wxPushTaskDetail = new WxPushTaskDetail();
                    wxPushTaskDetail.setPushId(pushId);
                    wxPushTaskDetail.setUserId(date.getId());
                    wxPushTaskDetail.setOpenId(date.getOpenId());
                    pushTaskDetails.add(wxPushTaskDetail);
                });
                wxPushTaskDetailManager.saveBatch(pushTaskDetails);
            }
        }
        return null;
    }

    @Override
    public Void closePushTask(Integer id) throws ApiException {
        WxPushTask wxPushTask = new WxPushTask();
        wxPushTask.setId(id);
        wxPushTask.setStatus("CLOSED");
        wxPushTaskMapper.updateById(wxPushTask);
        return null;
    }

    @Override
    public Void editPushTask(WxPushTaskReq wxPushTaskReq) throws ApiException {
        WxPushTask wxPushTask= BeanUtil.convert(wxPushTaskReq,WxPushTask.class);
        wxPushTaskMapper.updateById(wxPushTask);
        return null;
    }


    @Override
    public String downloadFile(Integer id) throws ApiException {
        return null;
    }

    @Override
    public Integer filterPushData(WxPushTaskReq wxPushTaskReq) throws ApiException {

        return wxPushTaskDao.getWxPushTaskDataCount(wxPushTaskReq);
    }

    @Override
    public IPage<WxPushTaskResult> pushTaskList(WxPushTaskReq wxPushTaskReq) {
        IPage<WxPushTask> iPage = wxPushTaskMapper.selectPage(new Page<>(wxPushTaskReq.getPageIndex(), wxPushTaskReq.getPageSize()), Wrappers.lambdaQuery(WxPushTask.class).orderByDesc(WxPushTask::getPushTime));
        return BeanUtil.iPageConvert(iPage, WxPushTaskResult.class);
    }

    @Override
    public WxPushTaskStatsResult pushTaskStats() {
        WxPushTaskStatsResult wxPushTaskStatsResult = new WxPushTaskStatsResult();
        List<WxPushTask> wxPushTasks = wxPushTaskMapper.selectList(Wrappers.lambdaQuery(WxPushTask.class).in(WxPushTask::getStatus, "SUCCESS", "FAIL"));
        wxPushTasks.forEach(item -> {
            wxPushTaskStatsResult.setPushCount(wxPushTaskStatsResult.getPushCount() + item.getPushCount());
            wxPushTaskStatsResult.setSuccessCount(wxPushTaskStatsResult.getSuccessCount() + item.getSuccessCount());
            wxPushTaskStatsResult.setFailCount(wxPushTaskStatsResult.getFailCount() + item.getFailCount());
        });
        return wxPushTaskStatsResult;
    }

    @Override
    public List<WxPushFailExportResult> pushFailExport(Integer id) throws ApiException {
        List<WxPushTaskDetail> list = wxPushTaskDetailMapper.selectList(Wrappers.lambdaQuery(WxPushTaskDetail.class).eq(WxPushTaskDetail::getPushId, id).eq(WxPushTaskDetail::getStatus, "FAIL"));
        return BeanUtil.convertList(list, WxPushFailExportResult.class);
    }

    @Override
    public List<WxPushTemplateResult> pushTemplateList() throws ApiException {

        return BeanUtil.convertList(wxPushTemplateMapper.selectList(Wrappers.emptyWrapper()),WxPushTemplateResult.class);
    }

}
