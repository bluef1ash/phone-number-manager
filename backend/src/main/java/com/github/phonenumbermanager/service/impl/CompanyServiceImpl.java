package com.github.phonenumbermanager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.CompanyMapper;
import com.github.phonenumbermanager.service.*;
import com.github.phonenumbermanager.vo.SelectListVo;

import cn.hutool.json.JSONObject;

/**
 * 单位业务实现
 *
 * @author 廿二月的天
 */
@Service("companyService")
public class CompanyServiceImpl extends BaseServiceImpl<CompanyMapper, Company> implements CompanyService {
    @Resource
    private CommunityResidentService communityResidentService;
    @Resource
    private DormitoryManagerService dormitoryManagerService;
    @Resource
    private PhoneNumberService phoneNumberService;
    @Resource
    private CompanyPhoneNumberService companyPhoneNumberService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(Company entity) {
        baseMapper.insert(entity);
        phoneNumberService.saveIgnoreBatch(entity.getPhoneNumbers());
        QueryWrapper<PhoneNumber> wrapper = new QueryWrapper<>();
        entity.getPhoneNumbers().forEach(phoneNumber -> wrapper.eq("phone_number", phoneNumber.getPhoneNumber()).or());
        List<PhoneNumber> phoneNumbers = phoneNumberService.list(wrapper);
        List<CompanyPhoneNumber> companyPhoneNumbers = phoneNumbers.stream().map(
            phoneNumber -> new CompanyPhoneNumber().setCompanyId(entity.getId()).setPhoneNumberId(phoneNumber.getId()))
            .collect(Collectors.toList());
        return companyPhoneNumberService.saveBatch(companyPhoneNumbers);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(Company entity) {
        baseMapper.updateById(entity);
        phoneNumberService.saveBatch(entity.getPhoneNumbers());
        List<CompanyPhoneNumber> companyPhoneNumbers = entity.getPhoneNumbers().stream().map(
            phoneNumber -> new CompanyPhoneNumber().setCompanyId(entity.getId()).setPhoneNumberId(phoneNumber.getId()))
            .collect(Collectors.toList());
        return companyPhoneNumberService.updateBatchById(companyPhoneNumbers);
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
    public boolean removeCorrelationById(Long id) {
        long communityResidentCount =
            communityResidentService.count(new QueryWrapper<CommunityResident>().eq("company_id", id));
        if (communityResidentCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民的社区单位！");
        }
        long dormitoryManagerCount =
            dormitoryManagerService.count(new QueryWrapper<DormitoryManager>().eq("company_id", id));
        if (dormitoryManagerCount > 0) {
            throw new BusinessException("不允许删除存在有下属社区居民楼长的社区单位！");
        }
        return baseMapper.deleteById(id) > 0
            && companyPhoneNumberService.remove(new QueryWrapper<CompanyPhoneNumber>().eq("company_id", id));
    }

    @Override
    public void listSubmissionCompanyIds(List<Long> companyIds, List<Company> companies, List<Company> companyAll,
        Map<Long, String> parents) {
        companies.forEach(company -> {
            if (company.getLevel() == 0) {
                companyIds.add(company.getId());
                if (parents != null && parents.get(company.getParentId()).isEmpty()) {
                    Optional<Company> companyOptional = companyAll.stream()
                        .filter(company1 -> company1.getId().equals(company.getParentId())).findFirst();
                    assert companyOptional.isPresent();
                    parents.put(company.getParentId(), companyOptional.get().getName());
                }
            } else {
                List<Company> companyList = companyAll.stream()
                    .filter(company1 -> company1.getParentId().equals(company.getId())).collect(Collectors.toList());
                listSubmissionCompanyIds(companyIds, companyList, companyAll, parents);
            }
        });
    }

    @Override
    public void listRecursionCompanyIds(List<Long> companyIds, List<Company> companies, List<Company> companyAll,
        Long parentId) {
        companies.forEach(company -> {
            if (parentId.equals(company.getParentId())) {
                companyIds.add(company.getId());
            } else {
                List<Company> companies1 = companyAll.stream()
                    .filter(company1 -> company1.getParentId().equals(company.getId())).collect(Collectors.toList());
                listRecursionCompanyIds(companyIds, companies1, companyAll, company.getId());
            }
        });
    }

    @Override
    public List<SelectListVo> treeSelectList() {
        SystemUser systemUser = (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Company> companies = baseMapper.selectList(null);
        List<Company> userCompanies = systemUser.getCompanies();
        if (userCompanies == null) {
            userCompanies = companies;
        }
        return userCompanies.stream().filter(company -> company.getParentId() == 0L).map(company -> {
            SelectListVo selectListVo = new SelectListVo();
            selectListVo.setTitle(company.getName()).setValue(company.getId()).setLevel(company.getLevel());
            treeRecursive(company, companies);
            return selectListVo;
        }).collect(Collectors.toList());
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
}
