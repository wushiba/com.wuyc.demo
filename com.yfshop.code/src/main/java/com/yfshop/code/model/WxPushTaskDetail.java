package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author yoush
 * @since 2021-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_wx_push_task_detail")
public class WxPushTaskDetail extends Model<WxPushTaskDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    /**
     * 推送时间
     */
    private LocalDateTime pushTime;

    /**
     * 推送任务id
     */
    private Integer pushId;

    /**
     * WAIT 等待  SUCCESS 成功 FAIL 失败
     */
    private String status;

    private Integer userId;

    private String openId;

    private String errorMsg;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
