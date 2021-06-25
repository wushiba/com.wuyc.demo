package com.yfshop.admin.api.user;

import com.yfshop.admin.api.user.request.UserReq;
import com.yfshop.admin.api.user.result.UserResult;
import com.yfshop.common.exception.ApiException;

/**
 * 微信用户服务服务
 *
 * @author youshenghui
 * Created in 2021-03-23 9:10
 */
public interface UserService {

    UserResult getUserByOpenId(String openId) throws ApiException;

    Void subscribe(UserReq userReq) throws ApiException;

    Void unsubscribe(UserReq userReq) throws ApiException;

    Integer checkSubscribe(String openId);

    Integer saveUser(UserReq convert) throws ApiException;

    String getSubscribeMsg();
}
