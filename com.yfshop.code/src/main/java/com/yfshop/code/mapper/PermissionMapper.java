package com.yfshop.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yfshop.code.model.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    Permission findByAlias(@Param("alias") String alias);

    List<Permission> findByAliases(@Param("list") List<String> aliases);
}
