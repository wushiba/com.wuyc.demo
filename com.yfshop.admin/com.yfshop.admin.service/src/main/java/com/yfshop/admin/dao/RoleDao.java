package com.yfshop.admin.dao;

import com.yfshop.code.model.Role;
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
public interface RoleDao {

    Role findByAlias(@Param("alias") String alias);

    List<Role> findByAliases(@Param("list") List<String> alias);
}