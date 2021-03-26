package com.yfshop.shop.result;

import lombok.Data;
import java.io.Serializable;

/**
 * @Title:用户
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-26 17:10:17
 * @Version:1.1.0
 * @Copyright:Copyright 
 */
@Data
public class YfUserResult implements Serializable{

	private static final long serialVersionUID = -1L;
	
    /**  */
    private Integer id;

    /** 微信openId */
    private String openId;

    /** 用户昵称 */
    private String nickname;

    /**  用户绑定手机号 */
    private String mobile;

    /** 用户头像 */
    private String headImgUrl;

    /** 性别 0:未知 1:男 2:女 */
    private Integer sex;

    /**  N 未关注 Y 已关注 */
    private String subscribe;
	
}
