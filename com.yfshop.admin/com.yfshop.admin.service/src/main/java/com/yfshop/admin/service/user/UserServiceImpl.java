package com.yfshop.admin.service.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.user.UserService;
import com.yfshop.admin.api.user.request.UserReq;
import com.yfshop.admin.api.user.result.UserResult;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.model.User;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

@Validated
@DubboService
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserResult getUserByOpenId(String openId) throws ApiException {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getOpenId, openId));
        return BeanUtil.convert(user, UserResult.class);
    }

    @Override
    public Void subscribe(UserReq userReq) throws ApiException {
        UserResult userResult = getUserByOpenId(userReq.getOpenId());
        if (userResult == null) {
            User user = BeanUtil.convert(userReq, User.class);
            user.setSubscribe("Y");
            userMapper.insert(user);
        } else {
            User user = BeanUtil.convert(userReq, User.class);
            user.setId(userResult.getId());
            user.setSubscribe("Y");
            userMapper.updateById(user);
        }
        return null;
    }

    @Override
    public Void unsubscribe(UserReq userReq) throws ApiException {
        UserResult userResult = getUserByOpenId(userReq.getOpenId());
        if (userResult == null) {
            User user = BeanUtil.convert(userReq, User.class);
            user.setSubscribe("N");
            userMapper.insert(user);
        } else {
            User user = BeanUtil.convert(userReq, User.class);
            user.setId(userResult.getId());
            user.setSubscribe("N");
            userMapper.updateById(user);
        }
        return null;
    }

    @Override
    public Integer checkSubscribe(String openId) {
        return userMapper.selectCount(Wrappers.<User>lambdaQuery()
                .eq(User::getOpenId, openId)
                .eq(User::getSubscribe, 'Y'));
    }

    @Override
    public Void saveUser(UserReq userReq) throws ApiException {
        UserResult userResult = getUserByOpenId(userReq.getOpenId());
        if (userResult == null) {
            User user = BeanUtil.convert(userReq, User.class);
            userMapper.insert(user);
        } else {
            User user = BeanUtil.convert(userReq, User.class);
            user.setId(userResult.getId());
            userMapper.updateById(user);
        }
        return null;
    }

}
