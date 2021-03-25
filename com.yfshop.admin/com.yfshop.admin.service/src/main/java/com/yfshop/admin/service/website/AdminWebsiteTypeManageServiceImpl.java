package com.yfshop.admin.service.website;

import com.yfshop.admin.api.website.AdminWebsiteTypeManageService;
import com.yfshop.admin.api.website.result.WebsiteTypeResult;
import com.yfshop.code.mapper.MerchantDetailMapper;
import com.yfshop.code.mapper.WebsiteTypeMapper;
import com.yfshop.code.model.WebsiteType;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Xulg
 * Created in 2021-03-25 18:20
 */
@DubboService
@Validated
public class AdminWebsiteTypeManageServiceImpl implements AdminWebsiteTypeManageService {
    @Resource
    private WebsiteTypeMapper websiteTypeMapper;
    @Resource
    private MerchantDetailMapper merchantDetailMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createWebsiteType(@NotBlank(message = "网点类型不能为空") String typeName) throws ApiException {
        WebsiteType website = new WebsiteType();
        website.setCreateTime(LocalDateTime.now());
        website.setUpdateTime(LocalDateTime.now());
        website.setTypeName(typeName);
        try {
            websiteTypeMapper.insert(website);
        } catch (DuplicateKeyException e) {
            throw new ApiException(500, "网点类型已存在");
        }
        return null;
    }

    @Override
    public List<WebsiteTypeResult> queryWebsiteTypes() {
        List<WebsiteTypeResult> websiteTypes = websiteTypeMapper.selectList(null)
                .stream().map((w) -> BeanUtil.convert(w, WebsiteTypeResult.class))
                .collect(Collectors.toList());
        // 统计各个类型网点数量
        Map<String, Object> indexMap = merchantDetailMapper.countGroupByWebsiteType().stream()
                .collect(Collectors.toMap(map -> map.get("typeName").toString(), map -> map.get("num")));
        for (WebsiteTypeResult websiteType : websiteTypes) {
            Integer count = Integer.valueOf(indexMap.getOrDefault(websiteType.getTypeName(), "0").toString());
            websiteType.setCount(count);
        }
        return websiteTypes;
    }

}
