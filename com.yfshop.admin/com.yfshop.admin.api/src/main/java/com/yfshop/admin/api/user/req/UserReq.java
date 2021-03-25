package com.yfshop.admin.api.user.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
public class UserReq implements Serializable {

    private static final long serialVersionUID = 1L;


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


}
