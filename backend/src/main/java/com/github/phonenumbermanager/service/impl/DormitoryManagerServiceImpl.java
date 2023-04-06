package com.github.phonenumbermanager.service.impl;

import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.constant.enums.*;
import com.github.phonenumbermanager.dto.DormitoryManagerExcelDTO;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.handler.DormitoryManagerTitleSheetWriteHandler;
import com.github.phonenumbermanager.listener.DormitoryManagerImportReadListener;
import com.github.phonenumbermanager.mapper.*;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * 社区楼长业务实现
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Service
public class DormitoryManagerServiceImpl extends BaseServiceImpl<DormitoryManagerMapper, DormitoryManager>
    implements DormitoryManagerService {
    private final CompanyMapper companyMapper;
    private final PhoneNumberMapper phoneNumberMapper;
    private final DormitoryManagerPhoneNumberMapper dormitoryManagerPhoneNumberMapper;
    private final SubcontractorMapper subcontractorMapper;
    private final RedisUtil redisUtil;

    @Override
    public DormitoryManager getCorrelation(Long id) {
        List<String> info = new ArrayList<>();
        DormitoryManager dormitoryManager = baseMapper.selectAndCompanyById(id);
        info.add(dormitoryManager.getCompany().getName());
        info.add(dormitoryManager.getSubcontractor().getName());
        dormitoryManager.setSubcontractorInfo(info);
        return dormitoryManager;
    }

    @Async
    @Override
    public void asyncImport(InputStream inputStream, int startRowNumber, Long importId) {
        DormitoryManagerImportReadListener listener =
            new DormitoryManagerImportReadListener(redisUtil, importId, companyMapper.selectList(null),
                subcontractorMapper.selectList(null), phoneNumberMapper.selectList(null), this);
        EasyExcel.read(inputStream, DormitoryManagerExcelDTO.class, listener).headRowNumber(startRowNumber).sheet(0)
            .doRead();
        redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + importId,
            ImportOrExportStatusEnum.DONE.getValue(), 20, TimeUnit.MINUTES);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(List<DormitoryManagerExcelDTO> dormitoryManagerExcelDTOs, List<Company> companyAll,
        List<Subcontractor> subcontractorAll, List<PhoneNumber> phoneNumberAll) {
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        List<DormitoryManagerPhoneNumber> dormitoryManagerPhoneNumbers = new ArrayList<>();
        List<DormitoryManager> dormitoryManagers = dormitoryManagerExcelDTOs.stream().map(dto -> {
            DormitoryManager dormitoryManager = new DormitoryManager();
            String companyName = StrUtil.cleanBlank(dto.getCommunityName());
            Company company =
                companyAll.stream().filter(c -> c.getName().contains(companyName)).findFirst().orElse(null);
            if (company == null) {
                throw new BusinessException("单位读取失败！");
            }
            String subcontractorName = StrUtil.cleanBlank(dto.getSubcontractorName());
            Subcontractor subcontractor = subcontractorAll.stream()
                .filter(s -> s.getName().equals(subcontractorName) && s.getCompanyId().equals(company.getId()))
                .findFirst().orElse(null);
            if (subcontractor == null) {
                throw new BusinessException("未找到对应的分包人信息，请重试！");
            }
            String idNumber = StrUtil.cleanBlank(dto.getIdNumber());
            if (!IdcardUtil.isValidCard(idNumber)) {
                throw new BusinessException("身份证号码：“" + idNumber + "”不正确，请重试！");
            }
            dormitoryManager.setId(IdWorker.getId()).setCompanyId(company.getId())
                .setName(StrUtil.cleanBlank(dto.getName())).setIdNumber(idNumber)
                .setGender(GenderEnum.valueOf(IdcardUtil.getGenderByIdCard(idNumber)))
                .setBirth(LocalDate.of(IdcardUtil.getYearByIdCard(idNumber), IdcardUtil.getMonthByIdCard(idNumber),
                    IdcardUtil.getDayByIdCard(idNumber)))
                .setPoliticalStatus(PoliticalStatusEnum.descriptionOf(StrUtil.cleanBlank(dto.getPoliticalStatus())))
                .setEmploymentStatus(EmploymentStatusEnum.descriptionOf(StrUtil.cleanBlank(dto.getEmploymentStatus())))
                .setEducation(EducationStatusEnum.descriptionOf(StrUtil.cleanBlank(dto.getEducation())))
                .setAddress(StrUtil.cleanBlank(dto.getAddress()))
                .setManagerAddress(StrUtil.cleanBlank(dto.getManagerAddress())).setManagerCount(dto.getManagerCount())
                .setSubcontractorId(subcontractor.getId());
            String mobile = StrUtil.cleanBlank(dto.getMobile());
            String landline = StrUtil.cleanBlank(dto.getFixedLine());
            List<PhoneNumber> phones = CommonUtil.phoneNumber2List(phoneNumberAll, mobile, landline);
            dormitoryManagerPhoneNumbers.addAll(phones.stream().map(phoneNumber -> {
                DormitoryManagerPhoneNumber dormitoryManagerPhoneNumber = new DormitoryManagerPhoneNumber();
                phoneNumbers.add(phoneNumber);
                dormitoryManagerPhoneNumber.setDormitoryManagerId(dormitoryManager.getId())
                    .setPhoneNumberId(phoneNumber.getId());
                return dormitoryManagerPhoneNumber;
            }).toList());
            return dormitoryManager;
        }).toList();
        if (!dormitoryManagers.isEmpty()) {
            LambdaQueryWrapper<DormitoryManagerPhoneNumber> wrapper = new LambdaQueryWrapper<>();
            boolean isSuccess = baseMapper.insertBatchSomeColumn(dormitoryManagers) > 0;
            dormitoryManagers.forEach(dormitoryManager -> wrapper
                .eq(DormitoryManagerPhoneNumber::getDormitoryManagerId, dormitoryManager.getId()).or());
            dormitoryManagerPhoneNumberMapper.delete(wrapper);
            phoneNumberMapper.insertIgnoreBatchSomeColumn(phoneNumbers);
            dormitoryManagerPhoneNumberMapper.insertIgnoreBatchSomeColumn(dormitoryManagerPhoneNumbers);
            return isSuccess;
        }
        return false;
    }

    @Async
    @Override
    public void listCorrelationExportExcel(SystemUser currentSystemUser, Map<String, JSONObject> configurationMap,
        Long exportId) {
        redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + exportId,
            ImportOrExportStatusEnum.HANDLING.getValue(), 20, TimeUnit.MINUTES);
        String excelDormitoryTitleUp = Convert.toStr(configurationMap.get("excel_dormitory_title_up").get("content"));
        String excelDormitoryTitle = Convert.toStr(configurationMap.get("excel_dormitory_title").get("content"));
        List<Company> companyAll = companyMapper.selectList(null);
        List<Subcontractor> subcontractorAll = subcontractorMapper.selectAndPhoneNumber();
        List<Long> subordinateCompanyIds =
            exportExcelGetSubordinateCompanyIds(currentSystemUser, configurationMap, companyAll);
        List<DormitoryManager> dormitoryManagers = getListByCompanyIds(subordinateCompanyIds);
        Queue<String> previousCommunityName = new LinkedList<>();
        previousCommunityName.offer("");
        String[] ids = new String[2];
        AtomicInteger sequenceNumber = new AtomicInteger(1);
        AtomicInteger idSequenceNumber = new AtomicInteger(1);
        List<DormitoryManagerExcelDTO> exportDTOs = dormitoryManagers.stream().map(dormitoryManager -> {
            DormitoryManagerExcelDTO dormitoryManagerExcelDTO = BeanUtil.copyProperties(dormitoryManager,
                DormitoryManagerExcelDTO.class, "id", "gender", "politicalStatus", "employmentStatus", "education");
            dormitoryManagerExcelDTO.setSequenceNumber(sequenceNumber.getAndIncrement());
            // 街道名称
            String streetName =
                companyAll.stream().filter(c -> dormitoryManager.getCompany().getParentId().equals(c.getId()))
                    .map(Company::getName).findFirst().orElse("").replaceAll(SystemConstant.STREET_NAME_PATTERN, "");
            dormitoryManagerExcelDTO.setStreetName(streetName);
            // 处理社区名称
            String communityName =
                dormitoryManager.getCompany().getName().replaceAll(SystemConstant.COMMUNITY_NAME_PATTERN, "");
            dormitoryManagerExcelDTO.setCommunityName(communityName);
            // 编号
            String prevCommunityName = previousCommunityName.poll();
            assert prevCommunityName != null;
            if (!prevCommunityName.equals(communityName)) {
                idSequenceNumber.set(1);
                ids[0] = PinyinUtil.getFirstLetter(streetName, "").toUpperCase()
                    + PinyinUtil.getFirstLetter(communityName, "").toUpperCase();
            }
            ids[1] = String.format("%04d", idSequenceNumber.getAndIncrement());
            dormitoryManagerExcelDTO.setId(ids[0] + ids[1]);
            previousCommunityName.offer(communityName);
            dormitoryManager.getPhoneNumbers().forEach(phoneNumber -> {
                if (PhoneUtil.isMobile(phoneNumber.getPhoneNumber())) {
                    dormitoryManagerExcelDTO.setMobile(phoneNumber.getPhoneNumber());
                } else {
                    dormitoryManagerExcelDTO.setFixedLine(phoneNumber.getPhoneNumber());
                }
            });
            dormitoryManagerExcelDTO.setGender(dormitoryManager.getGender().getDescription());
            dormitoryManagerExcelDTO.setPoliticalStatus(dormitoryManager.getPoliticalStatus().getDescription());
            dormitoryManagerExcelDTO.setEmploymentStatus(dormitoryManager.getEmploymentStatus().getDescription());
            dormitoryManagerExcelDTO.setEducation(dormitoryManager.getEducation().getDescription());
            // 处理分包人
            dormitoryManagerExcelDTO.setSubcontractorName(dormitoryManager.getSubcontractor().getName());
            Subcontractor subcontractor = subcontractorAll.stream()
                .filter(s -> dormitoryManager.getSubcontractor().getId().equals(s.getId())).findFirst().orElse(null);
            if (subcontractor != null) {
                dormitoryManagerExcelDTO
                    .setSubcontractorMobile(subcontractor.getPhoneNumbers().get(0).getPhoneNumber());
            } else {
                dormitoryManagerExcelDTO.setSubcontractorMobile("");
            }
            return dormitoryManagerExcelDTO;
        }).toList();
        String fileName = FileUtil.getTmpDirPath() + SystemConstant.EXPORT_ID_KEY + exportId + ".xlsx";
        ExcelWriter excelWriter = EasyExcel.write(fileName, DormitoryManagerExcelDTO.class)
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .registerWriteHandler(
                new DormitoryManagerTitleSheetWriteHandler(excelDormitoryTitleUp, excelDormitoryTitle))
            .registerWriteHandler(getHeadAndContentStyle()).useDefaultStyle(false).relativeHeadRowIndex(3).build();
        try (excelWriter) {
            if (exportDTOs.isEmpty()) {
                excelWriter.write(exportDTOs, EasyExcel.writerSheet().build());
            } else {
                for (int i = 0; i < Math.ceil((double)exportDTOs.size() / SystemConstant.WRITE_FILE_SIZE); i++) {
                    List<DormitoryManagerExcelDTO> data = ListUtil.sub(exportDTOs, i * SystemConstant.WRITE_FILE_SIZE,
                        (SystemConstant.WRITE_FILE_SIZE - 1) * (i + 1) + i);
                    excelWriter.write(data, EasyExcel.writerSheet().build());
                }
            }
        } finally {
            excelWriter.finish();
        }
        redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + exportId,
            ImportOrExportStatusEnum.HANDLED.getValue(), 20, TimeUnit.MINUTES);
    }

    @Override
    public IPage<DormitoryManager> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize,
        JSONObject search, JSONObject sort) {
        List<Company> companyList = systemUserCompanyHandler(companies, companyMapper.selectList(null));
        Page<DormitoryManager> page = new Page<>(pageNumber, pageDataSize);
        page.setSearchCount(false);
        page.setTotal(baseMapper.selectCorrelationCountByCompanies(companyList, search, sort));
        return baseMapper.selectCorrelationByCompanies(page, companyList, search, sort);
    }

    @Override
    public Map<String, Object> getBaseMessage(List<Company> companies, Long[] companyIds) {
        List<Company> companyAll = companyMapper.selectList(null);
        if (companies == null) {
            companies = companyAll.stream().filter(company -> company.getParentId() == 0L).collect(Collectors.toList());
        }
        if (companyIds == null) {
            companyIds = companies.stream().map(Company::getId).toArray(Long[]::new);
        }
        return computedBaseMessage(companyAll, companyIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        baseMapper.deleteById(id);
        return dormitoryManagerPhoneNumberMapper
            .delete(new QueryWrapper<DormitoryManagerPhoneNumber>().eq("dormitory_manager_id", id)) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(DormitoryManager entity) {
        entity.setGender(GenderEnum.valueOf(IdcardUtil.getGenderByIdCard(entity.getIdNumber())))
            .setBirth(LocalDate.of(IdcardUtil.getYearByIdCard(entity.getIdNumber()),
                IdcardUtil.getMonthByIdCard(entity.getIdNumber()), IdcardUtil.getDayByIdCard(entity.getIdNumber())))
            .setCompanyId(Long.valueOf(entity.getSubcontractorInfo().get(0)))
            .setSubcontractorId(Long.valueOf(entity.getSubcontractorInfo().get(1)));
        baseMapper.insert(entity);
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        return dormitoryManagerPhoneNumberMapper.insertBatchSomeColumn(dormitoryManagerPhoneNumbersHandler(entity)) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(DormitoryManager entity) {
        entity.setGender(GenderEnum.valueOf(IdcardUtil.getGenderByIdCard(entity.getIdNumber())))
            .setBirth(LocalDate.of(IdcardUtil.getYearByIdCard(entity.getIdNumber()),
                IdcardUtil.getMonthByIdCard(entity.getIdNumber()), IdcardUtil.getDayByIdCard(entity.getIdNumber())))
            .setCompanyId(Long.valueOf(entity.getSubcontractorInfo().get(0)))
            .setSubcontractorId(Long.valueOf(entity.getSubcontractorInfo().get(1)));
        boolean isSuccess = baseMapper.updateById(entity) > 0;
        if (phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers()) > 0) {
            dormitoryManagerPhoneNumberMapper
                .delete(new QueryWrapper<DormitoryManagerPhoneNumber>().eq("dormitory_manager_id", entity.getId()));
            dormitoryManagerPhoneNumberMapper.insertBatchSomeColumn(dormitoryManagerPhoneNumbersHandler(entity));
        }
        return isSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeByIds(Collection<?> list) {
        LambdaQueryWrapper<DormitoryManagerPhoneNumber> wrapper = new LambdaQueryWrapper<>();
        list.forEach(l -> wrapper.eq(DormitoryManagerPhoneNumber::getDormitoryManagerId, l).or());
        baseMapper.deleteBatchIds(list);
        return dormitoryManagerPhoneNumberMapper.delete(wrapper) > 0;
    }

    /**
     * 计算录入统计信息
     *
     * @param companyAll
     *            所有单位集合
     * @param companyIds
     *            单位编号集合
     * @return 录入统计信息
     */
    private Map<String, Object> computedBaseMessage(List<Company> companyAll, Long[] companyIds) {
        Map<String, Object> baseMessage = new HashMap<>(5);
        int maleCount = 0;
        int femaleCount = 0;
        int unknownCount = 0;
        int[] ageCounts = {0, 0, 0, 0, 0, 0, 0, 0};
        Map<String, Integer> educationMap = Arrays.stream(EducationStatusEnum.values())
            .collect(Collectors.toMap(EducationStatusEnum::getDescription, value -> 0));
        Map<String, Integer> politicalStatusMap = Arrays.stream(PoliticalStatusEnum.values())
            .collect(Collectors.toMap(PoliticalStatusEnum::getDescription, value -> 0));
        Map<String, Integer> employmentStatusMap = Arrays.stream(EmploymentStatusEnum.values())
            .collect(Collectors.toMap(EmploymentStatusEnum::getDescription, value -> 0));
        LambdaQueryWrapper<DormitoryManager> wrapper = new LambdaQueryWrapper<>();
        Arrays.stream(companyIds).forEach(companyId -> {
            List<Long> recursionCompanyIds = CommonUtil.listRecursionCompanyIds(companyAll, companyId);
            if (!recursionCompanyIds.isEmpty()) {
                recursionCompanyIds.forEach(id -> wrapper.eq(DormitoryManager::getCompanyId, id).or());
            } else {
                wrapper.eq(DormitoryManager::getCompanyId, companyId).or();
            }
        });
        List<DormitoryManager> dormitoryManagers = baseMapper.selectList(wrapper);
        LocalDate now = LocalDate.now();
        for (DormitoryManager dormitoryManager : dormitoryManagers) {
            switch (dormitoryManager.getGender()) {
                case MALE -> maleCount += 1;
                case FEMALE -> femaleCount += 1;
                default -> unknownCount += 1;
            }
            long age = dormitoryManager.getBirth().until(now, ChronoUnit.YEARS);
            if (age < 20L) {
                ageCounts[0] += 1;
            } else if (age < 30L) {
                ageCounts[1] += 1;
            } else if (age < 40L) {
                ageCounts[2] += 1;
            } else if (age < 50L) {
                ageCounts[3] += 1;
            } else if (age < 60L) {
                ageCounts[4] += 1;
            } else if (age < 70L) {
                ageCounts[5] += 1;
            } else if (age < 80L) {
                ageCounts[6] += 1;
            } else {
                ageCounts[7] += 1;
            }
            educationMap.put(dormitoryManager.getEducation().getDescription(),
                educationMap.get(dormitoryManager.getEducation().getDescription()) + 1);
            politicalStatusMap.put(dormitoryManager.getPoliticalStatus().getDescription(),
                politicalStatusMap.get(dormitoryManager.getPoliticalStatus().getDescription()) + 1);
            employmentStatusMap.put(dormitoryManager.getEmploymentStatus().getDescription(),
                employmentStatusMap.get(dormitoryManager.getEmploymentStatus().getDescription()) + 1);
        }
        baseMessage.put("inputCount", dormitoryManagers.size());
        baseMessage.put("genderCount", genderCount(maleCount, femaleCount, unknownCount));
        baseMessage.put("ageCount", ageCount(ageCounts));
        baseMessage.put("educationCount", mapCount("educationType", educationMap));
        baseMessage.put("politicalStatusCount", mapCount("politicalStatusType", politicalStatusMap));
        baseMessage.put("employmentStatusCount", mapCount("employmentStatusType", employmentStatusMap));
        baseMessage.put("loading", false);
        return baseMessage;
    }

    /**
     * 生成 Map 集合统计
     *
     * @param mapType
     *            Map 类型
     * @param map
     *            定义 Map
     * @return 统计对象
     */
    private Map<String, Object> mapCount(String mapType, Map<String, Integer> map) {
        Map<String, Object> count = new HashMap<>(3);
        String value = "value";
        List<Map<String, Object>> countData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Map<String, Object> temp = new HashMap<>(2);
            temp.put(mapType, entry.getKey());
            temp.put(value, entry.getValue());
            countData.add(temp);
        }
        count.put("data", countData);
        count.put("angleField", value);
        count.put("colorField", mapType);
        return count;
    }

    /**
     * 生成年龄统计
     *
     * @param ageCounts
     *            年龄统计数组
     * @return 统计对象
     */
    private Map<String, Object> ageCount(int[] ageCounts) {
        Map<String, Object> ageCount = new HashMap<>(3);
        String value = "value";
        String ageType = "ageType";
        List<Map<String, Object>> ageCountData = new ArrayList<>();
        Map<String, Object> ageCountDataTemp = new HashMap<>(8);
        ageCountDataTemp.put("20岁以下", ageCounts[0]);
        ageCountDataTemp.put("20岁~29岁", ageCounts[1]);
        ageCountDataTemp.put("30岁~39岁", ageCounts[2]);
        ageCountDataTemp.put("40岁~49岁", ageCounts[3]);
        ageCountDataTemp.put("50岁~59岁", ageCounts[4]);
        ageCountDataTemp.put("60岁~69岁", ageCounts[5]);
        ageCountDataTemp.put("70岁~79岁", ageCounts[6]);
        ageCountDataTemp.put("80岁以上", ageCounts[7]);
        for (Map.Entry<String, Object> entry : ageCountDataTemp.entrySet()) {
            Map<String, Object> tempMap = new HashMap<>(2);
            tempMap.put(ageType, entry.getKey());
            tempMap.put(value, entry.getValue());
            ageCountData.add(tempMap);
        }
        ageCount.put("data", ageCountData);
        ageCount.put("angleField", value);
        ageCount.put("colorField", ageType);
        return ageCount;
    }

    /**
     * 生成性别统计
     *
     * @param maleCount
     *            男性数量
     * @param femaleCount
     *            女性数量
     * @param unknownCount
     *            未知数量
     * @return 统计对象
     */
    private Map<String, Object> genderCount(int maleCount, int femaleCount, int unknownCount) {
        Map<String, Object> genderCount = new HashMap<>(5);
        List<Map<String, Object>> genderCountData = new ArrayList<>();
        String value = "value";
        String genderType = "genderType";
        Map<String, Object> genderCountDataMaleMap = new HashMap<>(2);
        genderCountDataMaleMap.put(genderType, GenderEnum.MALE.getDescription());
        genderCountDataMaleMap.put(value, maleCount);
        Map<String, Object> genderCountDataFemaleMap = new HashMap<>(2);
        genderCountDataFemaleMap.put(genderType, GenderEnum.FEMALE.getDescription());
        genderCountDataFemaleMap.put(value, femaleCount);
        Map<String, Object> genderCountDataUnknownMap = new HashMap<>(2);
        genderCountDataUnknownMap.put(genderType, GenderEnum.UNKNOWN.getDescription());
        genderCountDataUnknownMap.put(value, unknownCount);
        genderCountData.add(genderCountDataMaleMap);
        genderCountData.add(genderCountDataFemaleMap);
        genderCountData.add(genderCountDataUnknownMap);
        genderCount.put("data", genderCountData);
        genderCount.put("xField", value);
        genderCount.put("yField", genderType);
        return genderCount;
    }

    /**
     * 处理社区居民楼长联系方式关联对象
     *
     * @param entity
     *            单位对象
     * @return 处理完成的对象集合
     */
    private List<DormitoryManagerPhoneNumber> dormitoryManagerPhoneNumbersHandler(DormitoryManager entity) {
        QueryWrapper<PhoneNumber> wrapper = new QueryWrapper<>();
        entity.getPhoneNumbers().forEach(phoneNumber -> wrapper.eq("phone_number", phoneNumber.getPhoneNumber()).or());
        List<PhoneNumber> phoneNumbers = phoneNumberMapper.selectList(wrapper);
        return phoneNumbers.stream().map(phoneNumber -> new DormitoryManagerPhoneNumber()
            .setDormitoryManagerId(entity.getId()).setPhoneNumberId(phoneNumber.getId())).collect(Collectors.toList());
    }

}
