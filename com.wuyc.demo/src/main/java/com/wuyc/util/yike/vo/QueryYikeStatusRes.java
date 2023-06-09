package com.wuyc.util.yike.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询亿客状态返回值
 *
 * @author sp0313
 * @date 2023年05月31日 13:55:00
 */
@Data
public class QueryYikeStatusRes implements Serializable {
    private static final long serialVersionUID = -8263897572607419526L;

    /**
     * 超A唯一id
     */
    private String outId;

    /**
     * 绑定状态：0绑定中、1已绑定、2绑定失败、3已解绑、4已变更
     */
    private String bindStatus;

    /**
     * 绑定时间：2023-05-25 01:12:30
     */
    private String bindTime;

    /**
     * 解绑时间 2023-05-25 01:12:30
     */
    private String unbindTime;

    /**
     * 是否试驾：0否、1是
     */
    private String driveStatus;

    /**
     * 试驾时间 2023-05-25 01:12:30
     */
    private String driveTime;

    /**
     * 是否下定：0否、1是
     */
    private String orderStatus;

    /**
     * 订车时间 2023-05-25 01:12:30
     */
    private String orderTime;

    /**
     * 是否交车：0否、1是
     */
    private String pickStatus;

    /**
     * 交车时间 2023-05-25 01:12:30
     */
    private String pickTime;

    /**
     * 解绑原因
     */
    private String unbindReason;

    /**
     * 绑定失败原因
     */
    private String bindFailReason;

    /**
     * 变更原因
     */
    private String changeReason;
}
