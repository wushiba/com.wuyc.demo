package com.yfshop.admin.service.activity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.activity.request.ActCodeQueryDetailsReq;
import com.yfshop.admin.api.activity.request.ActCodeQueryReq;
import com.yfshop.admin.api.activity.result.ActCodeBatchRecordResult;
import com.yfshop.admin.api.activity.result.ActCodeDetailsResult;
import com.yfshop.admin.api.activity.result.ActCodeResult;
import com.yfshop.admin.api.activity.service.AdminActCodeManageService;
import com.yfshop.admin.dao.ActCodeDao;
import com.yfshop.admin.task.EmailTask;
import com.yfshop.admin.task.OssDownloader;
import com.yfshop.code.mapper.ActCodeBatchDetailMapper;
import com.yfshop.code.mapper.ActCodeBatchMapper;
import com.yfshop.code.mapper.ActCodeBatchRecordMapper;
import com.yfshop.code.mapper.SourceFactoryMapper;
import com.yfshop.code.model.ActCodeBatch;
import com.yfshop.code.model.ActCodeBatchDetail;
import com.yfshop.code.model.ActCodeBatchRecord;
import com.yfshop.code.model.SourceFactory;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * youshenghui
 * 活动码管理接口
 */
@DubboService
public class AdminActCodeManageServiceImpl implements AdminActCodeManageService {
    @Value("${actCode.targetDir}")
    private String actCodeCodeDirs;

    @Resource
    private ActCodeBatchMapper actCodeBatchMapper;

    @Resource
    private ActCodeBatchDetailMapper actCodeBatchDetailMapper;

    @Resource
    private ActCodeBatchRecordMapper actCodeBatchRecordMapper;

    @Resource
    private SourceFactoryMapper sourceFactoryMapper;

    @Resource
    private ActCodeDao actCodeDao;


    @Autowired
    private OssDownloader ossDownloader;

    @Autowired
    private EmailTask emailTask;


    @Override
    public IPage<ActCodeResult> queryActCodeList(ActCodeQueryReq req) {
        IPage<ActCodeResult> iPage = new Page(req.getPageIndex(), req.getPageSize());
        List<ActCodeResult> list = actCodeDao.queryActCodeList(iPage, req);
        iPage.setTotal(actCodeBatchMapper.selectCount(Wrappers.<ActCodeBatch>lambdaQuery()
                .eq(req.getActId() != null, ActCodeBatch::getActId, req.getActId())
                .eq(StringUtils.isNotBlank(req.getBatchNo()), ActCodeBatch::getBatchNo, req.getBatchNo())));
        iPage.setRecords(list);
        return iPage;
    }

    @Override
    public Void actCodeImport(Integer actId, String md5, String fileUrl) throws ApiException {
        checkFile(md5);
        ActCodeBatch actCodeBatch = new ActCodeBatch();
        actCodeBatch.setBatchNo(DateUtil.format(new Date(), "yyMMddHHmmssSSS") + RandomUtil.randomNumbers(4));
        actCodeBatch.setActId(actId);
        actCodeBatch.setFileMd5(md5);
        actCodeBatch.setCreateTime(LocalDateTime.now());
        actCodeBatch.setFileSrcUrl(fileUrl);
        actCodeBatchMapper.insert(actCodeBatch);
        return null;
    }

