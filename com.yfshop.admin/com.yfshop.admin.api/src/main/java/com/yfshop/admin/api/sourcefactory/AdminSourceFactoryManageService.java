package com.yfshop.admin.api.sourcefactory;

import com.yfshop.admin.api.sourcefactory.req.CreateSourceFactoryReq;
import com.yfshop.admin.api.sourcefactory.req.UpdateSourceFactoryReq;
import com.yfshop.common.exception.ApiException;

import javax.validation.constraints.NotNull;

/**
 * @author Xulg
 * Created in 2021-03-25 18:20
 */
public interface AdminSourceFactoryManageService {

    /**
     * 创建源码工厂
     *
     * @param req the req
     * @return void
     * @throws ApiException e
     */
    Void createSourceFactory(@NotNull CreateSourceFactoryReq req) throws ApiException;

    /**
     * 编辑源码工厂
     *
     * @param req the req
     * @return void
     * @throws ApiException e
     */
    Void updateSourceFactory(@NotNull UpdateSourceFactoryReq req) throws ApiException;

}
