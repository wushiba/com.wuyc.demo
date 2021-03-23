package com.yfshop.admin.api.rolepermission.request;

import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.validate.annotation.CheckEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-23 15:07
 */
@Data
public class AssociateRolePermissionReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotBlank(message = "角色码不能为空")
    @CheckEnum(value = GroupRoleEnum.class, message = "不支持的角色码")
    private String role;
    @NotEmpty(message = "权限列表不能为空")
    private List<String> permissions;
}
