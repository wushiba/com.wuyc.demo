package com.yfshop.admin.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.excel.result.WebsiteCodeExcel;
import com.yfshop.admin.dao.ExcelDao;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.model.Merchant;
import com.yfshop.common.enums.GroupRoleEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Xulg
 * @since 2021-06-17 18:39
 * Description: TODO 请加入类描述信息
 */
@Component
public class QueryJxsDataHelper {
    @Resource
    private MerchantMapper merchantMapper;
    @Resource
    private ExcelDao excelDao;

    public List<WebsiteCodeExcel> getWebsiteCode() {
        List<Merchant> jxsMerchants = merchantMapper.selectList(Wrappers.lambdaQuery(Merchant.class)
                .eq(Merchant::getRoleAlias, GroupRoleEnum.JXS.getCode())
                .eq(Merchant::getIsEnable, "Y").eq(Merchant::getIsDelete, "N"));
        return jxsMerchants.parallelStream().map(jxsMerchant -> {
            int childCount = merchantMapper.selectCount(Wrappers.lambdaQuery(Merchant.class)
                    .ne(Merchant::getId, jxsMerchant.getId())
                    .like(Merchant::getPidPath, "." + jxsMerchant.getId() + ".")
                    .eq(Merchant::getIsEnable, "Y").eq(Merchant::getIsDelete, "N")
                    .ne(Merchant::getRoleAlias, GroupRoleEnum.WD.getCode()));
            int factoryCount = excelDao.countFactoryCount(jxsMerchant.getId());
            int emailCount = excelDao.countEmailCount(jxsMerchant.getId());
            int activeCount = excelDao.countActiveCount(jxsMerchant.getId());
            WebsiteCodeExcel websiteCodeExcel = new WebsiteCodeExcel();
            websiteCodeExcel.setProvince(jxsMerchant.getProvince());
            websiteCodeExcel.setCity(jxsMerchant.getCity());
            websiteCodeExcel.setDistrict(jxsMerchant.getDistrict());
            websiteCodeExcel.setAddress(jxsMerchant.getProvince() + jxsMerchant.getCity() + jxsMerchant.getDistrict());
            websiteCodeExcel.setPMerchantName(jxsMerchant.getPMerchantName());
            websiteCodeExcel.setMerchantName(jxsMerchant.getMerchantName());
            websiteCodeExcel.setMobile(jxsMerchant.getMobile());
            websiteCodeExcel.setContacts(jxsMerchant.getContacts());
            websiteCodeExcel.setChildCount(childCount);
            websiteCodeExcel.setFactoryCount(factoryCount);
            websiteCodeExcel.setEmailCount(emailCount);
            websiteCodeExcel.setActiveCount(activeCount);
            return websiteCodeExcel;
        }).collect(Collectors.toList());
    }

}
