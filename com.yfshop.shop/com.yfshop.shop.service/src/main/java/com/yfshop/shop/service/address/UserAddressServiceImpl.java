package com.yfshop.shop.service.address;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.RegionMapper;
import com.yfshop.code.mapper.UserAddressMapper;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.model.Region;
import com.yfshop.code.model.User;
import com.yfshop.code.model.UserAddress;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.dao.UserAddressDao;
import com.yfshop.shop.service.address.request.CreateUserAddressReq;
import com.yfshop.shop.service.address.request.UpdateUserAddressReq;
import com.yfshop.shop.service.address.result.UserAddressResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-23 18:59
 */
@DubboService
@Validated
public class UserAddressServiceImpl implements UserAddressService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserAddressMapper userAddressMapper;
    @Resource
    private UserAddressDao customUserAddressMapper;
    @Resource
    private RegionMapper regionMapper;

    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_USER_ADDRESS_CACHE_NAME,
            key = "'" + CacheConstants.MALL_USER_ADDRESS_CACHE_KEY_PREFIX + "' + #root.args[0]")
    @Override
    public List<UserAddressResult> queryUserAddresses(Integer userId) {
        if (userId == null) {
            return new ArrayList<>(0);
        }
        List<UserAddress> userAddresses = userAddressMapper.selectList(Wrappers
                .lambdaQuery(UserAddress.class).eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getCreateTime));
        // 默认地址放第一位置
        userAddresses.sort((u1, u2) -> u1.getIsDefault().equals(u2.getIsDefault()) ? 0 : ("Y".equals(u1.getIsDefault()) ? -1 : 1));
        return BeanUtil.convertList(userAddresses, UserAddressResult.class);
    }

    @CacheEvict(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_USER_ADDRESS_CACHE_NAME,
            key = "'" + CacheConstants.MALL_USER_ADDRESS_CACHE_KEY_PREFIX + "' + #root.args[0]")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void addUserAddress(@NotNull(message = "用户ID不能为空") Integer userId, @Valid @NotNull CreateUserAddressReq req) throws ApiException {
        assertUserExist(userId);
        Region province = regionMapper.selectById(req.getProvinceId());
        Asserts.assertNonNull(province, 500, "省份信息不存在");
        Region city = regionMapper.selectById(req.getCityId());
        Asserts.assertNonNull(city, 500, "市信息不存在");
        Region district = regionMapper.selectById(req.getDistrictId());
        Asserts.assertNonNull(district, 500, "区信息不存在");
        UserAddress userAddress = new UserAddress();
        userAddress.setCreateTime(LocalDateTime.now());
        userAddress.setUpdateTime(LocalDateTime.now());
        userAddress.setUserId(userId);
        userAddress.setIsDefault("N");
        userAddress.setRealname(req.getRealname());
        userAddress.setMobile(req.getMobile());
        userAddress.setSex(req.getSex());
        userAddress.setProvinceId(req.getProvinceId());
        userAddress.setCityId(req.getCityId());
        userAddress.setDistrictId(req.getDistrictId());
        userAddress.setAddress(req.getAddress());
        userAddress.setProvince(province.getName());
        userAddress.setCity(city.getName());
        userAddress.setDistrict(district.getName());
        userAddressMapper.insert(userAddress);
        return null;
    }

    @CacheEvict(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_USER_ADDRESS_CACHE_NAME,
            key = "'" + CacheConstants.MALL_USER_ADDRESS_CACHE_KEY_PREFIX + "' + #root.args[0]")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void updateUserAddress(@NotNull(message = "用户ID不能为空") Integer userId, @Valid @NotNull UpdateUserAddressReq req) throws ApiException {
        assertUserExist(userId);
        UserAddress existUserAddress = userAddressMapper.selectById(req.getUserAddressId());
        Asserts.assertNonNull(existUserAddress, 500, "地址信息不存在");
        Asserts.assertTrue(existUserAddress.getUserId().equals(userId), 500, "非法操作");
        Region province = regionMapper.selectById(req.getProvinceId());
        Asserts.assertNonNull(province, 500, "省份信息不存在");
        Region city = regionMapper.selectById(req.getCityId());
        Asserts.assertNonNull(city, 500, "市信息不存在");
        Region district = regionMapper.selectById(req.getDistrictId());
        Asserts.assertNonNull(district, 500, "区信息不存在");
        UserAddress userAddress = new UserAddress();
        userAddress.setId(req.getUserAddressId());
        userAddress.setCreateTime(LocalDateTime.now());
        userAddress.setUpdateTime(LocalDateTime.now());
        userAddress.setIsDefault("N");
        userAddress.setRealname(req.getRealname());
        userAddress.setMobile(req.getMobile());
        userAddress.setSex(req.getSex());
        userAddress.setProvinceId(req.getProvinceId());
        userAddress.setCityId(req.getCityId());
        userAddress.setDistrictId(req.getDistrictId());
        userAddress.setAddress(req.getAddress());
        userAddress.setProvince(province.getName());
        userAddress.setCity(city.getName());
        userAddress.setDistrict(district.getName());
        userAddressMapper.updateById(userAddress);
        return null;
    }

    @CacheEvict(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_USER_ADDRESS_CACHE_NAME,
            key = "'" + CacheConstants.MALL_USER_ADDRESS_CACHE_KEY_PREFIX + "' + #root.args[0]")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void deleteUserAddress(@NotNull(message = "用户ID不能为空") Integer userId, List<Integer> userAddressIds) throws ApiException {
        if (CollectionUtil.isEmpty(userAddressIds)) {
            return null;
        }
        assertUserExist(userId);
        List<UserAddress> userAddresses = userAddressMapper.selectBatchIds(userAddressIds);
        if (CollectionUtil.isEmpty(userAddresses)) {
            return null;
        }
        boolean allMatch = userAddresses.stream()
                .allMatch(userAddress -> userAddress.getUserId().equals(userId));
        Asserts.assertTrue(allMatch, 500, "非法操作");
        userAddressMapper.deleteBatchIds(userAddressIds);
        return null;
    }

    @CacheEvict(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_USER_ADDRESS_CACHE_NAME,
            key = "'" + CacheConstants.MALL_USER_ADDRESS_CACHE_KEY_PREFIX + "' + #root.args[0]")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void configDefaultUserAddress(@NotNull(message = "用户ID不能为空") Integer userId,
                                         @NotNull(message = "地址ID不能为空") Integer userAddressId) throws ApiException {
        assertUserExist(userId);
        UserAddress existUserAddress = userAddressMapper.selectById(userAddressId);
        Asserts.assertNonNull(existUserAddress, 500, "地址信息不存在");
        Asserts.assertTrue(existUserAddress.getUserId().equals(userId), 500, "非法操作");
        customUserAddressMapper.disableDefaultAddress(userId);
        UserAddress bean = new UserAddress();
        bean.setId(userAddressId);
        bean.setIsDefault("Y");
        userAddressMapper.updateById(bean);
        return null;
    }

    @SuppressWarnings("UnusedReturnValue")
    private User assertUserExist(Integer userId) {
        User user = userMapper.selectById(userId);
        Asserts.assertNonNull(user, 500, "用户不存在");
        return user;
    }
}
