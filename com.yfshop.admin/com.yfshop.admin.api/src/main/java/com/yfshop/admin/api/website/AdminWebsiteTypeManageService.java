package com.yfshop.admin.api.website;

import com.yfshop.admin.api.website.result.WebsiteTypeResult;
import com.yfshop.common.exception.ApiException;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-25 18:20
 */
public interface AdminWebsiteTypeManageService {

    /**
     * 创建网点类型
     *
     * @param typeName the type name
     * @return void
     * @throws ApiException e
     */
    Void createWebsiteType(@NotBlank(message = "网点类型不能为空") String typeName) throws ApiException;

    /**
     * 查询网点类型列表
     *
     * @return the website type list
     */
    List<WebsiteTypeResult> queryWebsiteTypes();
}
