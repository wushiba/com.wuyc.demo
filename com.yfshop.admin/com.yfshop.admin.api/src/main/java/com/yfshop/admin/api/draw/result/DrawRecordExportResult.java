package com.yfshop.admin.api.draw.result;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yfshop.common.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 抽奖活动奖品表
 * </p>
 *
 * @author yoush
 * @since 2021-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DrawRecordExportResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动标题
     */
    @Excel(name = "活动标题", width = 18)
    private String actTitle;

    /**
     * 创建时间
     */
    @Excel(name = "抽奖时间", width = 18)
    private LocalDateTime createTime;


    /**
     * 奖品等级, 1 (一等奖), 2(二等奖) 3(三等奖)
     */
    @Excel(name = "奖品等级", width = 18, replace = {"一等奖_1", "二等奖_2", "三等奖_3"})
    private Integer prizeLevel;

    /**
     * 奖品标题
     */
    @Excel(name = "奖品", width = 18)
    private String prizeTitle;

    /**
     * 用户id
     */
    @Excel(name = "用户id", width = 18)
    private Integer userId;


    /**
     * 用户姓名
     */
    @Excel(name = "用户名称", width = 18)
    private String userName;

    /**
     * 用户手机号
     */
    @Excel(name = "用户手机号", width = 18)
    private String userMobile;

    /**
     * 用户归属地
     */
    @Excel(name = "用户归属地", width = 18)
    private String userLocation;

    /**
     * 使用状态
     */
    @Excel(name = "使用状态", width = 18)
    private String useStatus;

    /**
     * 规格
     */
    @Excel(name = "规格", width = 18)
    private String spec;

    /**
     * 活动码
     */
    @Excel(name = "活动码", width = 18)
    private String actCode;

    /**
     * 溯源码
     */
    @Excel(name = "溯源码", width = 18)
    private String traceNo;

    /**
     * 经销商姓名
     */
    @Excel(name = "经销商姓名", width = 18)
    private String dealerName;

    /**
     * 经销商地址
     */
    @Excel(name = "经销商地址", width = 18)
    private String dealerAddress;


    public String getCreateTime() {
        return createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getUseStatus() {
        String use = "";
        switch (useStatus) {
            case "NO_USE":
                use = "未使用";
                break;
            case "IN_USE":
                use = "使用中";
                break;
            case "HAS_USE":
                use = "已使用";
                break;
        }
        return use;
    }

}
