package com.github.phonenumbermanager.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.CompanyPermission;
import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.mapper.SystemPermissionMapper;
import com.github.phonenumbermanager.service.CompanyPermissionService;
import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.vo.MenuVo;
import com.github.phonenumbermanager.vo.SelectListVo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统权限业务实现
 *
 * @author 廿二月的天
 */
@Slf4j
@Service("systemPermissionService")
public class SystemPermissionServiceImpl extends BaseServiceImpl<SystemPermissionMapper, SystemPermission>
    implements SystemPermissionService {
    @Resource
    private CompanyPermissionService companyPermissionService;

    @Override
    public List<SystemPermission> listByCompanyId(Long companyId) {
        return baseMapper.selectByCompanyId(companyId);
    }

    @Override
    public List<SystemPermission> listByCompanyIds(List<Long> companyIds) {
        return baseMapper.selectByCompanyIds(companyIds);
    }

    @Override
    public List<MenuVo> listMenu(Boolean display, List<Company> companies) {
        List<Long> companyIds = null;
        if (companies != null && companies.get(0).getId() != null) {
            companyIds = companies.stream().map(Company::getId).collect(Collectors.toList());
        }
        List<SystemPermission> systemPermissions = baseMapper.selectListByCompanyIds(companyIds);
        Map<String, String> fieldMappings = new HashMap<>(3);
        fieldMappings.put("uri", "path");
        CopyOptions copyOptions = CopyOptions.create();
        copyOptions.setFieldMapping(fieldMappings);
        return treeMenus(systemPermissions, 0L, display, copyOptions);
    }

    @Override
    public IPage<SystemPermission> treePage(Integer current, Integer pageSize, QueryWrapper<SystemPermission> wrapper) {
        Page<SystemPermission> page = new Page<>(current, pageSize);
        List<SystemPermission> systemPermissions = baseMapper.selectList(wrapper);
        List<SystemPermission> treeSystemPermissionList =
            systemPermissions.stream().filter(systemPermission -> systemPermission.getParentId() == 0L)
                .peek(systemPermission -> treeSystemPermissions(systemPermission, null, systemPermissions))
                .collect(Collectors.toList());
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
    public boolean removeCorrelationById(Long id) {
        QueryWrapper<CompanyPermission> wrapper = new QueryWrapper<>();
        wrapper.eq("permission_id", id);
        return baseMapper.deleteById(id) > 0 && companyPermissionService.remove(wrapper);
    }

    @Override
    public List<SelectListVo> treeSelectList() {
        List<SystemPermission> systemPermissions = baseMapper.selectList(null);
        return systemPermissions.stream().filter(systemPermission -> systemPermission.getParentId() == 0L)
            .map(systemPermission -> {
                SelectListVo selectListVo = new SelectListVo();
                selectListVo.setTitle(systemPermission.getName()).setValue(systemPermission.getId())
                    .setLevel(systemPermission.getLevel());
                treeSystemPermissions(null, selectListVo, systemPermissions);
                return selectListVo;
            }).collect(Collectors.toList());
    }

    /**
     * 递归菜单
     *
     * @param systemPermissions
     *            需要排列的系统用户权限对象集合
     * @param parentId
     *            上级编号
     * @param display
     *            是否在导航栏中显示
     * @param copyOptions
     *            复制选项
     * @return 整理后的菜单
     */
    private List<MenuVo> treeMenus(List<SystemPermission> systemPermissions, Long parentId, boolean display,
        CopyOptions copyOptions) {
        return systemPermissions.parallelStream().map(systemPermission -> {
            if (systemPermission.getParentId().equals(parentId) && display == systemPermission.getIsDisplay()) {
                MenuVo menu = new MenuVo();
                BeanUtil.copyProperties(systemPermission, menu, copyOptions);
                menu.setRoutes(treeMenus(systemPermissions, menu.getId(), display, copyOptions));
                return menu;
            }
            return null;
        }).filter(ObjectUtil::isNotNull).collect(Collectors.toList());
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
     */
    private void treeSystemPermissions(SystemPermission systemPermission, SelectListVo selectListVo,
        List<SystemPermission> systemPermissions) {
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
                    selectList.setTitle(permission.getName()).setValue(permission.getId())
                        .setLevel(permission.getLevel());
                    selectListVo.getChildren().add(selectList);
                } else {
                    if (systemPermission.getChildren() == null) {
                        systemPermission.setChildren(new ArrayList<>());
                    }
                    systemPermission.getChildren().add(permission);
                }
                treeSystemPermissions(permission, selectList, systemPermissions);
            }
        });
    }
}
