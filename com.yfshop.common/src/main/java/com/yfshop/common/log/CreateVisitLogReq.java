package com.yfshop.common.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2020-05-26 9:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateVisitLogReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 日志来源平台
     */
    private String platform;

    /**
     * 接口controller
     */
    private String interfaceClass;

    /**
     * 请求接口url
     */
    private String requestUrl;

    /**
     * 客户端ip
     */
    private String visitorClientIp;

    /**
     * 接口执行消耗的毫秒数
     */
    private long timeConsume;

    /**
     * 入参
     */
    private String parameterContent;

    /**
     * 结果
     */
    private String returnResult;
}