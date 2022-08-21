package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
import com.github.phonenumbermanager.util.RedisUtil;
import com.github.phonenumbermanager.vo.MenuVo;
import com.github.phonenumbermanager.vo.SelectListVo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
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
    private final RedisUtil redisUtil;

    @Override
    public List<SystemPermission> listCorrelation() {
        return baseMapper.selectCorrelationList();
    }

    @Override
    public List<SystemPermission> listByCompanyId(Long companyId) {
        return baseMapper.selectByCompanyId(companyId);
    }

    @Override
    public List<SystemPermission> listByCompanyIds(List<Long> companyIds) {
        return baseMapper.selectByCompanyIds(companyIds);
    }

    @Override
    public Map<String, Object> listMenu(Boolean display, List<Company> companies) {
        Map<String, Object> menuMap = new HashMap<>(2);
        List<Long> companyIds = null;
        if (companies != null) {
            companyIds = companies.stream().map(Company::getId).collect(Collectors.toList());
        }
        List<SystemPermission> systemPermissionAll =
            JSONUtil.parseArray(redisUtil.get(SystemConstant.SYSTEM_PERMISSIONS_KEY)).toList(SystemPermission.class);
        List<Company> companyAll = companyMapper.selectList(null);
        List<SystemPermission> systemPermissions =
            getPrevSystemPermissions(baseMapper, display, companyIds, companyAll);
        Map<String, String> fieldMappings = new HashMap<>(4);
        fieldMappings.put("uri", "path");
        fieldMappings.put("functionName", "component");
        CopyOptions copyOptions = CopyOptions.create();
        copyOptions.setFieldMapping(fieldMappings);
        List<MenuVo> menuVos = new ArrayList<>();
        Set<String> components = new HashSet<>();
        for (SystemPermission systemPermission : systemPermissions) {
            MenuVo menuVo = new MenuVo();
            BeanUtil.copyProperties(systemPermission, menuVo, copyOptions);
            menuVo.setKey(systemPermission.getId().toString()).setHideInMenu(!systemPermission.getIsDisplay());
            menuVos.add(menuVo);
            components.add(menuVo.getComponent());
        }
        treeMenus(menuVos, components, systemPermissionAll, copyOptions);
        menuMap.put("menuData", menuVos);
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
    public List<SelectListVo> treeSelectList(Long[] parentIds) {
        List<SystemPermission> systemPermissions = baseMapper.selectList(null);
        List<Long> parentIdAll =
            systemPermissions.stream().map(SystemPermission::getParentId).collect(Collectors.toList());
        return systemPermissions.stream()
            .filter(systemPermission -> ArrayUtil.contains(parentIds, systemPermission.getParentId()))
            .map(systemPermission -> {
                SelectListVo selectListVo = new SelectListVo();
                selectListVo.setTitle(systemPermission.getName()).setLabel(systemPermission.getName())
                    .setValue(systemPermission.getId()).setIsLeaf(!parentIdAll.contains(systemPermission.getId()));
                return selectListVo;
            }).collect(Collectors.toList());
    }

    /**
     * 递归菜单
     *
     * @param menuVos
     *            整理后的菜单
     * @param components
     *            权限名称列表
     * @param systemPermissionAll
     *            全部系统用户权限对象集合
     * @param copyOptions
     *            复制选项
     */
    private void treeMenus(List<MenuVo> menuVos, Set<String> components, List<SystemPermission> systemPermissionAll,
        CopyOptions copyOptions) {
        menuVos.forEach(menuVo -> {
            List<MenuVo> menuVoList = systemPermissionAll.stream()
                .filter(systemPermission -> systemPermission.getParentId().equals(menuVo.getId())
                    && systemPermission.getIsDisplay())
                .map(systemPermission -> {
                    MenuVo menu = new MenuVo();
                    BeanUtil.copyProperties(systemPermission, menu, copyOptions);
                    menu.setKey(systemPermission.getId().toString()).setHideInMenu(!systemPermission.getIsDisplay());
                    components.add(menu.getComponent());
                    return menu;
                }).collect(Collectors.toList());
            menuVo.setRoutes(menuVoList);
            treeMenus(menuVoList, components, systemPermissionAll, copyOptions);
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
    private void treeSystemPermissions(SystemPermission systemPermission, SelectListVo selectListVo,
        List<SystemPermission> systemPermissions, List<Long> parentIds) {
        systemPermissions.forEach(permission -> {
            Long id;
            if (systemPermission == null) {
                id = selectListVo.getValue();
            } else {
                id = systemPermission.getId();
            }
            if (id.equals(permission.getParentId())) {
                SelectListVo selectList = null;
                if (systemPermission == null) {
                    if (selectListVo.getChildren() == null) {
                        selectListVo.setChildren(new ArrayList<>());
                    }
                    selectList = new SelectListVo();
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
}
