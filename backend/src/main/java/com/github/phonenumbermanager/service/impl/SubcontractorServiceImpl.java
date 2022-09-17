package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.mapper.*;
import com.github.phonenumbermanager.service.SubcontractorService;
import com.github.phonenumbermanager.vo.SelectListVo;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * 社区分包人员业务接口实现
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Service
public class SubcontractorServiceImpl extends BaseServiceImpl<SubcontractorMapper, Subcontractor>
    implements SubcontractorService {
    private final PhoneNumberMapper phoneNumberMapper;
    private final SubcontractorPhoneNumberMapper subcontractorPhoneNumberMapper;
    private final CommunityResidentMapper communityResidentMapper;
    private final DormitoryManagerMapper dormitoryManagerMapper;
    private final CompanyMapper companyMapper;

    @Override
    public IPage<Subcontractor> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize,
        JSONObject search, JSONObject sort) {
        List<Company> companyList = systemUserCompanyHandler(companies, companyMapper.selectList(null));
        Page<Subcontractor> page = new Page<>(pageNumber, pageDataSize);
        page.setSearchCount(false);
        page.setTotal(baseMapper.selectCorrelationCountByCompanies(companyList, search, sort));
        return baseMapper.selectCorrelationByCompanies(page, companyList, search, sort);
    }

    @Override
    public Subcontractor getCorrelation(Long id) {
        return baseMapper.selectAndCompanyById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(Subcontractor entity) {
        boolean isSuccess = baseMapper.insert(entity) > 0;
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        subcontractorPhoneNumberMapper.insertBatchSomeColumn(phoneNumbersHandler(entity));
        return isSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(Subcontractor entity) {
        boolean isSuccess = baseMapper.updateById(entity) > 0;
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        subcontractorPhoneNumberMapper.insertIgnoreBatchSomeColumn(phoneNumbersHandler(entity));
        return isSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        if (communityResidentMapper.selectCount(
            new LambdaQueryWrapper<CommunityResident>().eq(CommunityResident::getSubcontractorId, id)) > 0) {
            throw new BusinessException("该分包人员已经参加分包社区居民，无法删除！");
        }
        if (dormitoryManagerMapper
            .selectCount(new LambdaQueryWrapper<DormitoryManager>().eq(DormitoryManager::getSubcontractorId, id)) > 0) {
            throw new BusinessException("该分包人员已经参加分包社区楼片长，无法删除！");
        }
        subcontractorPhoneNumberMapper.delete(
            new LambdaQueryWrapper<SubcontractorPhoneNumber>().eq(SubcontractorPhoneNumber::getSubcontractorId, id));
        return baseMapper.deleteById(id) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeByIds(Collection<?> list) {
        if (communityResidentMapper.selectCount(
            new LambdaQueryWrapper<CommunityResident>().in(CommunityResident::getSubcontractorId, list)) > 0) {
            throw new BusinessException("该分包人员已经参加分包社区居民，无法删除！");
        }
        if (dormitoryManagerMapper.selectCount(
            new LambdaQueryWrapper<DormitoryManager>().in(DormitoryManager::getSubcontractorId, list)) > 0) {
            throw new BusinessException("该分包人员已经参加分包社区楼片长，无法删除！");
        }
        subcontractorPhoneNumberMapper.delete(
            new LambdaQueryWrapper<SubcontractorPhoneNumber>().in(SubcontractorPhoneNumber::getSubcontractorId, list));
        return baseMapper.deleteBatchIds(list) > 0;
    }

    @Override
    public List<SelectListVo> treeSelectList(Long[] parentIds) {
        SystemUser systemUser = (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<SelectListVo> selectListVos = new ArrayList<>();
        List<Long> parentIdList =
            companyMapper.selectList(new LambdaQueryWrapper<Company>().select(Company::getParentId)).stream()
                .map(Company::getParentId).distinct().collect(Collectors.toList());
        if (parentIds == null) {
            if (systemUser.getCompanies() == null) {
                selectListVos = companyMapper.selectList(new LambdaQueryWrapper<Company>().eq(Company::getParentId, 0L))
                    .stream().map(company -> new SelectListVo().setValue(company.getId()).setLabel(company.getName())
                        .setIsLeaf(false))
                    .collect(Collectors.toList());
            } else {
                Long[] ids = systemUser.getCompanies().stream().map(Company::getId).toArray(Long[]::new);
                selectListVos.addAll(getSelectVos(ids, parentIdList));
            }
        } else {
            selectListVos.addAll(getSelectVos(parentIds, parentIdList));
        }
        return selectListVos;
    }

    @Override
    public Map<String, Object> getBarChart(List<Company> companies, Long[] companyIds, List<Company> companyAll,
        String personCountAlias, Long systemAdministratorId) {
        List<Company> companyList = new ArrayList<>();
        List<Long> subordinateCompanyIds = new ArrayList<>();
        companiesAndSubordinate(companies, companyIds, companyAll, companyList, subordinateCompanyIds);
        Map<String, Object> barChartMap;
        if (subordinateCompanyIds.isEmpty() && !companyAll.isEmpty()) {
            Long[] companyIdArray = companyIds;
            if (companyIds == null) {
                companyIdArray = companies.stream().map(Company::getId).toArray(Long[]::new);
            }
            List<Map<String, Object>> systemUserCounts = baseMapper.selectCorrelationCountByCompanyIds(companyIdArray);
            List<Map<String, Object>> data = new ArrayList<>();
            for (Map<String, Object> systemUserCount : systemUserCounts) {
                Map<String, Object> communityResidentMap = new HashMap<>(3);
                communityResidentMap.put("countType", "分包社区居民数");
                communityResidentMap.put("subcontractorName", systemUserCount.get("subcontractor_name"));
                communityResidentMap.put("personCount", systemUserCount.get("community_resident_count"));
                Map<String, Object> dormitoryManagerMap = new HashMap<>(3);
                dormitoryManagerMap.put("countType", "分包社区居民楼片长数");
                dormitoryManagerMap.put("subcontractorName", systemUserCount.get("subcontractor_name"));
                dormitoryManagerMap.put("personCount", systemUserCount.get("dormitory_manager_count"));
                data.add(communityResidentMap);
                data.add(dormitoryManagerMap);
            }
            barChartMap = getBarChartMap("subcontractorName", "分包人", "personCount", "人数");
            barChartMap.put("seriesField", "countType");
            barChartMap.put("isGroup", true);
            barChartMap.put("data", data);
        } else {
            // 选择非最下层单位时，列出分包人数
            barChartMap = barChartHandle(companyAll, "人数", companyList, subordinateCompanyIds, systemAdministratorId);
        }
        return barChartMap;
    }

    /**
     * 处理社区分包人员联系方式关联对象
     *
     * @param entity
     *            单位对象
     * @return 处理完成的对象集合
     */
    private List<SubcontractorPhoneNumber> phoneNumbersHandler(Subcontractor entity) {
        LambdaQueryWrapper<PhoneNumber> wrapper = new LambdaQueryWrapper<>();
        entity.getPhoneNumbers().stream().map(PhoneNumber::getPhoneNumber)
            .forEach(phoneNumber -> wrapper.eq(PhoneNumber::getPhoneNumber, phoneNumber).or());
        List<PhoneNumber> phoneNumbers = phoneNumberMapper.selectList(wrapper);
        return phoneNumbers.stream().map(phoneNumber -> new SubcontractorPhoneNumber()
            .setSubcontractorId(entity.getId()).setPhoneNumberId(phoneNumber.getId())).collect(Collectors.toList());
    }

    /**
     * 获取表单对象集合
     *
     * @param ids
     *            查询的编号集合
     * @param parentIds
     *            所有父级编号
     * @return 表单对象集合
     */
    private List<SelectListVo> getSelectVos(Long[] ids, List<Long> parentIds) {
        List<SelectListVo> selectListVos = new ArrayList<>();
        LambdaQueryWrapper<Company> companyWrapper = new LambdaQueryWrapper<>();
        Arrays.stream(ids).filter(parentIds::contains).forEach(id -> companyWrapper.eq(Company::getParentId, id));
        List<Long> subIds = Arrays.stream(ids).filter(id -> !parentIds.contains(id)).collect(Collectors.toList());
        if (companyWrapper.nonEmptyOfWhere()) {
            selectListVos.addAll(companyMapper.selectList(companyWrapper).stream().map(
                company -> new SelectListVo().setLabel(company.getName()).setValue(company.getId()).setIsLeaf(false))
                .collect(Collectors.toList()));
        }
        if (!subIds.isEmpty()) {
            selectListVos.addAll(baseMapper
                .selectListByCompanyIds(subIds).stream().map(subcontractor -> new SelectListVo()
                    .setLabel(subcontractor.getName()).setValue(subcontractor.getId()).setIsLeaf(true))
                .collect(Collectors.toList()));
        }
        return selectListVos;
    }
}