    @Override
    public Void checkFile(String md5) throws ApiException {
        int count = actCodeBatchMapper.selectCount(Wrappers.<ActCodeBatch>lambdaQuery()
                .eq(ActCodeBatch::getFileMd5, md5));
        Asserts.assertTrue(count == 0, 500, "溯源文件已存在，请勿重复导入！");
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String actCodeUrl(Integer merchantId, Integer id) {
        ActCodeBatch actCodeBatch = actCodeBatchMapper.selectById(id);
        Asserts.assertEquals(actCodeBatch.getIsDownload(), "N", 500, "溯源码文件已被下载过！");
        Asserts.assertStringNotBlank(actCodeBatch.getFileUrl(), 500, "文件不存在！");
        actCodeBatch.setIsDownload("Y");
        actCodeBatchMapper.updateById(actCodeBatch);
        ActCodeBatchRecord actCodeBatchRecord = new ActCodeBatchRecord();
        actCodeBatchRecord.setBatchId(actCodeBatch.getId());
        actCodeBatchRecord.setMerchantId(merchantId);
        actCodeBatchRecord.setType("DOWNLOAD");
        actCodeBatchRecordMapper.insert(actCodeBatchRecord);
        return ossDownloader.privateDownloadUrl(actCodeBatch.getFileUrl(), 60);
    }

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void sendEmailActCode(Integer merchantId, Integer id, Integer factoryId) {
        ActCodeBatch actCodeBatch = actCodeBatchMapper.selectById(id);
        Asserts.assertEquals(actCodeBatch.getIsSend(), "N", 500, "溯源码文件已被发送过！");
        Asserts.assertStringNotBlank(actCodeBatch.getFileUrl(), 500, "文件不存在！");
        SourceFactory sourceFactory = sourceFactoryMapper.selectById(factoryId);
        String filePath = actCodeCodeDirs + actCodeBatch.getBatchNo() + ".txt";
        String msg = "<p>您好!</p>\n" +
                "<p>&nbsp;&nbsp;&nbsp;&nbsp;此邮件内含光明活动码（溯源码+抽奖活动码），请妥善保管，切勿外传。雨帆</p>";
        emailTask.sendAttachmentsMail(sourceFactory.getEmail(), "光明活动码（溯源码+抽奖活动码）", msg, filePath);
        actCodeBatch.setIsSend("Y");
        actCodeBatchMapper.updateById(actCodeBatch);
        ActCodeBatchRecord actCodeBatchRecord = new ActCodeBatchRecord();
        actCodeBatchRecord.setBatchId(actCodeBatch.getId());
        actCodeBatchRecord.setMerchantId(merchantId);
        actCodeBatchRecord.setEmail(sourceFactory.getEmail());
        actCodeBatchRecord.setFactoryName(sourceFactory.getFactoryName());
        actCodeBatchRecord.setAddress(sourceFactory.getAddress());
        actCodeBatchRecord.setContacts(sourceFactory.getContacts());
        actCodeBatchRecord.setMobile(sourceFactory.getMobile());
        actCodeBatchRecord.setType("EMAIL");
        actCodeBatchRecordMapper.insert(actCodeBatchRecord);
        return null;
    }

    @Override
    public IPage<ActCodeDetailsResult> queryActCodeDetails(ActCodeQueryDetailsReq actCodeQueryReq) {
        LambdaQueryWrapper<ActCodeBatchDetail> lambdaQueryWrapper = Wrappers.<ActCodeBatchDetail>lambdaQuery()
                .eq(ActCodeBatchDetail::getBatchId, actCodeQueryReq.getBatchId())
                .eq(StringUtils.isNotBlank(actCodeQueryReq.getActCode()), ActCodeBatchDetail::getActCode, actCodeQueryReq.getActCode())
                .eq(StringUtils.isNotBlank(actCodeQueryReq.getTraceNo()), ActCodeBatchDetail::getTraceNo, actCodeQueryReq.getTraceNo());
        IPage<ActCodeBatchDetail> iPage = actCodeBatchDetailMapper.selectPage(new Page<>(actCodeQueryReq.getPageIndex(), actCodeQueryReq.getPageSize()), lambdaQueryWrapper);
        iPage.setTotal(actCodeBatchDetailMapper.selectCount(lambdaQueryWrapper));
        return BeanUtil.iPageConvert(iPage, ActCodeDetailsResult.class);
    }

    @Override
    public List<ActCodeBatchRecordResult> queryActCodeDownloadRecord(Integer batchId) {
        List<ActCodeBatchRecord> actCodeBatchRecords = actCodeBatchRecordMapper.selectList(Wrappers.<ActCodeBatchRecord>lambdaQuery().eq(ActCodeBatchRecord::getBatchId, batchId));
        return BeanUtil.convertList(actCodeBatchRecords, ActCodeBatchRecordResult.class);
    }
}