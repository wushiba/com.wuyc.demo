package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_menu")
public class Menu extends Model<Menu> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
