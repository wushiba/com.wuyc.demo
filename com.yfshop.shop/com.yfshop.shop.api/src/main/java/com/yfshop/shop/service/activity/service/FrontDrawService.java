package com.yfshop.shop.service.activity.service;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.shop.service.activity.result.YfDrawActivityResult;

/**
 * @Title:活动抽奖Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 19:09:17
 * @Version:1.1.0
 */
public interface FrontDrawService {

	public YfDrawActivityResult getDrawActivityDetailById(Integer id) throws ApiException;

}
