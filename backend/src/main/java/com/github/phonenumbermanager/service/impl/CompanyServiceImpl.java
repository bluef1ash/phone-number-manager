package com.github.phonenumbermanager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.CompanyPhoneNumber;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.CompanyMapper;
import com.github.phonenumbermanager.service.*;

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

    @Override
    public boolean save(Company entity) {
        baseMapper.insert(entity);
        // TODO: 2021/10/23 0023 测试mybatis plus 重复插入
        List<CompanyPhoneNumber> companyPhoneNumbers = new ArrayList<>();
        return companyPhoneNumberService.saveBatch(companyPhoneNumbers);
    }

    @Override
    public boolean updateById(Company entity) {
        baseMapper.updateById(entity);
        // TODO: 2021/10/23 0023 测试是否有冗余数据
        phoneNumberService.updateBatchById(entity.getPhoneNumbers());
        List<CompanyPhoneNumber> companyPhoneNumbers = new ArrayList<>();
        entity.getPhoneNumbers().forEach(phoneNumber -> companyPhoneNumbers
            .add(new CompanyPhoneNumber().setCompanyId(entity.getId()).setPhoneNumberId(phoneNumber.getId())));
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
            baseMapper.selectCorrelationByCompanies(companies, new Page<>(pageNumber, pageDataSize));
        if (companyPage != null && companyPage.getTotal() > 0) {
            List<Company> companyList = companyHandler(companies, companyPage.getRecords());
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

    /**
     * 单位处理
     *
     * @param companyList
     *            单位集合
     * @param companies
     *            全部单位
     * @return 整理后单位集合
     */
    private List<Company> companyHandler(List<Company> companyList, List<Company> companies) {
        List<Company> companyList2 = new ArrayList<>();
        for (Company company : companyList) {
            for (Company c : companies) {
                if (company.getId().equals(c.getId())) {
                    companyList2.add(c);
                }
            }
        }
        return companyList2.stream().map(company -> treeRecursive(company, companyList)).collect(Collectors.toList());
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
                company.getChildren().add(treeRecursive(c, companies));
            }
        }
        return company;
    }
}
