package com.yfshop.admin.api.push.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class WxPushTaskStatsResult implements Serializable {

    /**
     * 推送人数
     */
    private Integer pushCount;

    /**
     * 成功人数
     */
    private Integer successCount;

    /**
     * 失败人数
     */
    private Integer failCount;


}


