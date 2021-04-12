package com.yfshop.shop.controller.mall;

import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.enums.BannerPositionsEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.mall.MallService;
import com.yfshop.shop.service.mall.req.QueryItemDetailReq;
import com.yfshop.shop.service.mall.req.QueryItemReq;
import com.yfshop.shop.service.mall.result.BannerResult;
import com.yfshop.shop.service.mall.result.ItemCategoryResult;
import com.yfshop.shop.service.mall.result.ItemResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Xulg
 * Created in 2021-03-29 11:30
 */
@Controller
@RequestMapping("mall")
@Validated
public class MallController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MallController.class);

    @DubboReference(check = false)
    private MallService mallService;

    @ApiOperation(value = "查询商城商品分类", httpMethod = "GET")
    @RequestMapping(value = "/queryCategories", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<ItemCategoryResult>> queryCategories() {
        return CommonResult.success(mallService.queryCategories());
    }

    @ApiOperation(value = "根据分类ID查询商品列表", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "categoryId", value = "分类ID")
    })
    @RequestMapping(value = "/queryItems", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<ItemResult>> queryItems(QueryItemReq req) {
        return CommonResult.success(mallService.queryItems(req));
    }

    @ApiOperation(value = "查询商品详情", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "itemId", value = "商品ID")
    })
    @RequestMapping(value = "/findItemDetail", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<ItemResult> findItemDetail(QueryItemDetailReq req) {
        return CommonResult.success(mallService.findItemDetail(req));
    }

    @ApiOperation(value = "查询banner", httpMethod = "GET")
    @RequestMapping(value = "/queryBanners", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Map<String, List<BannerResult>>> queryBanners() {
        CompletableFuture<List<BannerResult>> homeBannersFuture = CompletableFuture.supplyAsync(() -> mallService.queryHomeBannerList());
        CompletableFuture<List<BannerResult>> loopBannersFuture = CompletableFuture.supplyAsync(() -> mallService.queryLoopBannerList());
        Map<String, List<BannerResult>> data = new HashMap<>(3);
        try {
            data.put(BannerPositionsEnum.HOME.getCode(), homeBannersFuture.get(10, TimeUnit.SECONDS));
            data.put(BannerPositionsEnum.BANNER.getCode(), loopBannersFuture.get(10, TimeUnit.SECONDS));
            return CommonResult.success(data);
        } catch (Exception e) {
            logger.error("查询banner失败", e);
            throw new ApiException(500, "查询banner失败");
        }
    }
}
