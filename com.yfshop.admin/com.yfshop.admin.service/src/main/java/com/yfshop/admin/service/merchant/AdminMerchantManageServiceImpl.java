package com.yfshop.admin.service.merchant;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.service.merchant.AdminMerchantManageService;
import com.yfshop.admin.api.service.merchant.req.CreateMerchantReq;
import com.yfshop.admin.api.service.merchant.req.QueryMerchantReq;
import com.yfshop.admin.api.service.merchant.req.UpdateMerchantReq;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.code.mapper.MerchantDetailMapper;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.mapper.RegionMapper;
import com.yfshop.code.mapper.custom.CustomMerchantMapper;
import com.yfshop.code.model.Merchant;
import com.yfshop.code.model.MerchantDetail;
import com.yfshop.code.model.Region;
import com.yfshop.code.query.QueryMerchantDetail;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户管理
 *
 * @author Xulg
 * Created in 2021-03-25 11:18
 */
@DubboService
public class AdminMerchantManageServiceImpl implements AdminMerchantManageService {

    private static final List<String> CANDIDATE_CHECK_ROLES = Arrays.asList(GroupRoleEnum.JXS.getCode(),
            GroupRoleEnum.YWY.getCode(), GroupRoleEnum.CXY.getCode());
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private MerchantMapper merchantMapper;
    @Resource
    private MerchantDetailMapper merchantDetailMapper;
    @Resource
    private CustomMerchantMapper customMerchantMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createMerchant(CreateMerchantReq req) throws ApiException {
        Region province = regionMapper.selectById(req.getProvinceId());
        Asserts.assertNonNull(province, 500, "省份信息不存在");
        Region city = regionMapper.selectById(req.getCityId());
        Asserts.assertNonNull(city, 500, "市信息不存在");
        Region district = regionMapper.selectById(req.getDistrictId());
        Asserts.assertNonNull(district, 500, "区信息不存在");

        // check the pid if necessary
        Merchant pm = null;
        if (CANDIDATE_CHECK_ROLES.contains(req.getRoleAlias())) {
            Asserts.assertNonNull(req.getPid(), 500, "创建经销商、业务员、促销员时，必须选择上级");
            pm = this.getParent(req.getPid());
        }

        // create
        MD5 md5 = MD5.create();
        Merchant merchant = new Merchant();
        merchant.setCreateTime(LocalDateTime.now());
        merchant.setUpdateTime(LocalDateTime.now());
        merchant.setOpenId(null);
        merchant.setRoleAlias(req.getRoleAlias());
        merchant.setRoleName(GroupRoleEnum.getByCode(req.getRoleAlias()).getDescription());
        merchant.setMerchantName(req.getMerchantName());
        merchant.setMobile(req.getMobile());
        merchant.setPassword(md5.digestHex(md5.digestHex(req.getPassword())));
        merchant.setContacts(req.getContacts());
        merchant.setProvince(province.getName());
        merchant.setCity(city.getName());
        merchant.setDistrict(district.getName());
        merchant.setProvinceId(req.getProvinceId());
        merchant.setCityId(req.getCityId());
        merchant.setDistrictId(req.getDistrictId());
        merchant.setAddress(req.getAddress());
        merchant.setIsEnable("Y");
        merchant.setIsDelete("N");
        merchant.setPid(pm == null ? 0 : pm.getId());
        merchant.setPMerchantName(pm == null ? null : pm.getMerchantName());
        merchant.setPidPath(this.generatePidPath(req.getPid()));
        merchantMapper.insert(merchant);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void updateMerchant(UpdateMerchantReq req) throws ApiException {
        Merchant existMerchant = merchantMapper.selectById(req.getMerchantId());
        Asserts.assertNonNull(existMerchant, 500, "编辑的商户不存在");
        Region province = regionMapper.selectById(req.getProvinceId());
        Asserts.assertNonNull(province, 500, "省份信息不存在");
        Region city = regionMapper.selectById(req.getCityId());
        Asserts.assertNonNull(city, 500, "市信息不存在");
        Region district = regionMapper.selectById(req.getDistrictId());
        Asserts.assertNonNull(district, 500, "区信息不存在");

        // check the pid if necessary
        Merchant pm = null;
        if (CANDIDATE_CHECK_ROLES.contains(req.getRoleAlias())) {
            Asserts.assertNonNull(req.getPid(), 500, "创建经销商、业务员、促销员时，必须选择上级");
            pm = this.getParent(req.getPid());
        }

        // 网点类型的商户
        if (GroupRoleEnum.WD.getCode().equals(req.getRoleAlias())) {
            Asserts.assertStringNotBlank(req.getIsRefrigerator(), 500, "是否有光明冰箱不能为空");
            Asserts.assertTrue(Arrays.asList("Y", "N").contains(req.getIsRefrigerator()), 500, "是否有光明冰箱只能是Y|N");
            // update detail
            MerchantDetail bean = new MerchantDetail();
            bean.setIsRefrigerator(req.getIsRefrigerator());
            LambdaQueryWrapper<MerchantDetail> queryWrapper = Wrappers.lambdaQuery(MerchantDetail.class)
                    .eq(MerchantDetail::getMerchantId, req.getMerchantId());
            Asserts.assertTrue(merchantDetailMapper.update(bean, queryWrapper) > 0,
                    500, "修改网点信息失败");
        }

        // update
        MD5 md5 = MD5.create();
        Merchant merchant = new Merchant();
        merchant.setId(req.getMerchantId());
        merchant.setUpdateTime(LocalDateTime.now());
        merchant.setRoleAlias(req.getRoleAlias());
        merchant.setRoleName(GroupRoleEnum.getByCode(req.getRoleAlias()).getDescription());
        merchant.setMerchantName(req.getMerchantName());
        merchant.setMobile(req.getMobile());
        merchant.setPassword(md5.digestHex(md5.digestHex(req.getPassword())));
        merchant.setContacts(req.getContacts());
        merchant.setProvince(province.getName());
        merchant.setCity(city.getName());
        merchant.setDistrict(district.getName());
        merchant.setProvinceId(req.getProvinceId());
        merchant.setCityId(req.getCityId());
        merchant.setDistrictId(req.getDistrictId());
        merchant.setAddress(req.getAddress());
        merchant.setPid(pm == null ? 0 : pm.getId());
        merchant.setPMerchantName(pm == null ? null : pm.getMerchantName());
        merchant.setPidPath(this.generatePidPath(req.getPid()));
        int rows = merchantMapper.updateById(merchant);
        Asserts.assertTrue(rows > 0, 500, "编辑商户信息失败");

        // 修改pMerchantName
        if (!existMerchant.getMerchantName().equals(req.getMerchantName())) {
            Merchant entity = new Merchant();
            entity.setPMerchantName(req.getMerchantName());
            merchantMapper.update(entity, Wrappers.lambdaQuery(Merchant.class).eq(Merchant::getPid, req.getMerchantId()));
        }

        return null;
    }

    @Override
    public IPage<MerchantResult> pageQueryMerchants(QueryMerchantReq req) {
        QueryMerchantDetail query = BeanUtil.convert(req, QueryMerchantDetail.class);
        int count = customMerchantMapper.countMerchantInfo(query);
        if (count <= 0) {
            return BeanUtil.emptyPageData(req.getPageIndex(), req.getPageSize());
        }
        int startIdx = (req.getPageIndex() - 1) * req.getPageSize();
        List<MerchantResult> list = customMerchantMapper.pageQueryMerchantInfo(query, startIdx, req.getPageSize())
                .stream().map((m) -> BeanUtil.convert(m, MerchantResult.class)).collect(Collectors.toList());
        IPage<MerchantResult> page = new Page<>(req.getPageIndex(), req.getPageSize(), count);
        page.setRecords(list);
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void updateMerchantIsEnable(Integer merchantId, boolean isEnable) throws ApiException {
        Merchant merchant = new Merchant();
        merchant.setId(merchantId);
        merchant.setIsEnable(isEnable ? "Y" : "N");
        int rows = merchantMapper.updateById(merchant);
        Asserts.assertTrue(rows > 0, 500, "修改失败");
        return null;
    }

    private String generatePidPath(Integer pid) {
        if (pid == null) {
            return null;
        }
        LinkedList<Integer> path = new LinkedList<>();
        path.addFirst(pid);
        Merchant parent = this.getParent(pid);
        while (parent.getPid() != 0) {
            path.addFirst(parent.getId());
            parent = this.getParent(parent.getPid());
        }
        path.addFirst(parent.getId());
        return StringUtils.join(path, ",");
    }

    private Merchant getParent(Integer pid) throws ApiException {
        Merchant pm = merchantMapper.selectById(pid);
        Asserts.assertNonNull(pm, 500, "上级商户" + pid + "不存在");
        Asserts.assertTrue("Y".equalsIgnoreCase(pm.getIsEnable()), 500, "上级商户" + pid + "已被禁用");
        Asserts.assertTrue("N".equalsIgnoreCase(pm.getIsDelete()), 500, "上级商户" + pid + "已被删除");
        return pm;
    }

}