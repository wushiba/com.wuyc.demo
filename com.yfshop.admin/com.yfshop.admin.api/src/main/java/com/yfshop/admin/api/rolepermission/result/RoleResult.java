package com.yfshop.admin.api.rolepermission.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-23 16:02
 */
@Data
public class RoleResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private String roleAlias;
    private List<String> permissionAliases;
}
