package com.yfshop.admin.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import com.yfshop.admin.tool.poster.kernal.UploadResult;
import com.yfshop.admin.tool.poster.kernal.oss.OssDownloader;
import com.yfshop.admin.tool.poster.kernal.oss.OssUploader;
import com.yfshop.code.manager.ActCodeBatchManager;
import com.yfshop.code.mapper.ActCodeBatchDetailMapper;
import com.yfshop.code.mapper.ActCodeBatchMapper;
import com.yfshop.code.model.ActCodeBatch;
import com.yfshop.code.model.ActCodeBatchDetail;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 生成商户码任务
 */
@Component
public class ActCodeTask {

    @Value("${actCode.url}")
    private String actCodeCodeUrl;

    @Value("${actCode.srcDir}")
    private String actCodeCodeSrcDir;

    @Value("${actCode.targetDir}")
    private String actCodeCodeTargetDir;

    @Resource
    private ActCodeBatchDetailMapper actCodeBatchDetailMapper;

    @Resource
    private ActCodeBatchManager actCodeBatchManager;

    @Resource
    private ActCodeBatchMapper actCodeBatchMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ActCodeConsume actCodeConsumeTask;


    @Autowired
    OssUploader ossUploader;

    @Autowired
    OssDownloader ossDownloader;
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    private static final Logger logger = LoggerFactory.getLogger(ActCodeTask.class);


    @SneakyThrows
    @Async
    public void buildActCode(ActCodeBatch actCodeBatch, List<String> sourceCodes) {
        List<ActCodeBatchDetail> actCodeBatchDetails = new ArrayList<>();
        List<String> codeFile = new ArrayList<>();
        actCodeBatch.setFileStatus("DONGING");
        actCodeBatchManager.updateById(actCodeBatch);
        logger.info("批从号{},正在生成{}个溯源码", actCodeBatch.getBatchNo(), actCodeBatch.getQuantity());
        logger.info("开始合成溯源码");
        for (String code : sourceCodes) {
            LocalDateTime now = LocalDateTime.now();
            ActCodeBatchDetail actCodeBatchDetail = new ActCodeBatchDetail();
            actCodeBatchDetail.setActCode(DigestUtil.md5HexTo16(SecureUtil.md5("yf" + code)));
            actCodeBatchDetail.setTraceNo(code);
            actCodeBatchDetail.setActId(actCodeBatch.getActId());
            actCodeBatchDetail.setBatchId(actCodeBatch.getId());
            actCodeBatchDetail.setCreateTime(now);
            actCodeBatchDetails.add(actCodeBatchDetail);
            codeFile.add(String.format("%s,%s%s", code, actCodeCodeUrl, actCodeBatchDetail.getActCode()));
        }
        logger.info("溯源码合成结束");

        CollectionUtil.split(actCodeBatchDetails, 1000).forEach(item -> {
            actCodeBatchDetailMapper.insertBatchSomeColumn(item);
        });


        String name = actCodeBatch.getBatchNo() + ".txt";
        FileUtil.appendUtf8Lines(codeFile, actCodeCodeTargetDir+name);
        logger.info("批从号{},{}个溯源码,生成完毕", actCodeBatch.getBatchNo(), actCodeBatch.getQuantity());
        actCodeBatch.setFileStatus("FAIL");
        UploadResult response = ossUploader.upload(new File(actCodeCodeTargetDir,name), name);
        if (response.isSuccessful()) {
            actCodeBatch.setFileStatus("SUCCESS");
            actCodeBatch.setFileUrl(response.getUrl());
        }
        actCodeBatchManager.updateById(actCodeBatch);
    }

    @Async
    public void downLoadFile(ActCodeBatch actCodeBatch) throws ApiException {
        try {
            actCodeConsumeTask.setFlag(true);
            File file = new File(actCodeCodeSrcDir + actCodeBatch.getFileMd5() + ".txt");
            String fileUrl;
            if (!file.exists()) {
                logger.info("正则下载溯源码文件");
                fileUrl = ossDownloader.privateDownloadUrl(actCodeBatch.getFileSrcUrl(), 60);
                file = HttpUtil.downloadFileFromUrl(fileUrl, file);
                logger.info("载溯源码文件下载完成");
                Asserts.assertEquals(actCodeBatch.getFileMd5(), SecureUtil.md5(file), 500, "下载文件md5不匹配");
            }
            BufferedReader bufferedReader = FileUtil.getUtf8Reader(file);
            List<String> sourceCodes = new ArrayList<>();
            bufferedReader.lines().forEach(item -> {
                if (StringUtils.isNotBlank(item)) {
                    sourceCodes.add(item);
                }
            });
            String actCodeId = "actCode:" + actCodeBatch.getId();
            CollectionUtil.split(sourceCodes, 10000).forEach(item -> {
                String codes = StringUtils.join(item);
                stringRedisTemplate.opsForList().leftPush(actCodeId, codes);
            });
            stringRedisTemplate.expire(actCodeId, 1, TimeUnit.DAYS);
            actCodeConsumeTask.doWork(actCodeId);
        } catch (Exception e) {
            e.printStackTrace();
            actCodeBatch.setFileStatus("FAIL");
            actCodeBatchMapper.updateById(actCodeBatch);
            actCodeConsumeTask.setFlag(false);
        }
        //buildActCode(actCodeBatch, sourceCodes);
    }

    public boolean isFlag() {
        return actCodeConsumeTask.isFlag();
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

    public static void main(String[] args) {
//        File file = new File("C:\\Users\\Administrator\\Desktop\\huodong\\1.txt");
//        File targetFile = new File("C:\\Users\\Administrator\\Desktop\\huodong\\1(合成).txt");
//        BufferedReader bufferedReader = FileUtil.getUtf8Reader(file);
//        String actCodeCodeUrl = "https://m.yufanlook.com/#/LuckDrawPage?actCode=";
//        List<String> codeFile = new ArrayList<>();
//        bufferedReader.lines().forEach(item -> {
//            if (StringUtils.isNotBlank(item)) {
//                String actCode = DigestUtil.md5HexTo16(SecureUtil.md5("yf" + item));
//                codeFile.add(String.format("%s,%s%s", item, actCodeCodeUrl, actCode));
//            }
//        });
//        FileUtil.appendUtf8Lines(codeFile, targetFile);




        List<String> codes =new ArrayList<>();
        for (int i=0;i<500000;i++){
            codes.add(String.format("%016d",i));
        }
        CollectionUtil.split(codes, 100000).forEach(item -> {
            int i=0;
            File file = new File(String.format("F:\\temp\\%s.txt",++i));
            FileUtil.appendUtf8Lines(item, file);
            String md5=SecureUtil.md5(file)+".txt";
            FileUtil.rename(file,md5,true);
            file.delete();
        });
    }
}
