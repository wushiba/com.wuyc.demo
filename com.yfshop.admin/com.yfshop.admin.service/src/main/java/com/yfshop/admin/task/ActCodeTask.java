package com.yfshop.admin.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpUtil;
import com.qiniu.http.Response;
import com.yfshop.admin.dao.ActCodeDao;
import com.yfshop.admin.tool.poster.kernal.qiniu.QiniuConfig;
import com.yfshop.admin.tool.poster.kernal.qiniu.QiniuDownloader;
import com.yfshop.admin.tool.poster.kernal.qiniu.QiniuUploader;
import com.yfshop.code.manager.ActCodeBatchDetailManager;
import com.yfshop.code.manager.ActCodeBatchManager;
import com.yfshop.code.mapper.ActCodeBatchMapper;
import com.yfshop.code.model.ActCodeBatch;
import com.yfshop.code.model.ActCodeBatchDetail;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.DateUtil;
import com.yfshop.common.util.StringUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 生成商户码任务
 */
@Component
@EnableAsync
public class ActCodeTask {

    @Value("${actCode.url}")
    private String actCodeCodeUrl;

    @Value("${actCode.srcDir}")
    private String actCodeCodeSrcDir;

    @Value("${actCode.targetDir}")
    private String actCodeCodeTargetDir;

    @Resource
    private ActCodeDao actCodeDao;

    @Resource
    private ActCodeBatchDetailManager actCodeBatchDetailManager;

    @Resource
    private ActCodeBatchManager actCodeBatchManager;

    @Resource
    private ActCodeBatchMapper actCodeBatchMapper;

    @Autowired
    QiniuUploader qiniuUploader;

    @Autowired
    QiniuDownloader qiniuDownloader;

    @Autowired
    QiniuConfig qiniuConfig;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    private static final Logger logger = LoggerFactory.getLogger(ActCodeTask.class);


    @SneakyThrows
    @Async
    public void buildActCode(ActCodeBatch actCodeBatch, List<String> sourceCodes) {
        Date date = DateUtil.getDate(DateUtil.localDateTimeToDate(actCodeBatch.getCreateTime()));
        List<ActCodeBatchDetail> actCodeBatchDetails = new ArrayList<>();
        List<String> codeFile = new ArrayList<>();
        actCodeBatch.setFileStatus("DONGING");
        actCodeBatchManager.updateById(actCodeBatch);
        logger.info("批从号{},正在生成{}个溯源码", actCodeBatch.getBatchNo(), actCodeBatch.getQuantity());
        logger.info("开始合成溯源码");
        for (String code : sourceCodes) {
            LocalDateTime now = LocalDateTime.now();
            ActCodeBatchDetail actCodeBatchDetail = new ActCodeBatchDetail();
            actCodeBatchDetail.setActCode(DigestUtil.md5HexTo16(SecureUtil.md5(code)));
            actCodeBatchDetail.setTraceNo(code);
            actCodeBatchDetail.setActId(actCodeBatch.getActId());
            actCodeBatchDetail.setBatchId(actCodeBatch.getId());
            actCodeBatchDetail.setCreateTime(now);
            actCodeBatchDetail.setUpdateTime(now);
            actCodeBatchDetails.add(actCodeBatchDetail);
            codeFile.add(String.format("%s,%s%s", code, actCodeCodeUrl, actCodeBatchDetail.getActCode()));
        }
        logger.info("溯源码合成结束");
        actCodeBatchDetailManager.saveBatch(actCodeBatchDetails, 1000);
        String filePath = actCodeCodeTargetDir + actCodeBatch.getBatchNo() + ".txt";
        FileUtil.appendUtf8Lines(codeFile, filePath);
        logger.info("批从号{},{}个溯源码,生成完毕", actCodeBatch.getBatchNo(), actCodeBatch.getQuantity());
        actCodeBatch.setFileStatus("FAIL");
        Response response = qiniuUploader.getUploadManager().put(filePath, actCodeBatch.getBatchNo() + ".txt", qiniuUploader.getAuth().uploadToken(qiniuConfig.getBucket()));
        if (response.isOK()) {
            actCodeBatch.setFileStatus("SUCCESS");
            actCodeBatch.setFileUrl("http://" + qiniuConfig.getDomain() + "/" + actCodeBatch.getBatchNo() + ".txt");
        }
        actCodeBatchManager.updateById(actCodeBatch);
    }

    @Async
    public void downLoadFile(ActCodeBatch actCodeBatch, String md5, String fileUrl) throws ApiException {
        File file = new File(actCodeCodeSrcDir + actCodeBatch.getBatchNo() + ".txt");
        if (!file.exists()) {
            logger.info("正则下载溯源码文件");
            fileUrl = qiniuDownloader.privateDownloadUrl(fileUrl, 60);
            file = HttpUtil.downloadFileFromUrl(fileUrl, file);
            logger.info("载溯源码文件下载完成");
            Asserts.assertEquals(md5, SecureUtil.md5(file), 500, "下载文件md5不匹配");
        }
        FileUtil.getInputStream(file);
        BufferedReader bufferedReader = FileUtil.getUtf8Reader(file);
        List<String> sourceCodes = new ArrayList<>();
        bufferedReader.lines().forEach(item -> {
            Asserts.assertTrue(item.length() == 16, 500, item + "溯源码格式有误！");
            sourceCodes.add(item);
        });
        actCodeBatch.setQuantity(sourceCodes.size());
        actCodeBatchMapper.updateById(actCodeBatch);
        buildActCode(actCodeBatch, sourceCodes);
    }

    @Async
    public void sendAttachmentsMail(String to, String subject, String content, String filePath, String... cc) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        if (ArrayUtil.isNotEmpty(cc)) {
            helper.setCc(cc);
        }
        File file = new File(filePath);
        FileSystemResource fileResource = new FileSystemResource(file);
        helper.addAttachment(file.getName(), fileResource);
        mailSender.send(message);
    }

}
