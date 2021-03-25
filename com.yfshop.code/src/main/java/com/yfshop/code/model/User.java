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
 * 用户表
 * </p>
 *
 * @author yoush
 * @since 2021-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_user")
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 微信openId
     */
    private String openId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户绑定手机号
     */
    private String mobile;

    /**
     * 用户头像
     */
    private String headImgUrl;

    /**
     * 性别 0:未知 1:男 2:女 
     */
    private Integer sex;

    /**
     * N 未关注 Y 已关注
     */
    private String subscribe;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
