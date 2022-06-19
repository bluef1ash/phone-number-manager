package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
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
import com.github.phonenumbermanager.service.SubcontractorService;

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

    @Override
    public IPage<Subcontractor> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize,
        JSONObject search, JSONObject sort) {
        return baseMapper.selectCorrelationByCompanies(companies, new Page<>(pageNumber, pageDataSize), search, sort);
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
}
