package com.yfshop.admin.api.draw.service;

import com.yfshop.admin.api.draw.request.QueryProvinceRateReq;
import com.yfshop.admin.api.draw.request.SaveProvinceRateReq;
import com.yfshop.admin.api.draw.result.DrawProvinceResult;
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
     *
     * @param id
     * @return
     * @Description:
     */
    public DrawProvinceResult getYfDrawProvinceById(Integer id) throws ApiException;

    /**
     * 得到所有抽奖省份定制化中奖几率YfDrawProvince
     *
     * @param req
     * @return
     * @Description:
     */
    public List<DrawProvinceResult> getAll(QueryProvinceRateReq req) throws ApiException;

    /**
     * 保存省份概率
     * @param req
     * @return
     * @throws ApiException
     */
    Void saveProvinceRate(List<SaveProvinceRateReq> req) throws ApiException;

    List<DrawProvinceResult> getProvinceRate(Integer id);

    Void deleteProvinceRate(Integer id);
}
