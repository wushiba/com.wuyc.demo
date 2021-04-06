package com.yfshop.shop.service.user.service;


import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.address.result.UserAddressResult;
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

    /**
     * 查询用户收货地址
     * @param addressId     收货地址id
     * @return
     * @throws ApiException
     */
    UserAddressResult getUserAddressById(Integer addressId) throws ApiException;

}
