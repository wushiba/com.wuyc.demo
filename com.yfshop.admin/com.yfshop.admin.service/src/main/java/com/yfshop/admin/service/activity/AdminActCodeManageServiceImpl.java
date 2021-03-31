package com.yfshop.admin.service.activity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.activity.request.ActCodeQueryReq;
import com.yfshop.admin.api.activity.result.ActCodeResult;
import com.yfshop.admin.api.activity.service.AdminActCodeManageService;
import com.yfshop.admin.dao.ActCodeDao;
import com.yfshop.admin.task.ActCodeTask;
import com.yfshop.admin.tool.poster.kernal.qiniu.QiniuDownloader;
import com.yfshop.code.mapper.ActCodeBatchMapper;
import com.yfshop.code.mapper.ActCodeBatchRecordMapper;
import com.yfshop.code.mapper.SourceFactoryMapper;
import com.yfshop.code.model.ActCodeBatch;
import com.yfshop.code.model.ActCodeBatchRecord;
import com.yfshop.code.model.SourceFactory;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * youshenghui
 * 活动码管理接口
 */
@DubboService
public class AdminActCodeManageServiceImpl implements AdminActCodeManageService {
    @Value("${actCode.dirs}")
    private String actCodeCodeDirs;
    @Resource
    private ActCodeBatchMapper actCodeBatchMapper;

    @Resource
    private ActCodeBatchRecordMapper actCodeBatchRecordMapper;

    @Resource
    private SourceFactoryMapper sourceFactoryMapper;

    @Resource
    private ActCodeDao actCodeDao;

    @Autowired
    private QiniuDownloader qiniuDownloader;

    @Autowired
    private ActCodeTask actCodeTask;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

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
    public Void actCodeImport(Integer actId, String md5, List<String> sourceCodes) throws ApiException {
        Asserts.assertTrue(sourceCodes.size() < 99999999, 500, "溯源码超出当次的最大数量99999999");
        ActCodeBatch actCodeBatch = new ActCodeBatch();
        actCodeBatch.setBatchNo(DateUtil.format(new Date(), "yyMMddHHmmssSSS") + RandomUtil.randomNumbers(4));
        actCodeBatch.setActId(actId);
        actCodeBatch.setFileMd5(md5);
        actCodeBatch.setQuantity(sourceCodes.size());
        actCodeBatchMapper.insert(actCodeBatch);
        actCodeTask.buildActCode(actCodeBatch, sourceCodes);
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
    public String actCodeUrl(Integer merchantId, Integer id) {
        ActCodeBatch actCodeBatch = actCodeBatchMapper.selectById(id);
        Asserts.assertEquals(actCodeBatch.getIsDownload(), 'N', 500, "溯源码文件已被下载过！");
        Asserts.assertStringNotBlank(actCodeBatch.getFileUrl(), 500, "文件不存在！");
        actCodeBatch.setIsDownload("Y");
        actCodeBatchMapper.updateById(actCodeBatch);
        ActCodeBatchRecord actCodeBatchRecord = new ActCodeBatchRecord();
        actCodeBatchRecord.setBatchId(actCodeBatchRecord.getBatchId());
        actCodeBatchRecord.setMerchatId(merchantId);
        actCodeBatchRecord.setType("DOWNLOAD");
        actCodeBatchRecordMapper.insert(actCodeBatchRecord);
        return qiniuDownloader.privateDownloadUrl(actCodeBatch.getFileUrl(), 60);
    }

    @SneakyThrows
    @Override
    public Void sendEmailActCode(Integer merchantId, Integer id, Integer factoryId) {
        ActCodeBatch actCodeBatch = actCodeBatchMapper.selectById(id);
        Asserts.assertEquals(actCodeBatch.getIsSend(), 'N', 500, "溯源码文件已被发送过！");
        Asserts.assertStringNotBlank(actCodeBatch.getFileUrl(), 500, "文件不存在！");
        SourceFactory sourceFactory = sourceFactoryMapper.selectById(factoryId);
        String filePath = actCodeCodeDirs + actCodeBatch.getBatchNo() + ".txt";
        String msg = "您好：\n" +
                "此邮件内含光明活动码（溯源码+抽奖活动码），请妥善保管，切勿外传。\n" +
                "                                                                                                雨帆 ";
        sendAttachmentsMail(sourceFactory.getEmail(), "光明活动码（溯源码+抽奖活动码）", msg, filePath);
        actCodeBatch.setIsSend("Y");
        actCodeBatchMapper.updateById(actCodeBatch);
        ActCodeBatchRecord actCodeBatchRecord = new ActCodeBatchRecord();
        actCodeBatchRecord.setBatchId(actCodeBatchRecord.getBatchId());
        actCodeBatchRecord.setMerchatId(merchantId);
        actCodeBatchRecord.setEmail(sourceFactory.getEmail());
        actCodeBatchRecord.setFactoryName(sourceFactory.getFactoryName());
        actCodeBatchRecord.setAddress(sourceFactory.getAddress());
        actCodeBatchRecord.setContacts(sourceFactory.getContacts());
        actCodeBatchRecord.setMobile(sourceFactory.getMobile());
        actCodeBatchRecord.setType("EMAIL");
        actCodeBatchRecordMapper.insert(actCodeBatchRecord);
        return null;
    }

    private void sendAttachmentsMail(String to, String subject, String content, String filePath, String... cc) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        if (ArrayUtil.isNotEmpty(cc)) {
            helper.setCc(cc);
        }
        FileSystemResource file = new FileSystemResource(new File(filePath));
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
        helper.addAttachment(fileName, file);

        mailSender.send(message);
    }
}