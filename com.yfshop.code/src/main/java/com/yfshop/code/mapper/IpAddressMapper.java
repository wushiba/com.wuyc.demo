package com.yfshop.code.mapper;

import com.yfshop.code.model.IpAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * ip地址表 Mapper 接口
 * </p>
 *
 * @author yoush
 * @since 2021-03-24
 */
public interface IpAddressMapper extends BaseMapper<IpAddress> {

    /**
     * 通过ip转化的数字，获取ip所在地
     * @param ipLong
     * @return
     * @Description:
     */
    public IpAddress getYfIpAddressByIpLong(@Param("ipLong") Long ipLong);

}
