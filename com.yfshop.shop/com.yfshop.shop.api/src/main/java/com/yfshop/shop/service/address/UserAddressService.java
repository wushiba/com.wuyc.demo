package com.yfshop.shop.service.address;

import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.address.request.CreateUserAddressReq;
import com.yfshop.shop.service.address.request.UpdateUserAddressReq;
import com.yfshop.shop.service.address.result.UserAddressResult;

import java.util.List;

/**
 * 用户地址服务
 *
 * @author Xulg
 * Created in 2021-03-23 18:52
 */
public interface UserAddressService {

    /**
     * 查询用户地址列表
     *
     * @param userId the user id
     * @return the user address list
     */
    List<UserAddressResult> queryUserAddresses(Integer userId);

    /**
     * 添加用户收货地址
     *
     * @param userId the user id
     * @param req    the req
     * @return void
     * @throws ApiException e
     */
    Void addUserAddress(Integer userId, CreateUserAddressReq req) throws ApiException;

    /**
     * 编辑用户收货地址
     *
     * @param userId the user id
     * @param req    the req
     * @return void
     * @throws ApiException e
     */
    Void updateUserAddress(Integer userId, UpdateUserAddressReq req) throws ApiException;

    /**
     * 删除用户的地址
     *
     * @param userId         the user id
     * @param userAddressIds the user address id
     * @return void
     * @throws ApiException e
     */
    Void deleteUserAddress(Integer userId, List<Integer> userAddressIds) throws ApiException;

    /**
     * 设置默认地址
     *
     * @param userId        the user id
     * @param userAddressId the user address id
     * @return void
     * @throws ApiException e
     */
    Void configDefaultUserAddress(Integer userId, Integer userAddressId) throws ApiException;
}