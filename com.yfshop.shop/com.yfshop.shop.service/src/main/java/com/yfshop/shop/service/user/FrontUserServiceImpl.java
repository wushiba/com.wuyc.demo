package com.yfshop.shop.service.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.model.User;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
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

}
