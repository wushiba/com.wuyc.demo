package com.yfshop.shop.service.user;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.OrderAddressMapper;
import com.yfshop.code.mapper.UserAddressMapper;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.model.OrderAddress;
import com.yfshop.code.model.User;
import com.yfshop.code.model.UserAddress;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.address.result.UserAddressResult;
import com.yfshop.shop.service.user.request.UserReq;
import com.yfshop.shop.service.user.result.UserResult;
import com.yfshop.shop.service.user.service.FrontUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

@Validated
@DubboService
public class FrontUserServiceImpl implements FrontUserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserAddressMapper userAddressMapper;
    @Resource
    private RedisService redisService;

    @Override
    public UserResult getUserById(Integer userId) throws ApiException {
        if (userId == null || userId <= 0) {
            return null;
        }

        User user = null;
        Object userObject = redisService.get(CacheConstants.USER_INFO_ID + userId);
        if (userObject != null) {
            user = JSON.parseObject(userObject.toString(), User.class);
        } else {
            user = userMapper.selectById(userId);
            redisService.set(CacheConstants.USER_INFO_ID + userId,
                    JSON.toJSONString(user), 60 * 30);
        }
        return user == null ? null : BeanUtil.convert(user, UserResult.class);
    }

    @Override
    public UserResult getUserByOpenId(String openId) throws ApiException {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getOpenId, openId));
        return BeanUtil.convert(user, UserResult.class);
    }


    @Override
    public Integer checkSubscribe(String openId) {
        return userMapper.selectCount(Wrappers.<User>lambdaQuery()
                .eq(User::getOpenId, openId)
                .eq(User::getSubscribe, 'Y'));
    }

    @Override
    public Integer saveUser(UserReq userReq) throws ApiException {
        UserResult userResult = getUserByOpenId(userReq.getOpenId());
        User user;
        if (userResult == null) {
            user = BeanUtil.convert(userReq, User.class);
            userMapper.insert(user);
        } else {
            user = BeanUtil.convert(userReq, User.class);
            user.setId(userResult.getId());
            userMapper.updateById(user);
        }
        return user.getId();
    }

    /**
     * 查询用户收货地址
     * @param addressId     收货地址id
     * @return
     * @throws ApiException
     */
    @Override
    public UserAddressResult getUserAddressById(Long addressId) throws ApiException {
        Asserts.assertNonNull(addressId, 500, "收货地址id不可以为空");

        Object userAddressObject = redisService.get(CacheConstants.USER_ADDRESS_ID);
        if (userAddressObject != null) {
            return JSON.parseObject(userAddressObject.toString(), UserAddressResult.class);
        }

        UserAddress orderAddress = userAddressMapper.selectOne(Wrappers.lambdaQuery(UserAddress.class)
                .eq(UserAddress::getId, addressId));
        Asserts.assertNonNull(orderAddress, 500, "收货地址不存在");
        redisService.set(CacheConstants.USER_ADDRESS_ID, JSON.toJSONString(orderAddress), 60 * 60);
        return BeanUtil.convert(orderAddress, UserAddressResult.class);
    }

}
