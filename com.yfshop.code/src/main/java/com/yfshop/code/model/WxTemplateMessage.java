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
 * @since 2021-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_wx_template_message")
public class WxTemplateMessage extends Model<WxTemplateMessage> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    /**
     * 微信模板id
     */
    private String templateId;

    /**
     * 用户openId
     */
    private String openId;

    /**
     * 跳转url
     */
    private String url;

    /**
     * json数据
     */
    private String data;

    /**
     * 成功SUCCESS 失败FAIL
     */
    private String status;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
