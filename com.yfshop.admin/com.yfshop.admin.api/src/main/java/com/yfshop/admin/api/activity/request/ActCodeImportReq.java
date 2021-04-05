package com.yfshop.admin.api.activity.request;

import lombok.Data;

import javax.annotation.RegEx;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class ActCodeImportReq implements Serializable {
    @NotNull(message = "活动id不能null")
    private Integer actId;
    @Pattern(regexp = "/^([a-f\\d]{32}|[A-F\\d]{32})$/",message = "md5格式不正确！")
    private String md5;
    @Pattern(regexp = "/^(((ht|f)tps?):\\/\\/)?[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?$/",message = "url格式不正确！")
    private String url;
}
