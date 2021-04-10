package com.yfshop.admin.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.qiniu.http.Response;
import com.yfshop.code.manager.ActCodeBatchManager;
import com.yfshop.code.mapper.ActCodeBatchDetailMapper;
import com.yfshop.code.mapper.ActCodeBatchMapper;
import com.yfshop.code.model.ActCodeBatch;
import com.yfshop.code.model.ActCodeBatchDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ActCodeConsume {
    @Resource
    private ActCodeBatchMapper actCodeBatchMapper;
    @Resource
    private ActCodeBatchDetailMapper actCodeBatchDetailMapper;
    @Value("${actCode.url}")
    private String actCodeCodeUrl;

    @Value("${actCode.srcDir}")
    private String actCodeCodeSrcDir;

    @Value("${actCode.targetDir}")
    private String actCodeCodeTargetDir;

    private static final Logger logger = LoggerFactory.getLogger(ActCodeConsume.class);

    public void getMessage(String message) {
        String[] data = message.split("-");
        Integer id = Integer.valueOf(data[0]);
        List<String> codes = Arrays.asList(data[1].split(","));
        doTask(id, codes);
    }

    public void doTask(Integer id, List<String> codes) {
        ActCodeBatch actCodeBatch = actCodeBatchMapper.selectById(id);
        List<ActCodeBatchDetail> actCodeBatchDetails = new ArrayList<>();
        List<String> codeFile = new ArrayList<>();
        actCodeBatch.setFileStatus("DONGING");
        actCodeBatchMapper.updateById(actCodeBatch);
        logger.info("批从号{},正在生成{}个溯源码", actCodeBatch.getBatchNo(), actCodeBatch.getQuantity());
        logger.info("开始合成溯源码");
        for (String code : codes) {
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
        try {
            actCodeBatchDetailMapper.insertBatchSomeColumn(actCodeBatchDetails);
            Integer count = actCodeBatch.getQuantity() == null ? 0 : actCodeBatch.getQuantity();
            actCodeBatch.setQuantity(count + codes.size());
            String filePath = actCodeCodeTargetDir + actCodeBatch.getBatchNo() + ".txt";
            FileUtil.appendUtf8Lines(codeFile, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            String filePath = actCodeCodeTargetDir + actCodeBatch.getBatchNo() + "-fail.txt";
            FileUtil.appendUtf8Lines(codes, filePath);
        }

    }

}