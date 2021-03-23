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
 * 验证码
 * </p>
 *
 * @author yoush
 * @since 2021-03-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_captcha")
public class Captcha extends Model<Captcha> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 来源
     */
    private String source;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 短信模板
     */
    private String smsTemplate;

    /**
     * 失效时间
     */
    private LocalDateTime expireTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
