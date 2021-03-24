package com.yfshop.shop.service.address;

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
    List<Object> queryUserAddresses(Integer userId);

    /**
     * 添加用户收货地址
     *
     * @param req the req
     * @return void
     * @throws Exception e
     */
    Void addUserAddress(Object req) throws Exception;

    /**
     * 编辑用户收货地址
     *
     * @param req the req
     * @return void
     * @throws Exception e
     */
    Void updateUserAddress(Object req) throws Exception;

    /**
     * 删除用户的地址
     *
     * @param userId         the user id
     * @param userAddressIds the user address id
     * @return void
     * @throws Exception e
     */
    Void deleteUserAddress(Integer userId, List<Integer> userAddressIds) throws Exception;
}
