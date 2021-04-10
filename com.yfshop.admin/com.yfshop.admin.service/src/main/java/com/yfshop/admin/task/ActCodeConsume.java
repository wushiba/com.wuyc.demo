package com.yfshop.admin.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonArray;
import com.qiniu.http.Response;
import com.yfshop.admin.tool.poster.kernal.qiniu.QiniuConfig;
import com.yfshop.admin.tool.poster.kernal.qiniu.QiniuUploader;
import com.yfshop.code.mapper.ActCodeBatchDetailMapper;
import com.yfshop.code.mapper.ActCodeBatchMapper;
import com.yfshop.code.model.ActCodeBatch;
import com.yfshop.code.model.ActCodeBatchDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
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
    @Autowired
    QiniuConfig qiniuConfig;
    @Value("${actCode.targetDir}")
    private String actCodeCodeTargetDir;

    @Autowired
    QiniuUploader qiniuUploader;

    private static final Logger logger = LoggerFactory.getLogger(ActCodeConsume.class);

    public void getMessage(String message) {
        try {
            String[] data = message.split("-");
            Integer id = Integer.valueOf(data[0]);
            List<String> codes = JSONUtil.toList(data[1],String.class);
            doTask(id, codes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finish(String id) {
        try {
            ActCodeBatch actCodeBatch = actCodeBatchMapper.selectById(Integer.valueOf(id));
            actCodeBatch.setFileStatus("FAIL");
            actCodeBatch.setFileStatus("");
            String filePath = actCodeCodeTargetDir + actCodeBatch.getBatchNo() + ".txt";
            if (new File(filePath).exists()) {
                try {
                    Response response = qiniuUploader.getUploadManager().put(filePath, actCodeBatch.getBatchNo() + ".txt", qiniuUploader.getAuth().uploadToken(qiniuConfig.getBucket()));
                    if (response.isOK()) {
                        actCodeBatch.setFileStatus("SUCCESS");
                        actCodeBatch.setFileUrl("http://" + qiniuConfig.getDomain() + "/" + actCodeBatch.getBatchNo() + ".txt");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            actCodeBatchMapper.updateById(actCodeBatch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doTask(Integer id, List<String> codes) {
        ActCodeBatch actCodeBatch = actCodeBatchMapper.selectById(id);
        List<ActCodeBatchDetail> actCodeBatchDetails = new ArrayList<>();
        List<String> codeFile = new ArrayList<>();
        actCodeBatch.setFileStatus("DONGING");
        actCodeBatchMapper.updateById(actCodeBatch);
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
            actCodeBatchMapper.updateById(actCodeBatch);
        } catch (Exception e) {
            e.printStackTrace();
            String filePath = actCodeCodeTargetDir + actCodeBatch.getBatchNo() + "-fail.txt";
            FileUtil.appendUtf8Lines(codes, filePath);
        }

    }

}
