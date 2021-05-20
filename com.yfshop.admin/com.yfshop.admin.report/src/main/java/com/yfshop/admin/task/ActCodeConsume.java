package com.yfshop.admin.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.yfshop.admin.tool.poster.kernal.UploadResult;
import com.yfshop.admin.tool.poster.kernal.oss.OssConfig;
import com.yfshop.admin.tool.poster.kernal.oss.OssUploader;
import com.yfshop.code.mapper.ActCodeBatchDetailMapper;
import com.yfshop.code.mapper.ActCodeBatchMapper;
import com.yfshop.code.model.ActCodeBatch;
import com.yfshop.code.model.ActCodeBatchDetail;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    OssConfig ossConfig;
    @Value("${actCode.targetDir}")
    private String actCodeCodeTargetDir;
    @Autowired
    OssUploader ossUploader;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private boolean flag = false;

    private static final Logger logger = LoggerFactory.getLogger(ActCodeConsume.class);

    @Async
    public void doWork(String key) {
        flag = true;
        while (true) {
            String value = stringRedisTemplate.opsForList().rightPop(key);
            String[] data = key.split(":");
            Integer id = Integer.valueOf(data[1]);
            if (StringUtils.isBlank(value)) {
                finish(id);
                break;
            }
            try {
                List<String> codes = JSONUtil.toList(value, String.class);
                doTask(id, codes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        flag = false;
    }

    private void finish(Integer id) {
        ActCodeBatch actCodeBatch = actCodeBatchMapper.selectById(id);
        try {
            actCodeBatch.setFileStatus("FAIL");
            String fileName = String.format("%s.txt", actCodeBatch.getBatchNo());
            String targetFileName = String.format("%s(%sml).txt", actCodeBatch.getBatchNo(), actCodeBatch.getSpec());
            String filePath = actCodeCodeTargetDir + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                try {
                    UploadResult response = ossUploader.upload(file, targetFileName);
                    if (response.isSuccessful()) {
                        actCodeBatch.setFileStatus("SUCCESS");
                        actCodeBatch.setFileUrl(response.getUrl());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            actCodeBatchMapper.updateById(actCodeBatch);
        } catch (Exception e) {
            actCodeBatch.setFileStatus("FAIL");
            actCodeBatchMapper.updateById(actCodeBatch);
            e.printStackTrace();
        } finally {
            flag = false;
        }
    }

    private void doTask(Integer id, List<String> codes) {
        ActCodeBatch actCodeBatch = actCodeBatchMapper.selectById(id);
        List<ActCodeBatchDetail> actCodeBatchDetails = new ArrayList<>();
        List<String> codeFile = new ArrayList<>();
        actCodeBatch.setFileStatus("DOING");
        actCodeBatchMapper.updateById(actCodeBatch);
        logger.info("开始合成溯源码");
        if (actCodeBatch.getType() == 0) {
            for (String code : codes) {
                LocalDateTime now = LocalDateTime.now();
                ActCodeBatchDetail actCodeBatchDetail = new ActCodeBatchDetail();
                actCodeBatchDetail.setActCode(DigestUtil.md5HexTo16(SecureUtil.md5("yf" + code)));
                actCodeBatchDetail.setTraceNo(code);
                actCodeBatchDetail.setSpec(actCodeBatch.getSpec());
                actCodeBatchDetail.setActId(actCodeBatch.getActId());
                actCodeBatchDetail.setBatchId(actCodeBatch.getId());
                actCodeBatchDetail.setCreateTime(now);
                actCodeBatchDetails.add(actCodeBatchDetail);
                codeFile.add(String.format("%s,%s%s", code, actCodeCodeUrl, actCodeBatchDetail.getActCode()));
            }
        } else {
            for (String code : codes) {
                LocalDateTime now = LocalDateTime.now();
                ActCodeBatchDetail actCodeBatchDetail = new ActCodeBatchDetail();
                actCodeBatchDetail.setActCode(DigestUtil.md5HexTo16(SecureUtil.md5("yf" + code)));
                actCodeBatchDetail.setTraceNo(code);
                actCodeBatchDetail.setSpec(actCodeBatch.getSpec());
                actCodeBatchDetail.setActId(actCodeBatch.getActId());
                actCodeBatchDetail.setBatchId(actCodeBatch.getId());
                actCodeBatchDetail.setCreateTime(now);
                actCodeBatchDetails.add(actCodeBatchDetail);
                codeFile.add(String.format("%s%s", actCodeCodeUrl, actCodeBatchDetail.getActCode()));
            }
        }
        logger.info("溯源码合成结束");
        try {
            actCodeBatchDetailMapper.insertBatchSomeColumn(actCodeBatchDetails);
            if (actCodeBatch.getType() == 0) {
                Integer count = actCodeBatch.getQuantity() == null ? 0 : actCodeBatch.getQuantity();
                actCodeBatch.setQuantity(count + codes.size());
                actCodeBatchMapper.updateById(actCodeBatch);
            }
            String filePath = actCodeCodeTargetDir + actCodeBatch.getBatchNo() + ".txt";
            FileUtil.appendUtf8Lines(codeFile, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            String filePath = actCodeCodeTargetDir + actCodeBatch.getBatchNo() + "-fail.txt";
            FileUtil.appendUtf8Lines(codes, filePath);
        }

    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @PreDestroy
    public void onDestroy() {
        flag = false;
    }

}
