package com.yfshop.admin.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.qiniu.http.Response;
import com.yfshop.admin.dao.ActCodeDao;
import com.yfshop.admin.tool.poster.kernal.qiniu.QiniuConfig;
import com.yfshop.admin.tool.poster.kernal.qiniu.QiniuUploader;
import com.yfshop.code.manager.ActCodeBatchDetailManager;
import com.yfshop.code.manager.ActCodeBatchManager;
import com.yfshop.code.model.ActCodeBatch;
import com.yfshop.code.model.ActCodeBatchDetail;
import com.yfshop.common.util.DateUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    @Value("${actCode.dirs}")
    private String actCodeCodeDirs;

    @Resource
    private ActCodeDao actCodeDao;

    @Resource
    private ActCodeBatchDetailManager actCodeBatchDetailManager;

    @Resource
    private ActCodeBatchManager actCodeBatchManager;

    @Autowired
    QiniuUploader qiniuUploader;

    @Autowired
    QiniuConfig qiniuConfig;

    private static final Logger logger = LoggerFactory.getLogger(ActCodeTask.class);


    //活动吗 4位活动id+6位年月日+6位计数+2位crc校验位
    @SneakyThrows
    @Async
    public void buildActCode(ActCodeBatch actCodeBatch, List<String> sourceCodes) {
        AES aes = SecureUtil.aes("yufansop".getBytes(StandardCharsets.UTF_8));
        Date date = DateUtil.getDate(DateUtil.localDateTimeToDate(actCodeBatch.getCreateTime()));
        String dateTime = cn.hutool.core.date.DateUtil.format(date, "yyMMdd");
        List<ActCodeBatchDetail> actCodeBatchDetails = new ArrayList<>();
        List<String> codeFile = new ArrayList<>();
        actCodeBatch.setFileStatus("DONGING");
        actCodeBatchManager.updateById(actCodeBatch);
        logger.info("批从号{},正在生成{}个溯源码", actCodeBatch.getBatchNo(), actCodeBatch.getQuantity());
        AtomicReference<Integer> count = new AtomicReference<>(actCodeDao.sumActCodeByBeforeId(actCodeBatch.getId(), date));
        sourceCodes.stream().forEach(code -> {
            ActCodeBatchDetail actCodeBatchDetail = new ActCodeBatchDetail();
            actCodeBatchDetail.setActCode(String.format("%04d%s%08d", actCodeBatch.getActId(), dateTime, count.getAndSet(count.get() + 1)));
            actCodeBatchDetail.setCipherCode(aes.decryptStr(actCodeBatchDetail.getActCode()));
            actCodeBatchDetail.setTraceNo(code);
            actCodeBatchDetail.setActId(actCodeBatch.getActId());
            actCodeBatchDetail.setBatchId(actCodeBatch.getActId());
            actCodeBatchDetails.add(actCodeBatchDetail);
            codeFile.add(String.format("%s,%s%s", code, actCodeCodeUrl, actCodeBatchDetail.getCipherCode()));
        });
        actCodeBatchDetailManager.saveBatch(actCodeBatchDetails, 200);
        String filePath = actCodeCodeDirs + actCodeBatch.getBatchNo() + ".txt";
        FileUtil.appendUtf8Lines(codeFile, filePath);
        logger.info("批从号{},{}个溯源码,生成完毕", actCodeBatch.getBatchNo(), actCodeBatch.getQuantity());
        actCodeBatch.setFileStatus("FAIL");
        Response response = qiniuUploader.getUploadManager().put(filePath, actCodeBatch.getBatchNo() + ".txt", qiniuUploader.getAuth().uploadToken(qiniuConfig.getBucket()));
        if (response.isOK()) {
            actCodeBatch.setFileStatus("SUCCESS");
            actCodeBatch.setFileUrl("http://"+qiniuConfig.getDomain() + actCodeBatch.getBatchNo() + ".txt");
        }
        actCodeBatchManager.updateById(actCodeBatch);
    }


}
