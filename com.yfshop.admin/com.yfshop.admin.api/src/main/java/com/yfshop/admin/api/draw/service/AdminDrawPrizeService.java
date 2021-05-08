package com.yfshop.admin.api.draw.service;

import java.util.List;

import com.yfshop.admin.api.draw.request.QueryDrawPrizeReq;
import com.yfshop.admin.api.draw.result.DrawPrizeResult;
import com.yfshop.common.exception.ApiException;

/**
 * @Title:抽奖活动奖品Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 11:14:43
 * @Version:1.1.0
 */
public interface AdminDrawPrizeService {

	/**
	 * 通过id得到抽奖活动奖品YfDrawPrize
	 * @param id
	 * @return 
	 * @Description:
	 */
	public DrawPrizeResult getYfDrawPrizeById(Integer id) throws ApiException;

	/**
	 * 得到所有抽奖活动奖品YfDrawPrize
	 * @param req
	 * @return 
	 * @Description:
	 */
	public List<DrawPrizeResult> getAll(QueryDrawPrizeReq req) throws ApiException;

}
