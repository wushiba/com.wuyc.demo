package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author yoush
 * @since 2021-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_visit_log")
public class VisitLog extends Model<VisitLog> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer operatorId;

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
    @TableField("visitorClientIp")
    private String visitorclientip;

    /**
     * 接口执行消耗的毫秒数
     */
    private Long timeConsume;

    /**
     * 入参
     */
    private String parameterContent;

    /**
     * 结果
     */
    private String returnResult;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
