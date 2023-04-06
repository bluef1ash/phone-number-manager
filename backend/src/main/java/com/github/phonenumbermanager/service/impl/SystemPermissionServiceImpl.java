package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.CompanyPermission;
import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.mapper.CompanyMapper;
import com.github.phonenumbermanager.mapper.CompanyPermissionMapper;
import com.github.phonenumbermanager.mapper.SystemPermissionMapper;
import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.vo.MenuVO;
import com.github.phonenumbermanager.vo.SelectListVO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;

/**
 * 系统权限业务实现
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Service
public class SystemPermissionServiceImpl extends BaseServiceImpl<SystemPermissionMapper, SystemPermission>
    implements SystemPermissionService {
    private final CompanyPermissionMapper companyPermissionMapper;
    private final CompanyMapper companyMapper;

    @Cacheable(cacheNames = SystemConstant.SYSTEM_PERMISSIONS_KEY + "${{'ttl': -1}}")
    @Override
    public List<SystemPermission> listAll() {
        return baseMapper.selectList(new LambdaQueryWrapper<SystemPermission>().select(SystemPermission::getId,
            SystemPermission::getMenuType, SystemPermission::getFunctionName, SystemPermission::getHttpMethods,
            SystemPermission::getIsDisplay, SystemPermission::getMenuType, SystemPermission::getName,
            SystemPermission::getParentId, SystemPermission::getUri));
    }

    @Override
    public List<SystemPermission> listByCompanyId(Long companyId) {
        return baseMapper.selectByCompanyId(companyId);
    }

    @Cacheable(cacheNames = SystemConstant.SYSTEM_MENU_KEY + "${{'ttl': 60 * 60 * 24 * 7}}",
        key = "#currentSystemUserId")
    @Override
    public Map<String, Object> listMenu(List<Company> companies, Long currentSystemUserId) {
        Map<String, Object> menuMap = new HashMap<>(2);
        List<MenuVO> menuVOs = new ArrayList<>();
        Set<String> components = new HashSet<>();
        List<SystemPermission> systemPermissionAll = listAll();
        Map<String, String> fieldMappings = new HashMap<>(2);
        fieldMappings.put("uri", "path");
        fieldMappings.put("functionName", "component");
        CopyOptions copyOptions = CopyOptions.create();
        copyOptions.setFieldMapping(fieldMappings);
        List<SystemPermission> baseSystemPermissions;
        if (companies == null) {
            // 超级管理员
            baseSystemPermissions = systemPermissionAll.stream()
                .filter(systemPermission -> systemPermission.getParentId() == 0L).collect(Collectors.toList());
        } else if (companies.isEmpty()) {
            menuMap.put("menuData", menuVOs);
            menuMap.put("components", components);
            return menuMap;
        } else {
            // 需要权限验证的用户
            baseSystemPermissions = companies.stream().map(Company::getSystemPermissions).flatMap(Collection::stream)
                .distinct().collect(Collectors.toList());
            if (baseSystemPermissions.isEmpty()) {
                baseSystemPermissions =
                    getPrevSystemPermissions(companies.stream().map(Company::getId).collect(Collectors.toList()));
            }
        }
        menuVOs = baseSystemPermissions.stream().map(systemPermission -> {
            MenuVO menuVo = new MenuVO();
            BeanUtil.copyProperties(systemPermission, menuVo, copyOptions);
            menuVo.setKey(systemPermission.getId().toString()).setHideInMenu(!systemPermission.getIsDisplay());
            components.add(menuVo.getComponent());
            return menuVo;
        }).collect(Collectors.toList());
        treeMenus(menuVOs, components, systemPermissionAll, copyOptions);
        menuMap.put("menuData", menuVOs);
        menuMap.put("components", components);
        return menuMap;
    }

    @Override
    public IPage<SystemPermission> treePage(Integer current, Integer pageSize, QueryWrapper<SystemPermission> wrapper) {
        Page<SystemPermission> page = new Page<>(current, pageSize);
        List<SystemPermission> systemPermissions = baseMapper.selectList(wrapper);
        List<SystemPermission> treeSystemPermissionList = systemPermissions.stream()
            .filter(systemPermission -> systemPermission.getParentId() == 0L).collect(Collectors.toList());
        if (treeSystemPermissionList.size() == 0) {
            treeSystemPermissionList = systemPermissions;
        } else {
            treeSystemPermissionList
                .forEach(systemPermission -> treeSystemPermissions(systemPermission, null, systemPermissions, null));
        }
        List<SystemPermission> pageList = new ArrayList<>();
        int currId = current > 1 ? (current - 1) * pageSize : 0;
        for (int i = 0; i < pageSize && i < treeSystemPermissionList.size() - currId; i++) {
            pageList.add(treeSystemPermissionList.get(currId + i));
        }
        page.setSize(pageSize);
        page.setCurrent(current);
        page.setTotal(treeSystemPermissionList.size());
        page.setPages(treeSystemPermissionList.size() % 10 == 0 ? treeSystemPermissionList.size() / 10
            : treeSystemPermissionList.size() / 10 + 1);
        page.setRecords(pageList);
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        companyPermissionMapper
            .delete(new LambdaQueryWrapper<CompanyPermission>().eq(CompanyPermission::getPermissionId, id));
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public List<SelectListVO> treeSelectList(Long[] parentIds) {
        List<SystemPermission> systemPermissions = baseMapper.selectList(null);
        List<Long> parentIdAll = systemPermissions.stream().map(SystemPermission::getParentId).toList();
        return systemPermissions.stream()
            .filter(systemPermission -> ArrayUtil.contains(parentIds, systemPermission.getParentId()))
            .map(systemPermission -> {
                SelectListVO selectListVo = new SelectListVO();
                selectListVo.setTitle(systemPermission.getName()).setLabel(systemPermission.getName())
                    .setValue(systemPermission.getId()).setIsLeaf(!parentIdAll.contains(systemPermission.getId()));
                return selectListVo;
            }).collect(Collectors.toList());
    }

    /**
     * 递归菜单
     *
     * @param menuVOs
     *            整理后的菜单
     * @param components
     *            权限名称列表
     * @param systemPermissionAll
     *            全部系统用户权限对象集合
     * @param copyOptions
     *            复制选项
     */
    private void treeMenus(List<MenuVO> menuVOs, Set<String> components, List<SystemPermission> systemPermissionAll,
        CopyOptions copyOptions) {
        menuVOs.forEach(menuVO -> {
            List<MenuVO> menuVOList = systemPermissionAll.stream()
                .filter(systemPermission -> systemPermission.getParentId().equals(menuVO.getId())
                    && systemPermission.getIsDisplay())
                .map(systemPermission -> {
                    MenuVO menu = new MenuVO();
                    BeanUtil.copyProperties(systemPermission, menu, copyOptions);
                    menu.setKey(systemPermission.getId().toString()).setHideInMenu(!systemPermission.getIsDisplay());
                    components.add(menu.getComponent());
                    return menu;
                }).collect(Collectors.toList());
            menuVO.setRoutes(menuVOList);
            treeMenus(menuVOList, components, systemPermissionAll, copyOptions);
        });
    }

    /**
     * 递归系统权限
     *
     * @param systemPermission
     *            顶级系统权限
     * @param selectListVo
     *            顶级系统权限表单
     * @param systemPermissions
     *            全部系统权限集合
     * @param parentIds
     *            全部父级编号集合
     */
    private void treeSystemPermissions(SystemPermission systemPermission, SelectListVO selectListVo,
        List<SystemPermission> systemPermissions, List<Long> parentIds) {
        systemPermissions.forEach(permission -> {
            Long id;
            if (systemPermission == null) {
                id = selectListVo.getValue();
            } else {
                id = systemPermission.getId();
            }
            if (id.equals(permission.getParentId())) {
                SelectListVO selectList = null;
                if (systemPermission == null) {
                    if (selectListVo.getChildren() == null) {
                        selectListVo.setChildren(new ArrayList<>());
                    }
                    selectList = new SelectListVO();
                    selectList.setTitle(permission.getName()).setLabel(permission.getName())
                        .setValue(permission.getId())
                        .setIsLeaf(parentIds == null || !parentIds.contains(permission.getId()));
                    selectListVo.getChildren().add(selectList);
                } else {
                    if (systemPermission.getChildren() == null) {
                        systemPermission.setChildren(new ArrayList<>());
                    }
                    systemPermission.getChildren().add(permission);
                }
                treeSystemPermissions(permission, selectList, systemPermissions, parentIds);
            }
        });
    }

    /**
     * 获取上级单位存在权限
     *
     * @param companyIds
     *            单位编号集合
     * @return 系统权限集合
     */
    private List<SystemPermission> getPrevSystemPermissions(List<Long> companyIds) {
        List<SystemPermission> systemPermissions = null;
        List<SystemPermission> systemPermissionList = baseMapper.selectListByIsDisplay(true);
        List<Company> companyAll = companyMapper.selectList(null);
        do {
            if (companyIds == null || companyIds.isEmpty()) {
                break;
            }
            systemPermissions = companyIds.stream()
                .map(id -> systemPermissionList.stream()
                    .filter(systemPermission -> systemPermission.getCompanies().stream()
                        .anyMatch(company -> id.equals(company.getId())))
                    .collect(Collectors.toList()))
                .flatMap(Collection::stream).collect(Collectors.toList());
            companyIds = companyIds.stream()
                .map(companyId -> companyAll.stream().filter(company -> companyId.equals(company.getId())).findFirst()
                    .orElse(null))
                .filter(Objects::nonNull).map(Company::getParentId)
                .map(parentId -> companyAll.stream().filter(c -> parentId.equals(c.getId())).findFirst().orElse(null))
                .filter(Objects::nonNull).map(Company::getId).collect(Collectors.toList());
        } while (systemPermissions.isEmpty());
        return systemPermissions;
    }
}
