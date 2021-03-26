package com.yfshop.shop.service;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.shop.result.YfUserResult;

/**
 * @Title:用户Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-26 17:10:17
 * @Version:1.1.0
 */
public interface ActivityUserService {

	/**
	 * 根据用户id获取用户信息
	 * @param userId
	 * @return 
	 * @Description:
	 */
	public YfUserResult getUserById(Integer userId);

}
