package com.yfshop.shop.service.address;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-23 18:59
 */
@DubboService
@Validated
public class UserAddressServiceImpl implements UserAddressService {
    @Override
    public List<Object> queryUserAddresses(Integer userId) {
        return null;
    }

    @Override
    public Void addUserAddress(Object req) throws Exception {
        return null;
    }

    @Override
    public Void updateUserAddress(Object req) throws Exception {
        return null;
    }

    @Override
    public Void deleteUserAddress(Integer userId, List<Integer> userAddressIds) throws Exception {
        return null;
    }
}
