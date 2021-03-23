package com.yfshop.admin.api.rolepermission.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-23 15:02
 */
@Data
public class CreatePermissionReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotBlank(message = "权限码不能为空")
    private String permissionAlias;
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;
}
