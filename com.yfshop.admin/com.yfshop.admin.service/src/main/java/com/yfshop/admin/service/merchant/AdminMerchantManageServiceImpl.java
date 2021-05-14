package com.yfshop.admin.service.merchant;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.merchant.AdminMerchantManageService;
import com.yfshop.admin.api.merchant.request.CreateMerchantReq;
import com.yfshop.admin.api.merchant.request.QueryMerchantReq;
import com.yfshop.admin.api.merchant.request.UpdateMerchantReq;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.admin.dao.MerchantDao;
import com.yfshop.admin.dto.query.QueryMerchantDetail;
import com.yfshop.code.mapper.MerchantDetailMapper;
import com.yfshop.code.mapper.MerchantLogMapper;
import com.yfshop.code.mapper.RegionMapper;
import com.yfshop.code.model.Merchant;
import com.yfshop.code.model.MerchantDetail;
import com.yfshop.code.model.MerchantLog;
import com.yfshop.code.model.Region;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.dao.DuplicateKeyException;
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

    private static final List<String> CANDIDATE_CHECK_ROLES = Arrays.asList(
            GroupRoleEnum.ZB.getCode(), GroupRoleEnum.FGS.getCode(), GroupRoleEnum.SQ.getCode(),
            GroupRoleEnum.JXS.getCode(), GroupRoleEnum.YWY.getCode(), GroupRoleEnum.FXS.getCode(),
            GroupRoleEnum.CXY.getCode(), GroupRoleEnum.WD.getCode()
    );

    @Resource
    private RegionMapper regionMapper;
    @Resource
    private com.yfshop.code.mapper.MerchantMapper merchantMapper;
    @Resource
    private MerchantDetailMapper merchantDetailMapper;
    @Resource
    private MerchantDao customMerchantMapper;
    @Resource
    private MerchantLogMapper merchantLogMapper;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createMerchant(Integer loginedMerchantId, CreateMerchantReq req) throws ApiException {
        Region province = regionMapper.selectById(req.getProvinceId());
        Asserts.assertNonNull(province, 500, "省份信息不存在");
        Region city = regionMapper.selectById(req.getCityId());
        Asserts.assertNonNull(city, 500, "市信息不存在");
        Region district = regionMapper.selectById(req.getDistrictId());
        Asserts.assertNonNull(district, 500, "区信息不存在");
        Merchant currentLoginMerchant = merchantMapper.selectById(loginedMerchantId);
        Asserts.assertNonNull(currentLoginMerchant, 500, "当前登录商户不存在");
        Asserts.assertTrue("Y".equalsIgnoreCase(currentLoginMerchant.getIsEnable()), 500, "当前登录商户已被禁用");
        Asserts.assertTrue("N".equalsIgnoreCase(currentLoginMerchant.getIsDelete()), 500, "当前登录商户已被删除");
        GroupRoleEnum createMerchantRole = GroupRoleEnum.getByCode(req.getRoleAlias());

        // 查询上级
        Merchant pm = getParentMerchantThenCheck(currentLoginMerchant, req.getPid(), req.getRoleAlias());

        // create
        Merchant merchant = new Merchant();
        merchant.setCreateTime(LocalDateTime.now());
        merchant.setUpdateTime(LocalDateTime.now());
        merchant.setOpenId(null);
        merchant.setRoleAlias(req.getRoleAlias());
        merchant.setRoleName(createMerchantRole.getDescription());
        merchant.setMerchantName(req.getMerchantName());
        merchant.setMobile(req.getMobile());
        merchant.setPassword(SecureUtil.md5(req.getPassword()));
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
        merchant.setPid(pm.getId());
        merchant.setPMerchantName(pm.getMerchantName());
        try {
            merchantMapper.insert(merchant);
        } catch (DuplicateKeyException e) {
            throw new ApiException(500, "手机号" + merchant.getMobile() + "已经注册过了");
        }
        merchant.setPidPath(this.generatePidPath(pm.getId(), merchant.getId()));
        merchantMapper.updateById(merchant);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void updateMerchant(Integer merchantId, UpdateMerchantReq req) throws ApiException {
        Merchant existMerchant = merchantMapper.selectById(req.getMerchantId());
        Asserts.assertNonNull(existMerchant, 500, "编辑的商户不存在");
        Region province = regionMapper.selectById(req.getProvinceId());
        Asserts.assertNonNull(province, 500, "省份信息不存在");
        Region city = regionMapper.selectById(req.getCityId());
        Asserts.assertNonNull(city, 500, "市信息不存在");
        Region district = regionMapper.selectById(req.getDistrictId());
        Asserts.assertNonNull(district, 500, "区信息不存在");
        Merchant currentLoginMerchant = merchantMapper.selectById(merchantId);
        Asserts.assertNonNull(currentLoginMerchant, 500, "当前登录商户不存在");
        Asserts.assertTrue("Y".equalsIgnoreCase(currentLoginMerchant.getIsEnable()), 500, "当前登录商户已被禁用");
        Asserts.assertTrue("N".equalsIgnoreCase(currentLoginMerchant.getIsDelete()), 500, "当前登录商户已被删除");

        // 查询上级
        Merchant pm = getParentMerchantThenCheck(currentLoginMerchant, req.getPid(), req.getRoleAlias());

        // 网点类型的商户
        if (GroupRoleEnum.WD.getCode().equals(req.getRoleAlias())) {
            Asserts.assertStringNotBlank(req.getIsRefrigerator(), 500, "是否有光明冰箱不能为空");
            Asserts.assertTrue(Arrays.asList("Y", "N").contains(req.getIsRefrigerator()), 500, "是否有光明冰箱只能是Y|N");
            // update detail
            MerchantDetail bean = new MerchantDetail();
            bean.setIsRefrigerator(req.getIsRefrigerator());
            bean.setHeadImage(req.getHeadImage());
            LambdaQueryWrapper<MerchantDetail> queryWrapper = Wrappers.lambdaQuery(MerchantDetail.class)
                    .eq(MerchantDetail::getMerchantId, req.getMerchantId());
            Asserts.assertTrue(merchantDetailMapper.update(bean, queryWrapper) > 0,
                    500, "修改网点信息失败");
        }

        // update
        Merchant merchant = new Merchant();
        merchant.setId(req.getMerchantId());
        merchant.setUpdateTime(LocalDateTime.now());
        merchant.setRoleAlias(req.getRoleAlias());
        merchant.setRoleName(GroupRoleEnum.getByCode(req.getRoleAlias()).getDescription());
        merchant.setMerchantName(req.getMerchantName());
        if (!existMerchant.getMobile().equals(req.getMobile())) {
            merchant.setMobile(req.getMobile());
        }
        if (StringUtils.isNotBlank(req.getPassword())) {
            merchant.setPassword(SecureUtil.md5(req.getPassword()));
        }
        merchant.setContacts(req.getContacts());
        merchant.setProvince(province.getName());
        merchant.setCity(city.getName());
        merchant.setDistrict(district.getName());
        merchant.setProvinceId(req.getProvinceId());
        merchant.setCityId(req.getCityId());
        merchant.setDistrictId(req.getDistrictId());
        merchant.setAddress(req.getAddress());
        merchant.setPid(pm.getId());
        merchant.setPMerchantName(pm.getMerchantName());
        merchant.setPidPath(this.generatePidPath(pm.getId(), merchant.getId()));
        try {
            int rows = merchantMapper.updateById(merchant);
            Asserts.assertTrue(rows > 0, 500, "编辑商户信息失败");
        } catch (DuplicateKeyException e) {
            throw new ApiException(500, "手机号" + req.getMobile() + "已存在");
        }

        // 修改pMerchantName
        if (!existMerchant.getMerchantName().equals(req.getMerchantName())) {
            Merchant entity = new Merchant();
            entity.setPMerchantName(req.getMerchantName());
            merchantMapper.update(entity, Wrappers.lambdaQuery(Merchant.class).eq(Merchant::getPid, req.getMerchantId()));
        }
        Merchant newM = merchantMapper.selectById(existMerchant.getId());
        MerchantLog merchantLog = new MerchantLog();
        merchantLog.setMerchantId(existMerchant.getId());
        merchantLog.setOperatorId(req.getOperatorId());
        merchantLog.setBeforeData(JSONUtil.toJsonStr(existMerchant));
        merchantLog.setAfterData(JSONUtil.toJsonStr(newM));
        merchantLogMapper.insert(merchantLog);
        return null;
    }

    @Override
    public IPage<MerchantResult> pageQueryMerchants(Integer merchantId, QueryMerchantReq req) {
        Merchant loginMerchant = merchantMapper.selectById(merchantId);
        if (loginMerchant == null) {
            return BeanUtil.emptyPageData(req.getPageIndex(), req.getPageSize());
        }
        QueryMerchantDetail query = new QueryMerchantDetail();
        query.setStartCreateTime(req.getStartCreateTime());
        query.setEndCreateTime(req.getEndCreateTime());
        query.setMerchantId(req.getMerchantId());
        query.setMerchantName(StringUtils.isBlank(req.getMerchantName()) ? null : req.getMerchantName());
        query.setProvinceId(req.getProvinceId());
        query.setCityId(req.getCityId());
        query.setDistrictId(req.getDistrictId());
        query.setRoleAlias(StringUtils.isBlank(req.getRoleAlias()) ? null : req.getRoleAlias());
        query.setMobile(StringUtils.isBlank(req.getMobile()) ? null : req.getMobile());
        query.setContacts(StringUtils.isBlank(req.getContacts()) ? null : req.getContacts());
        query.setPMerchantName(StringUtils.isBlank(req.getPMerchantName()) ? null : req.getPMerchantName());
        query.setIsEnable(StringUtils.isBlank(req.getIsEnable()) ? null : req.getIsEnable());
        query.setIsRefrigerator(StringUtils.isBlank(req.getIsRefrigerator()) ? null : req.getIsRefrigerator());
        if (!loginMerchant.getRoleAlias().equals(GroupRoleEnum.SYS.getCode())) {
            query.setPid(loginMerchant.getId());
        }
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
    public Void updateMerchantIsEnable(Integer merchantId,Integer operatorId, boolean isEnable) throws ApiException {
        Merchant oldM = merchantMapper.selectById(merchantId);
        Merchant merchant = new Merchant();
        merchant.setId(merchantId);
        merchant.setIsEnable(isEnable ? "Y" : "N");
        int rows = merchantMapper.updateById(merchant);
        Asserts.assertTrue(rows > 0, 500, "修改失败");
        Merchant newM = merchantMapper.selectById(merchantId);
        MerchantLog merchantLog = new MerchantLog();
        merchantLog.setMerchantId(oldM.getId());
        merchantLog.setOperatorId(operatorId);
        merchantLog.setBeforeData(JSONUtil.toJsonStr(oldM));
        merchantLog.setAfterData(JSONUtil.toJsonStr(newM));
        merchantLogMapper.insert(merchantLog);
        return null;
    }

    @Override
    public IPage<MerchantResult> pageQueryMerchantsByPidAndRoleAlias(Integer merchantId, String roleAlias, String merchantName,
                                                                     Integer pageIndex, Integer pageSize) {
        if (merchantId == null || StringUtils.isBlank(roleAlias)) {
            return BeanUtil.emptyPageData(pageIndex, pageSize);
        }
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            return BeanUtil.emptyPageData(pageIndex, pageSize);
        }
        List<Merchant> list;
        if (merchant.getRoleAlias().equals(GroupRoleEnum.SYS.getCode())) {
            list = merchantMapper.selectList(Wrappers.lambdaQuery(Merchant.class).eq(Merchant::getRoleAlias, roleAlias)
                    .like(StringUtils.isNotBlank(merchantName), Merchant::getMerchantName, merchantName)
            );
            //Page<Merchant> page = merchantMapper.selectPage(new Page<>(pageIndex, pageSize),
            //        Wrappers.lambdaQuery(Merchant.class).eq(Merchant::getRoleAlias, roleAlias)
            //                .like(StringUtils.isNotBlank(merchantName), Merchant::getMerchantName, merchantName)
            //);
            //return BeanUtil.iPageConvert(page, MerchantResult.class);
        } else {
            list = merchantMapper.selectList(Wrappers.lambdaQuery(Merchant.class).eq(Merchant::getPid, merchantId)
                    .eq(Merchant::getRoleAlias, roleAlias).like(StringUtils.isNotBlank(merchantName), Merchant::getMerchantName, merchantName)
            );
            //Page<Merchant> page = merchantMapper.selectPage(new Page<>(pageIndex, pageSize),
            //        Wrappers.lambdaQuery(Merchant.class).eq(Merchant::getPid, merchantId).eq(Merchant::getRoleAlias, roleAlias)
            //                .like(StringUtils.isNotBlank(merchantName), Merchant::getMerchantName, merchantName)
            //);
            //return BeanUtil.iPageConvert(page, MerchantResult.class);
        }
        Page<MerchantResult> page = new Page<>(pageIndex, pageSize);
        page.setRecords(BeanUtil.convertList(list, MerchantResult.class));
        page.setTotal(list.size());
        return page;
    }

    private String generatePidPath(Integer pid, Integer id) {
        if (pid == null || pid == 0) {
            return null;
        }
        LinkedList<Integer> path = new LinkedList<>();
        path.addFirst(id);
        Merchant parent = this.getParent(pid);
        while (parent.getPid() != null && parent.getPid() != 0) {
            path.addFirst(parent.getId());
            parent = this.getParent(parent.getPid());
        }
        path.addFirst(parent.getId());
        return StringUtils.join(path, ".") + ".";
    }

    private Merchant getParent(Integer pid) throws ApiException {
        Merchant pm = merchantMapper.selectById(pid);
        Asserts.assertNonNull(pm, 500, "上级商户" + pid + "不存在");
        Asserts.assertTrue("Y".equalsIgnoreCase(pm.getIsEnable()), 500, "上级商户" + pid + "已被禁用");
        Asserts.assertTrue("N".equalsIgnoreCase(pm.getIsDelete()), 500, "上级商户" + pid + "已被删除");
        return pm;
    }

    private Merchant getParentMerchantThenCheck(Merchant loginMerchant, Integer pid, String roleAlias) {
        GroupRoleEnum loginMerchantRole = GroupRoleEnum.getByCode(loginMerchant.getRoleAlias());
        GroupRoleEnum createMerchantRole = GroupRoleEnum.getByCode(roleAlias);
        // 查上级
        Merchant pm;
        if (loginMerchantRole.getCode().equals(GroupRoleEnum.SYS.getCode())) {
            Asserts.assertNonNull(pid, 500, "上级商户不能为空");
            pm = getParent(pid);
            if (GroupRoleEnum.getByCode(pm.getRoleAlias()) == GroupRoleEnum.ZB && createMerchantRole == GroupRoleEnum.JXS) {
                // 总部建经销商时，必须要有二级
                boolean bool = pm.getRoleAlias().equals(GroupRoleEnum.FGS.getCode()) || pm.getRoleAlias().equals(GroupRoleEnum.SQ.getCode());
                Asserts.assertTrue(bool, 500, "经销商的上级必须是分公司或省区");
            }
        } else {
            Integer loginMerchantId = loginMerchant.getId();
            if (loginMerchantRole == GroupRoleEnum.ZB && createMerchantRole == GroupRoleEnum.JXS) {
                Asserts.assertNonNull(pid, 500, "上级商户ID不能为空");
                // 总部建经销商时，必须要有二级
                LambdaQueryWrapper<Merchant> query = Wrappers.lambdaQuery(Merchant.class)
                        .eq(Merchant::getPid, loginMerchantId).eq(Merchant::getId, pid)
                        .in(Merchant::getRoleAlias, Arrays.asList(GroupRoleEnum.FGS.getCode(), GroupRoleEnum.SQ.getCode()));
                pm = merchantMapper.selectOne(query);
                Asserts.assertNonNull(pm, 500, "经销商的上级必须是分公司或省区");
            } else if (pid != null && !pid.equals(loginMerchantId)) {
                // 验证选择的pid是否正确，这个上级必须是当前登录商户的下级
                pm = merchantMapper.selectOne(Wrappers.lambdaQuery(Merchant.class)
                        .eq(Merchant::getPid, loginMerchant.getId()).eq(Merchant::getId, pid));
                Asserts.assertNonNull(pm, 500, "错误的上级pid(" + pid + ")");
            } else {
                // 使用当前商户作为上级
                pm = this.getParent(loginMerchantId);
            }
        }
        // 是否平级或越级创建
        Asserts.assertTrue(GroupRoleEnum.getByCode(pm.getRoleAlias()).getLevel() < createMerchantRole.getLevel(),
                500, "不能越级创建商户");
        Asserts.assertTrue("Y".equalsIgnoreCase(pm.getIsEnable()), 500, "上级商户" + pid + "已被禁用");
        Asserts.assertTrue("N".equalsIgnoreCase(pm.getIsDelete()), 500, "上级商户" + pid + "已被删除");
        return pm;
    }

}
