package com.yfshop.admin.service.log;

import com.yfshop.code.mapper.VisitLogMapper;
import com.yfshop.code.model.VisitLog;
import com.yfshop.common.log.CreateVisitLogReq;
import com.yfshop.common.log.LogService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author Xulg
 * @since 2021-06-15 17:27
 * Description: 日志服务
 */
@DubboService(group = "MerchantLogService")
public class MerchantLogServiceImpl implements LogService {

    @Resource
    private VisitLogMapper visitLogMapper;

    @Override
    public Void createVisitLog(CreateVisitLogReq req) {
        try {
            VisitLog visitLog = new VisitLog();
            visitLog.setCreateTime(LocalDateTime.now());
            visitLog.setUpdateTime(LocalDateTime.now());
            visitLog.setPlatform(req.getPlatform());
            visitLog.setOperatorId(req.getOperatorId());
            visitLog.setInterfaceClass(req.getInterfaceClass());
            visitLog.setRequestUrl(req.getRequestUrl());
            visitLog.setVisitorclientip(req.getVisitorClientIp());
            visitLog.setTimeConsume(req.getTimeConsume());
            visitLog.setParameterContent(req.getParameterContent());
            visitLog.setReturnResult(req.getReturnResult());
            visitLogMapper.insert(visitLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
