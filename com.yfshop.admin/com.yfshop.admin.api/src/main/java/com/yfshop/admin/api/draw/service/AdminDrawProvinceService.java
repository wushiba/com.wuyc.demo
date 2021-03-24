package com.yfshop.admin.api.draw.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.draw.request.QueryProvinceRateReq;
import com.yfshop.admin.api.draw.result.YfDrawProvinceResult;
import com.yfshop.common.exception.ApiException;
import java.util.List;

/**
 * @Title:抽奖省份定制化中奖几率Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 11:13:23
 * @Version:1.1.0
 */
public interface AdminDrawProvinceService {

	/**
	 * 通过id得到抽奖省份定制化中奖几率YfDrawProvince
	 * @param id
	 * @return 
	 * @Description:
	 */
	public YfDrawProvinceResult getYfDrawProvinceById(Integer id) throws ApiException;

	/**
	 * 得到所有抽奖省份定制化中奖几率YfDrawProvince
	 * @param req
	 * @return 
	 * @Description:
	 */
	public List<YfDrawProvinceResult> getAll(QueryProvinceRateReq req) throws ApiException;

}
