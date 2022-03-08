package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.*;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(Company entity) {
        baseMapper.insert(entity);
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        return companyPhoneNumberMapper.insertBatchSomeColumn(companyPhoneNumbersHandler(entity)) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(Company entity) {
        baseMapper.updateById(entity);
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        companyPhoneNumberMapper.delete(new QueryWrapper<CompanyPhoneNumber>().eq("company_id", entity.getId()));
        return companyPhoneNumberMapper.insertBatchSomeColumn(companyPhoneNumbersHandler(entity)) > 0;
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
        long parentIdCount = baseMapper.selectCount(new QueryWrapper<Company>().eq("parent_id", id));
        if (parentIdCount > 0) {
            throw new BusinessException("不允许删除存在下级单位的单位！");
        }
        long communityResidentCount =
            communityResidentMapper.selectCount(new QueryWrapper<CommunityResident>().eq("company_id", id));
        if (communityResidentCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民的社区单位！");
        }
        long dormitoryManagerCount =
            dormitoryManagerMapper.selectCount(new QueryWrapper<DormitoryManager>().eq("company_id", id));
        if (dormitoryManagerCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民楼长的社区单位！");
        }
        return baseMapper.deleteById(id) > 0
            && companyPhoneNumberMapper.delete(new QueryWrapper<CompanyPhoneNumber>().eq("company_id", id)) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeByIds(Collection<?> list) {
        QueryWrapper<CommunityResident> communityResidentQueryWrapper = new QueryWrapper<>();
        QueryWrapper<DormitoryManager> dormitoryManagerQueryWrapper = new QueryWrapper<>();
        QueryWrapper<CompanyPhoneNumber> companyPhoneNumberQueryWrapper = new QueryWrapper<>();
        list.forEach(l -> {
            communityResidentQueryWrapper.eq("company_id", l).or();
            dormitoryManagerQueryWrapper.eq("company_id", l).or();
            companyPhoneNumberQueryWrapper.eq("company_id", l).or();
        });
        if (communityResidentMapper.selectCount(communityResidentQueryWrapper) > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民的社区单位！");
        }
        if (dormitoryManagerMapper.selectCount(dormitoryManagerQueryWrapper) > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民楼长的社区单位！");
        }
        companyPhoneNumberMapper.delete(companyPhoneNumberQueryWrapper);
        return super.removeByIds(list);
    }

    @Override
    public List<SelectListVo> treeSelectList() {
        SystemUser systemUser = (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Company> companies = baseMapper.selectList(null);
        List<Company> userCompanies = systemUser.getCompanies();
        if (userCompanies == null) {
            userCompanies = companies;
        }
        List<SelectListVo> selectListVoBases = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (Company company : userCompanies) {
            for (Company c : companies) {
                if (company.getId().equals(c.getId()) && !ids.contains(company.getParentId())) {
                    ids.add(company.getId());
                    selectListVoBases.add(new SelectListVo().setTitle(company.getName()).setLabel(company.getName())
                        .setValue(company.getId()).setLevel(company.getLevel()));
                }
            }
        }
        List<Company> finalUserCompanies = userCompanies;
        return selectListVoBases.stream().map(selectListVo -> treeRecursiveSelect(selectListVo, finalUserCompanies))
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
            selectListVo.setValue(company.getId()).setTitle(company.getName()).setLabel(company.getName())
                .setLevel(company.getLevel());
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
        QueryWrapper<PhoneNumber> wrapper = new QueryWrapper<>();
        entity.getPhoneNumbers().forEach(phoneNumber -> wrapper.eq("phone_number", phoneNumber.getPhoneNumber()).or());
        List<PhoneNumber> phoneNumbers = phoneNumberMapper.selectList(wrapper);
        return phoneNumbers.stream().map(
            phoneNumber -> new CompanyPhoneNumber().setCompanyId(entity.getId()).setPhoneNumberId(phoneNumber.getId()))
            .collect(Collectors.toList());
    }
}
