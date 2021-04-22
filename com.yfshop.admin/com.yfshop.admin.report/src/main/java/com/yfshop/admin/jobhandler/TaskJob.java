package com.yfshop.admin.jobhandler;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.yfshop.admin.task.ActCodeTask;
import com.yfshop.code.mapper.ActCodeBatchMapper;
import com.yfshop.code.model.ActCodeBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Component
public class TaskJob {
    private static Logger logger = LoggerFactory.getLogger(TaskJob.class);

    @Resource
    private ActCodeBatchMapper actCodeBatchMapper;

    @Autowired
    private ActCodeTask actCodeTask;

    @Value("${xxl.job.executor.logpath}")
    String logPath;

    @PostConstruct
    public void init(){
        XxlJobFileAppender.initLogPath(logPath);
    }


    @XxlJob("generateActCode")
    public void shardingJobHandler() throws Exception {
        if (!actCodeTask.isFlag()) {
            ActCodeBatch actCodeBatch = actCodeBatchMapper.selectOne(Wrappers.lambdaQuery(ActCodeBatch.class).eq(ActCodeBatch::getFileStatus, "WAIT"));
            if (actCodeBatch != null) {
                actCodeBatchMapper.updateById(actCodeBatch);
                actCodeTask.downLoadFile(actCodeBatch);
            }else{
                logger.debug("暂无溯源码任务");
            }
        }else{
            logger.debug("当前有溯源码任务正则执行");
        }
    }
}
