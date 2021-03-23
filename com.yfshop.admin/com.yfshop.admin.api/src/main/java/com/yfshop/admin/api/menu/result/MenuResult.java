package com.yfshop.admin.api.menu.result;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
public class MenuResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 父菜单标识
     */
    private String parentMenuAlias;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单标识
     */
    private String menuAlias;

    /**
     * 菜单图标
     */
    private String menuIcon;

    private String linkUrl;

    /**
     * 角色标识
     */
    private String roleAlias;

    private Integer sort;

    private List<MenuResult> subMenus;
}
