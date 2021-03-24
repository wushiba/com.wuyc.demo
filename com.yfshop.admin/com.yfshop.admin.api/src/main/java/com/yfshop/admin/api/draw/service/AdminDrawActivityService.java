package com.yfshop.admin.api.draw.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.draw.request.CreateDrawActivityReq;
import com.yfshop.admin.api.draw.request.QueryDrawActivityReq;
import com.yfshop.admin.api.draw.result.YfDrawActivityResult;
import com.yfshop.common.exception.ApiException;

/**
 * @Title:抽奖活动Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 11:12:29
 * @Version:1.1.0
 */
public interface AdminDrawActivityService {

	/**
	 * 通过id得到抽奖活动YfDrawActivity
	 * @param id
	 * @return 
	 * @Description:
	 */
	public YfDrawActivityResult getYfDrawActivityById(Integer id) throws ApiException;

	/**
	 * 分页查询抽奖活动YfDrawActivity
	 * @param req
	 * @return 
	 * @Description:
	 */
	public Page<YfDrawActivityResult> findYfDrawActivityListByPage(QueryDrawActivityReq req) throws ApiException;

	/**
	 * 得到所有抽奖活动YfDrawActivity
	 * @param req
	 * @return 
	 * @Description:
	 */
	public List<YfDrawActivityResult> getAll(QueryDrawActivityReq req) throws ApiException;

	/**
	 * 添加抽奖活动YfDrawActivity
	 * @param req
	 * @Description:
	 */
	public void insertYfDrawActivity(CreateDrawActivityReq req) throws ApiException;
	
	/**
	 * 通过id修改抽奖活动YfDrawActivity throws ApiException;
	 * @param req
	 * @Description:
	 */
	public void updateYfDrawActivity(CreateDrawActivityReq req) throws ApiException;

	/**
	 * 通过id修改上下架状态
	 * @param id		id
	 * @param isEnable	Y | N
	 * @throws ApiException
	 */
	public void updateYfDrawActivityStatus(Integer id, String isEnable) throws ApiException;

	/**
	 * 通过id删除抽奖活动
	 * @param id		id
	 * @throws ApiException
	 */
	public void deleteYfDrawActivityById(Integer id) throws ApiException;

}
