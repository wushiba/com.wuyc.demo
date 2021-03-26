package com.yfshop.shop.service;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.shop.result.YfActCodeBatchDetailResult;

/**
 * @Title:用户Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-26 17:24:36
 * @Version:1.1.0
 */
public interface ActivityActCodeBatchService {

	/**
	 * 根据用户id获取用户信息
	 * @param actCode
	 * @return 
	 * @Description:
	 */
	public YfActCodeBatchDetailResult getYfActCodeBatchDetailByActCode(String actCode) throws ApiException;

}
