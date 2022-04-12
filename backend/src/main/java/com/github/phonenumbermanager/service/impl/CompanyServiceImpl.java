package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.*;
import com.github.phonenumbermanager.service.CompanyExtraService;
import com.github.phonenumbermanager.service.CompanyService;
import com.github.phonenumbermanager.vo.SelectListVo;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * 单位业务实现
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Service
public class CompanyServiceImpl extends BaseServiceImpl<CompanyMapper, Company> implements CompanyService {
    private final CommunityResidentMapper communityResidentMapper;
    private final DormitoryManagerMapper dormitoryManagerMapper;
    private final PhoneNumberMapper phoneNumberMapper;
    private final CompanyPhoneNumberMapper companyPhoneNumberMapper;
    private final CompanyExtraService companyExtraService;
    private final CompanyPermissionMapper companyPermissionMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(Company entity) {
        baseMapper.insert(entity);
        if (entity.getCompanyExtras() != null && !entity.getCompanyExtras().isEmpty()) {
            entity.getCompanyExtras().forEach(companyExtra -> companyExtra.setCompanyId(entity.getId()));
            companyExtraService.saveBatch(entity.getCompanyExtras());
        }
        if (entity.getSystemPermissions() != null && !entity.getSystemPermissions().isEmpty()) {
            companyPermissionMapper.insertBatchSomeColumn(entity
                .getSystemPermissions().stream().map(systemPermission -> new CompanyPermission()
                    .setCompanyId(entity.getId()).setPermissionId(systemPermission.getId()))
                .collect(Collectors.toList()));
        }
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        return companyPhoneNumberMapper.insertBatchSomeColumn(companyPhoneNumbersHandler(entity)) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(Company entity) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Company>().eq(Company::getParentId, entity.getId())) > 0
            || entity.getId().equals(entity.getParentId())) {
            throw new BusinessException("上级单位不允许选择自己或已经是下级的单位！");
        }
        boolean isSuccess = baseMapper.updateById(entity) > 0;
        if (entity.getSystemPermissions() != null && !entity.getSystemPermissions().isEmpty()) {
            companyPermissionMapper.delete(
                new LambdaQueryWrapper<CompanyPermission>().eq(CompanyPermission::getCompanyId, entity.getId()));
            companyPermissionMapper.insertBatchSomeColumn(entity
                .getSystemPermissions().stream().map(systemPermission -> new CompanyPermission()
                    .setCompanyId(entity.getId()).setPermissionId(systemPermission.getId()))
                .collect(Collectors.toList()));
        }
        if (entity.getCompanyExtras() != null && !entity.getCompanyExtras().isEmpty()) {
            entity.getCompanyExtras().forEach(companyExtra -> companyExtra.setCompanyId(entity.getId()));
            companyExtraService.updateBatchById(entity.getCompanyExtras());
        }
        if (phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers()) > 0) {
            companyPhoneNumberMapper.delete(
                new LambdaQueryWrapper<CompanyPhoneNumber>().eq(CompanyPhoneNumber::getCompanyId, entity.getId()));
        }
        companyPhoneNumberMapper.insertIgnoreBatchSomeColumn(companyPhoneNumbersHandler(entity));
        return isSuccess;
    }

    @Override
    public Company getCorrelation(Long id) {
        return baseMapper.selectCorrelationById(id);
    }

    @Override
    public IPage<Company> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize,
        JSONObject search, JSONObject sort) {
        IPage<Company> companyPage =
            baseMapper.selectCorrelationByCompanies(companies, new Page<>(pageNumber, pageDataSize), search, sort);
        if (companyPage != null && companyPage.getTotal() > 0) {
            if (companies == null) {
                companies = baseMapper.selectCorrelation();
            }
            List<Company> systemUserCompanies = companies;
            List<Company> companyBases = new ArrayList<>();
            List<Long> ids = new ArrayList<>();
            for (Company company : companyPage.getRecords()) {
                for (Company c : companies) {
                    if (company.getId().equals(c.getId()) && !ids.contains(company.getParentId())) {
                        ids.add(company.getId());
                        companyBases.add(company);
                    }
                }
            }
            List<Company> companyList = companyBases.stream()
                .map(company -> treeRecursive(company, systemUserCompanies)).collect(Collectors.toList());
            companyPage.setRecords(companyList).setTotal(companyList.size());
        }
        return companyPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Company>().eq(Company::getParentId, id)) > 0) {
            throw new BusinessException("不允许删除存在下级单位的单位！");
        }
        if (communityResidentMapper
            .selectCount(new LambdaQueryWrapper<CommunityResident>().eq(CommunityResident::getCompanyId, id)) > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民的社区单位！");
        }
        if (dormitoryManagerMapper
            .selectCount(new LambdaQueryWrapper<DormitoryManager>().eq(DormitoryManager::getCompanyId, id)) > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民楼长的社区单位！");
        }
        companyExtraService.remove(new LambdaQueryWrapper<CompanyExtra>().eq(CompanyExtra::getCompanyId, id));
        return baseMapper.deleteById(id) > 0 && companyPhoneNumberMapper
            .delete(new LambdaQueryWrapper<CompanyPhoneNumber>().eq(CompanyPhoneNumber::getCompanyId, id)) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeByIds(Collection<?> list) {
        LambdaQueryWrapper<CompanyExtra> companyExtraQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<CommunityResident> communityResidentQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<DormitoryManager> dormitoryManagerQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<CompanyPhoneNumber> companyPhoneNumberQueryWrapper = new LambdaQueryWrapper<>();
        list.forEach(l -> {
            companyExtraQueryWrapper.eq(CompanyExtra::getCompanyId, l).or();
            communityResidentQueryWrapper.eq(CommunityResident::getCompanyId, l).or();
            dormitoryManagerQueryWrapper.eq(DormitoryManager::getCompanyId, l).or();
            companyPhoneNumberQueryWrapper.eq(CompanyPhoneNumber::getCompanyId, l).or();
        });
        if (communityResidentMapper.selectCount(communityResidentQueryWrapper) > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民的社区单位！");
        }
        if (dormitoryManagerMapper.selectCount(dormitoryManagerQueryWrapper) > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民楼长的社区单位！");
        }
        companyExtraService.remove(companyExtraQueryWrapper);
        return baseMapper.deleteBatchIds(list) > 0
            && companyPhoneNumberMapper.delete(companyPhoneNumberQueryWrapper) > 0;
    }

    @Override
    public List<SelectListVo> treeSelectList(Long[] parentIds) {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        Arrays.stream(parentIds).forEach(parentId -> wrapper.eq(Company::getParentId, parentId));
        return baseMapper.selectList(wrapper).stream()
            .map(company -> new SelectListVo().setId(company.getId()).setValue(company.getId())
                .setName(company.getName()).setLabel(company.getName()).setTitle(company.getName())
                .setIsLeaf(baseMapper
                    .selectCount(new LambdaQueryWrapper<Company>().eq(Company::getParentId, company.getId())) == 0))
            .collect(Collectors.toList());
    }

    /**
     * 树形递归
     *
     * @param company
     *            根节点
     * @param companies
     *            需要整理的单位集合
     * @return 树形完成
     */
    private Company treeRecursive(Company company, List<Company> companies) {
        for (Company c : companies) {
            if (company.getId().equals(c.getParentId())) {
                if (company.getChildren() == null) {
                    company.setChildren(new ArrayList<>());
                }
                company.getChildren().add(treeRecursive(c, companies));
            }
        }
        return company;
    }

    /**
     * 树形递归选择框
     *
     * @param baseSelect
     *            根节点
     * @param companies
     *            需要整理的单位集合
     * @return 树形完成
     */
    private SelectListVo treeRecursiveSelect(SelectListVo baseSelect, List<Company> companies) {
        for (Company company : companies) {
            SelectListVo selectListVo = new SelectListVo();
            selectListVo.setValue(company.getId()).setId(company.getId()).setTitle(company.getName())
                .setLabel(company.getName()).setName(company.getName());
            if (baseSelect.getValue().equals(company.getParentId())) {
                if (baseSelect.getChildren() == null) {
                    baseSelect.setChildren(new ArrayList<>());
                }
                baseSelect.getChildren().add(treeRecursiveSelect(selectListVo, companies));
            }
        }
        return baseSelect;
    }

    /**
     * 处理单位与联系方式关联对象
     *
     * @param entity
     *            单位对象
     * @return 处理完成的对象集合
     */
    private List<CompanyPhoneNumber> companyPhoneNumbersHandler(Company entity) {
        LambdaQueryWrapper<PhoneNumber> wrapper = new LambdaQueryWrapper<>();
        entity.getPhoneNumbers()
            .forEach(phoneNumber -> wrapper.eq(PhoneNumber::getPhoneNumber, phoneNumber.getPhoneNumber()).or());
        List<PhoneNumber> phoneNumbers = phoneNumberMapper.selectList(wrapper);
        return phoneNumbers.stream().map(
            phoneNumber -> new CompanyPhoneNumber().setCompanyId(entity.getId()).setPhoneNumberId(phoneNumber.getId()))
            .collect(Collectors.toList());
    }
}
