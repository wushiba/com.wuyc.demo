package com.yfshop.log;

/**
 * @author Xulg
 * @since 2021-06-15 16:29
 * Description: 日志服务
 */
public interface LogService {

    /**
     * 创建访问日志
     *
     * @param req the req
     * @return void
     */
    Void createVisitLog(CreateVisitLogReq req);

}
