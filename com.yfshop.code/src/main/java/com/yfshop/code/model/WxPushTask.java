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
@TableName("yf_wx_push_task")
public class WxPushTask extends Model<WxPushTask> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 推送主题
     */
    private String title;

    /**
     * 推送时间
     */
    private LocalDateTime pushTime;

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

    /**
     * CLOSED 已关闭 WAIT 等待 DOING 进行中 SUCCESS 成功 FAIL 失败
     */
    private String status;

    /**
     * CREATE ,EXCEL
     */
    private String source;

    /**
     * 导入的文件
     */
    private String fileUrl;

    /**
     * 模板消息id
     */
    private String templateId;

    /**
     * 模板数据
     */
    private String templateData;

    private String templateUrl;

    private String remark;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
