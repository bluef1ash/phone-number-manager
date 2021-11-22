package com.github.phonenumbermanager.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.CompanyPermission;
import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.mapper.SystemPermissionMapper;
import com.github.phonenumbermanager.service.CompanyPermissionService;
import com.github.phonenumbermanager.service.SystemPermissionService;

/**
 * 系统权限业务实现
 *
 * @author 廿二月的天
 */
@Service("systemPermissionService")
public class SystemPermissionServiceImpl extends BaseServiceImpl<SystemPermissionMapper, SystemPermission>
    implements SystemPermissionService {
    @Resource
    private CompanyPermissionService companyPermissionService;

    @Override
    public List<SystemPermission> listByCompanyId(Long companyId) {
        return baseMapper.selectByCompanyId(companyId);
    }

    @Cacheable(value = "privilege_menu", key = "#systemUser.id")
    @Override
    public List<SystemPermission> listMenu(Boolean display, List<Company> companies) {
        List<SystemPermission> systemPermissions =
            baseMapper.selectListByCompanyIds(companies.stream().map(Company::getId).collect(Collectors.toList()));
        return systemPermissions.stream()
            .map(systemPermission -> treePrivileges(systemPermission, baseMapper.selectList(null), display))
            .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCorrelationById(Long id) {
        QueryWrapper<CompanyPermission> wrapper = new QueryWrapper<>();
        wrapper.eq("permission_id", id);
        return baseMapper.deleteById(id) > 0 && companyPermissionService.remove(wrapper);
    }

    /**
     * 有序排列用户权限
     *
     * @param systemPermission
     *            上级对象
     * @param systemPermissions
     *            需要排列的系统用户权限对象集合
     * @param display
     *            是否在导航栏中显示
     * @return 排列成功的系统用户权限对象集合
     */
    private SystemPermission treePrivileges(SystemPermission systemPermission, List<SystemPermission> systemPermissions,
        boolean display) {
        for (SystemPermission sp : systemPermissions) {
            if (sp.getParentId().equals(systemPermission.getParentId()) && display == sp.getIsDisplay()) {
                systemPermission.getChildren().add(treePrivileges(sp, systemPermissions, display));
            }
        }
        return systemPermission;
    }
}
