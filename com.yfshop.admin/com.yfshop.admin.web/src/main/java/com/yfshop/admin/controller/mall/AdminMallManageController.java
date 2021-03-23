package com.yfshop.admin.controller.mall;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.mall.AdminMallManageService;
import com.yfshop.admin.api.mall.request.CreateBannerReq;
import com.yfshop.admin.api.mall.request.CreateItemCategoryReq;
import com.yfshop.admin.api.mall.request.UpdateBannerReq;
import com.yfshop.admin.api.mall.request.UpdateItemCategoryReq;
import com.yfshop.admin.api.mall.result.BannerResult;
import com.yfshop.admin.api.mall.result.ItemCategoryResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.validate.annotation.CandidateValue;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-22 18:37
 */
@Validated
@Controller
@RequestMapping("admin/mall")
public class AdminMallManageController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminMallManageController.class);

    @DubboReference(check = false)
    private AdminMallManageService adminMallManageService;

    @ApiOperation(value = "创建分类", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "categoryName", value = "分类名称", required = true),
            @ApiImplicitParam(paramType = "query", name = "isEnable", value = "是否可用(Y|N)", required = false),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序字段", required = false)
    })
    @RequestMapping(value = "/createCategory", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> createCategory(@NotNull(message = "创建分类信息不能为空") CreateItemCategoryReq req) {
        return CommonResult.success(adminMallManageService.createCategory(req));
    }

    @ApiOperation(value = "查询分类", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "isEnable", value = "是否可用(Y|N)", required = false),
    })
    @RequestMapping(value = "/queryCategory", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<List<ItemCategoryResult>> queryCategory(String isEnable) {
        return CommonResult.success(adminMallManageService.queryCategory(
                StringUtils.isBlank(isEnable) ? null : "Y".equalsIgnoreCase(isEnable))
        );
    }

    @ApiOperation(value = "删除分类", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "categoryId", value = "分类id", required = true)
    })
    @RequestMapping(value = "/deleteCategory", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> deleteCategory(@NotNull(message = "分类id不能为空") Integer categoryId) {
        return CommonResult.success(adminMallManageService.deleteCategory(categoryId));
    }

    @ApiOperation(value = "编辑分类", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "categoryId", value = "分类id", required = true),
            @ApiImplicitParam(paramType = "query", name = "categoryName", value = "分类名称", required = true)
    })
    @RequestMapping(value = "/editCategory", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> editCategory(@NotNull(message = "编辑的分类信息不能为空") UpdateItemCategoryReq req) {
        return CommonResult.success(adminMallManageService.editCategory(req));
    }

    @ApiOperation(value = "创建首页banner", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "bannerName", value = "banner名称", required = true),
            @ApiImplicitParam(paramType = "query", name = "imageUrl", value = "banner图片地址", required = true),
            @ApiImplicitParam(paramType = "query", name = "jumpUrl", value = "跳转链接", required = true),
            @ApiImplicitParam(paramType = "query", name = "isEnable", value = "是否上架(Y|N)", required = true),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序字段", required = false)
    })
    @RequestMapping(value = "/createHomeBanner", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> createHomeBanner(@NotBlank(message = "banner名称不能为空") String bannerName,
                                               @NotBlank(message = "banner图片链接不能为空") String imageUrl,
                                               @NotBlank(message = "banner跳转链接不能为空") String jumpUrl,
                                               @NotBlank(message = "是否上架不能为空") @CandidateValue(candidateValue = {"Y", "N"}, message = "是否上架值只能是Y|N") String isEnable,
                                               @RequestParam(name = "sort", required = false, defaultValue = "0") Integer sort) {
        CreateBannerReq req = new CreateBannerReq();
        req.setBannerName(bannerName);
        req.setPositions("home");
        req.setImageUrl(imageUrl);
        req.setJumpUrl(jumpUrl);
        req.setIsEnable(isEnable);
        req.setSort(sort);
        return CommonResult.success(adminMallManageService.createBanner(req));
    }

    @ApiOperation(value = "删除首页banner", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "bannerId", value = "bannerId", required = true)
    })
    @RequestMapping(value = "/deleteHomeBanner", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> deleteHomeBanner(@NotNull(message = "bannerId不能为空") Integer bannerId) {
        return CommonResult.success(adminMallManageService.deleteBanner(bannerId));
    }

    @ApiOperation(value = "分页查询首页banner", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "currentPage", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页显示个数", required = false)
    })
    @RequestMapping(value = "/pageQueryHomeBanner", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<IPage<BannerResult>> pageQueryHomeBanner(@RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                                           @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return CommonResult.success(adminMallManageService.pageQueryBanner(currentPage, pageSize, "home"));
    }

    @ApiOperation(value = "编辑首页banner", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "bannerId", value = "bannerId", required = true),
            @ApiImplicitParam(paramType = "query", name = "bannerName", value = "banner名称", required = true),
            @ApiImplicitParam(paramType = "query", name = "imageUrl", value = "banner图片地址", required = true),
            @ApiImplicitParam(paramType = "query", name = "jumpUrl", value = "跳转链接", required = true),
            @ApiImplicitParam(paramType = "query", name = "isEnable", value = "是否上架(Y|N)", required = true),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序字段", required = false)
    })
    @RequestMapping(value = "/editHomeBanner", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> editHomeBanner(@NotNull(message = "bannerId不能为空") Integer bannerId,
                                             @NotBlank(message = "banner名称不能为空") String bannerName,
                                             @NotBlank(message = "banner图片链接不能为空") String imageUrl,
                                             @NotBlank(message = "banner跳转链接不能为空") String jumpUrl,
                                             @NotBlank(message = "是否上架不能为空") @CandidateValue(candidateValue = {"Y", "N"}, message = "是否上架值只能是Y|N") String isEnable,
                                             @RequestParam(name = "sort", required = false, defaultValue = "0") Integer sort) {
        UpdateBannerReq req = new UpdateBannerReq();
        req.setBannerId(bannerId);
        req.setBannerName(bannerName);
        req.setImageUrl(imageUrl);
        req.setJumpUrl(jumpUrl);
        req.setIsEnable(isEnable);
        req.setSort(sort);
        return CommonResult.success(adminMallManageService.editBanner(req));
    }

    @ApiOperation(value = "创建首页banner", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "bannerName", value = "banner名称", required = true),
            @ApiImplicitParam(paramType = "query", name = "imageUrl", value = "banner图片地址", required = true),
            @ApiImplicitParam(paramType = "query", name = "jumpUrl", value = "跳转链接", required = true),
            @ApiImplicitParam(paramType = "query", name = "isEnable", value = "是否上架(Y|N)", required = true),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序字段", required = false)
    })
    @RequestMapping(value = "/createLoopBanner", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> createLoopBanner(@NotBlank(message = "banner名称不能为空") String bannerName,
                                               @NotBlank(message = "banner图片链接不能为空") String imageUrl,
                                               @NotBlank(message = "banner跳转链接不能为空") String jumpUrl,
                                               @NotBlank(message = "是否上架不能为空") @CandidateValue(candidateValue = {"Y", "N"}, message = "是否上架值只能是Y|N") String isEnable,
                                               @RequestParam(name = "sort", required = false, defaultValue = "0") Integer sort) {
        CreateBannerReq req = new CreateBannerReq();
        req.setBannerName(bannerName);
        req.setPositions("banner");
        req.setImageUrl(imageUrl);
        req.setJumpUrl(jumpUrl);
        req.setIsEnable(isEnable);
        req.setSort(sort);
        return CommonResult.success(adminMallManageService.createBanner(req));
    }

    @ApiOperation(value = "删除轮播banner", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "bannerId", value = "bannerId", required = true)
    })
    @RequestMapping(value = "/deleteLoopBanner", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> deleteLoopBanner(@NotNull(message = "bannerId不能为空") Integer bannerId) {
        return CommonResult.success(adminMallManageService.deleteBanner(bannerId));
    }

    @ApiOperation(value = "分页查询轮播banner", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "currentPage", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页显示个数", required = false)
    })
    @RequestMapping(value = "/pageQueryLoopBanner", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<IPage<BannerResult>> pageQueryLoopBanner(@RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return CommonResult.success(adminMallManageService.pageQueryBanner(currentPage, pageSize, "banner"));
    }

    @ApiOperation(value = "编辑轮播banner", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "bannerId", value = "bannerId", required = true),
            @ApiImplicitParam(paramType = "query", name = "bannerName", value = "banner名称", required = true),
            @ApiImplicitParam(paramType = "query", name = "imageUrl", value = "banner图片地址", required = true),
            @ApiImplicitParam(paramType = "query", name = "jumpUrl", value = "跳转链接", required = true),
            @ApiImplicitParam(paramType = "query", name = "isEnable", value = "是否上架(Y|N)", required = true),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序字段", required = false)
    })
    @RequestMapping(value = "/editLoopBanner", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> editLoopBanner(@NotNull(message = "bannerId不能为空") Integer bannerId,
                                             @NotBlank(message = "banner名称不能为空") String bannerName,
                                             @NotBlank(message = "banner图片链接不能为空") String imageUrl,
                                             @NotBlank(message = "banner跳转链接不能为空") String jumpUrl,
                                             @NotBlank(message = "是否上架不能为空") @CandidateValue(candidateValue = {"Y", "N"}, message = "是否上架值只能是Y|N") String isEnable,
                                             @RequestParam(name = "sort", required = false, defaultValue = "0") Integer sort) {
        UpdateBannerReq req = new UpdateBannerReq();
        req.setBannerId(bannerId);
        req.setBannerName(bannerName);
        req.setImageUrl(imageUrl);
        req.setJumpUrl(jumpUrl);
        req.setIsEnable(isEnable);
        req.setSort(sort);
        return CommonResult.success(adminMallManageService.editBanner(req));
    }

}
