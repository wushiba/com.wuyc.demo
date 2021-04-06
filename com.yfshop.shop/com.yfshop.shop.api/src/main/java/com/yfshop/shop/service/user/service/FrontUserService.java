package com.yfshop.shop.service.user.service;


import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.user.request.UserReq;
import com.yfshop.shop.service.user.result.UserResult;

/**
 * 微信用户服务服务
 *
 * @author youshenghui
 * Created in 2021-03-23 9:10
 */
public interface FrontUserService {

    UserResult getUserByOpenId(String openId) throws ApiException;

    Integer checkSubscribe(String openId);

    Integer saveUser(UserReq convert) throws ApiException;
}
