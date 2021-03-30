package com.yfshop.admin.api.sourcefactory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.sourcefactory.req.CreateSourceFactoryReq;
import com.yfshop.admin.api.sourcefactory.req.ImportSourceFactoryReq;
import com.yfshop.admin.api.sourcefactory.req.QuerySourceFactoriesReq;
import com.yfshop.admin.api.sourcefactory.req.UpdateSourceFactoryReq;
import com.yfshop.admin.api.sourcefactory.result.SourceFactoryResult;
import com.yfshop.common.exception.ApiException;

import javax.validation.Valid;
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
    Void createSourceFactory(@Valid @NotNull CreateSourceFactoryReq req) throws ApiException;

    /**
     * 编辑源码工厂
     *
     * @param req the req
     * @return void
     * @throws ApiException e
     */
    Void updateSourceFactory(@Valid @NotNull UpdateSourceFactoryReq req) throws ApiException;

    /**
     * 导入工厂数据
     *
     * @param req the req
     * @return void
     * @throws ApiException e
     */
    Void importSourceFactory(@Valid @NotNull ImportSourceFactoryReq req) throws ApiException;

    /**
     * 分页查询工厂列表
     *
     * @param req the req
     * @return the page data list
     */
    IPage<SourceFactoryResult> pageQuerySourceFactories(QuerySourceFactoriesReq req);
}
