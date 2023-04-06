package com.github.phonenumbermanager.service.impl;

import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
import com.github.phonenumbermanager.constant.enums.ImportOrExportStatusEnum;
import com.github.phonenumbermanager.dto.CommunityResidentExcelDTO;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.handler.CommunityResidentTitleSheetWriteHandler;
import com.github.phonenumbermanager.listener.CommunityResidentImportReadListener;
import com.github.phonenumbermanager.mapper.*;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * 社区居民信息业务实现
 *
 * @author 廿二月的天
 */
@Service
@AllArgsConstructor
public class CommunityResidentServiceImpl extends BaseServiceImpl<CommunityResidentMapper, CommunityResident>
    implements CommunityResidentService {
    private final CommunityResidentPhoneNumberMapper communityResidentPhoneNumberMapper;
    private final PhoneNumberMapper phoneNumberMapper;
    private final CompanyMapper companyMapper;
    private final SubcontractorMapper subcontractorMapper;
    private final RedisUtil redisUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(CommunityResident entity) {
        boolean isSuccess = baseMapper.insert(entity) > 0;
        phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers());
        communityResidentPhoneNumberMapper.insertBatchSomeColumn(phoneNumbersHandler(entity));
        return isSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(CommunityResident entity) {
        boolean isSuccess = baseMapper.updateById(entity) > 0;
        if (phoneNumberMapper.insertIgnoreBatchSomeColumn(entity.getPhoneNumbers()) > 0) {
            communityResidentPhoneNumberMapper.delete(new LambdaQueryWrapper<CommunityResidentPhoneNumber>()
                .eq(CommunityResidentPhoneNumber::getCommunityResidentId, entity.getId()));
            communityResidentPhoneNumberMapper.insertBatchSomeColumn(phoneNumbersHandler(entity));
        }
        return isSuccess;
    }

    @Override
    public CommunityResident getCorrelation(Long id) {
        CommunityResident communityResident = baseMapper.selectAndCompanyById(id);
        List<String> info = new ArrayList<>();
        info.add(communityResident.getCompany().getName());
        info.add(communityResident.getSubcontractor().getName());
        communityResident.setSubcontractorInfo(info);
        return communityResident;
    }

    @Override
    public IPage<CommunityResident> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize,
        JSONObject search, JSONObject sort) {
        if (search != null && !search.isEmpty()) {
            JSONObject systemUser = search.get("systemUser", JSONObject.class, true);
            if (systemUser != null && !systemUser.isEmpty()) {
                JSONArray username = systemUser.get("username", JSONArray.class);
                List<Long> ids =
                    username.toList(Long[].class).stream().map(id -> id[id.length - 1]).collect(Collectors.toList());
                search.set("systemUser", ids);
            }
        }
        List<Company> companyList = systemUserCompanyHandler(companies, companyMapper.selectList(null));
        Page<CommunityResident> page = new Page<>(pageNumber, pageDataSize);
        page.setSearchCount(false);
        page.setTotal(baseMapper.selectCorrelationCountByCompanies(companyList, search, sort));
        return baseMapper.selectCorrelationByCompanies(page, companyList, search, sort);
    }

    @Async
    @Override
    public void asyncImport(InputStream inputStream, int startRowNumber, Long importId) {
        CommunityResidentImportReadListener listener =
            new CommunityResidentImportReadListener(redisUtil, importId, this, companyMapper.selectList(null),
                subcontractorMapper.selectList(null), phoneNumberMapper.selectList(null));
        EasyExcel.read(inputStream, CommunityResidentExcelDTO.class, listener).headRowNumber(startRowNumber).sheet(0)
            .doRead();
        redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + importId,
            ImportOrExportStatusEnum.DONE.getValue(), 20, TimeUnit.MINUTES);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(List<CommunityResidentExcelDTO> communityResidentExcelDTOs, List<Company> companyAll,
        List<Subcontractor> subcontractorAll, List<PhoneNumber> phoneNumberAll) {
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        List<CommunityResidentPhoneNumber> communityResidentPhoneNumbers = new ArrayList<>();
        List<CommunityResident> residents = importExcelDataHandle(communityResidentExcelDTOs, phoneNumbers,
            communityResidentPhoneNumbers, subcontractorAll, companyAll, phoneNumberAll);
        if (!residents.isEmpty()) {
            LambdaQueryWrapper<CommunityResidentPhoneNumber> communityResidentPhoneNumberWrapper =
                new LambdaQueryWrapper<>();
            boolean isSuccess = baseMapper.insertBatchSomeColumn(residents) > 0;
            residents.forEach(communityResident -> communityResidentPhoneNumberWrapper
                .eq(CommunityResidentPhoneNumber::getCommunityResidentId, communityResident.getId()).or());
            communityResidentPhoneNumberMapper.delete(communityResidentPhoneNumberWrapper);
            phoneNumberMapper.insertIgnoreBatchSomeColumn(phoneNumbers);
            communityResidentPhoneNumberMapper.insertBatchSomeColumn(communityResidentPhoneNumbers);
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
        String excelResidentTitleUp = Convert.toStr(configurationMap.get("excel_resident_title_up").get("content"));
        String excelResidentTitle = Convert.toStr(configurationMap.get("excel_resident_title").get("content"));
        List<Company> companyAll = companyMapper.selectList(null);
        List<Long> subordinateCompanyIds =
            exportExcelGetSubordinateCompanyIds(currentSystemUser, configurationMap, companyAll);
        List<CommunityResident> communityResidents = getListByCompanyIds(subordinateCompanyIds);
        List<CommunityResidentExcelDTO> exportDTOs = communityResidents.stream().map(communityResident -> {
            CommunityResidentExcelDTO communityResidentExcelDto = new CommunityResidentExcelDTO();
            Company companyParentName = companyAll.stream()
                .filter(company -> company.getId().equals(communityResident.getCompany().getParentId())).findFirst()
                .orElse(null);
            String streetName = "";
            if (companyParentName != null) {
                streetName = companyParentName.getName().replaceAll(SystemConstant.STREET_NAME_PATTERN, "");
            }
            String communityName =
                communityResident.getCompany().getName().replaceAll(SystemConstant.COMMUNITY_NAME_PATTERN, "");
            for (int i = 0; i < communityResident.getPhoneNumbers().size(); i++) {
                if (i == 0) {
                    communityResidentExcelDto.setPhone1(communityResident.getPhoneNumbers().get(i).getPhoneNumber());
                } else if (i == 1) {
                    communityResidentExcelDto.setPhone2(communityResident.getPhoneNumbers().get(i).getPhoneNumber());
                } else if (i == 2) {
                    communityResidentExcelDto.setPhone3(communityResident.getPhoneNumbers().get(i).getPhoneNumber());
                } else {
                    break;
                }
            }
            communityResidentExcelDto.setStreetName(streetName);
            communityResidentExcelDto.setCommunityName(communityName);
            communityResidentExcelDto.setName(communityResident.getName());
            communityResidentExcelDto.setAddress(communityResident.getAddress());
            communityResidentExcelDto.setSubcontractor(communityResident.getSubcontractor().getName());
            return communityResidentExcelDto;
        }).toList();
        String fileName = FileUtil.getTmpDirPath() + SystemConstant.EXPORT_ID_KEY + exportId + ".xlsx";
        ExcelWriter excelWriter = EasyExcel.write(fileName, CommunityResidentExcelDTO.class)
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .registerWriteHandler(new CommunityResidentTitleSheetWriteHandler(excelResidentTitleUp, excelResidentTitle))
            .registerWriteHandler(getHeadAndContentStyle()).useDefaultStyle(false).relativeHeadRowIndex(3).build();
        try (excelWriter) {
            if (exportDTOs.isEmpty()) {
                excelWriter.write(exportDTOs, EasyExcel.writerSheet().build());
            } else {
                for (int i = 0; i < Math.ceil((double)exportDTOs.size() / SystemConstant.WRITE_FILE_SIZE); i++) {
                    List<CommunityResidentExcelDTO> data = ListUtil.sub(exportDTOs, i * SystemConstant.WRITE_FILE_SIZE,
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
    public Map<String, Object> getBaseMessage(List<Company> companies, Long[] companyIds) {
        List<Company> companyAll = companyMapper.selectList(null);
        Long[] systemUserCompanyParentIds = frontendUserRequestCompanyHandle(companies, companyAll, companyIds);
        return computedBaseMessage(systemUserCompanyParentIds, companyAll);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        return baseMapper.deleteById(id) > 0 && communityResidentPhoneNumberMapper
            .delete(new QueryWrapper<CommunityResidentPhoneNumber>().eq("community_resident_id", id)) > 0;
    }

    @Override
    public List<CommunityResident> listByPhoneNumbers(Long id, List<PhoneNumber> phoneNumbers) {
        return baseMapper.selectCorrelationByPhoneNumbers(id, phoneNumbers);
    }

    /**
     * 计算录入统计信息
     *
     * @param companyIds
     *            需要获取的单位编号集合
     * @param companyAll
     *            所有单位集合
     * @return 录入统计信息
     */
    private Map<String, Object> computedBaseMessage(Long[] companyIds, List<Company> companyAll) {
        Map<String, Object> baseMessage = new HashMap<>(2);
        long inputCount = 0;
        long haveToCount = 0;
        if (!ArrayUtil.isEmpty(companyIds)) {
            QueryWrapper<CommunityResident> wrapper = new QueryWrapper<>();
            List<Long> companyIdList =
                Arrays.stream(companyIds).map(companyId -> CommonUtil.listRecursionCompanies(companyAll, companyId))
                    .flatMap(List::stream).map(Company::getId).collect(Collectors.toList());
            if (companyIdList.isEmpty()) {
                companyIdList = Arrays.asList(companyIds);
            }
            wrapper.in("company_id", companyIdList);
            wrapper.select("COUNT(id) AS 'inputCount'", "COUNT(DISTINCT subcontractor_id) AS 'haveToCount'");
            List<Map<String, Object>> countMaps = baseMapper.selectMaps(wrapper);
            inputCount = (long)countMaps.get(0).get("inputCount");
            haveToCount = ((long)countMaps.get(0).get("haveToCount")) * 300;
        }
        baseMessage.put("inputCount", inputCount);
        baseMessage.put("haveToCount", haveToCount);
        baseMessage.put("loading", false);
        return baseMessage;
    }

    /**
     * 导入 Excel 文件数据处理
     *
     * @param data
     *            文件内数据
     * @param phoneNumbers
     *            需要加入的联系方式集合
     * @param communityResidentPhoneNumbers
     *            需要导入的关联集合
     * @param subcontractorAll
     *            所有社区分包人集合
     * @param companyAll
     *            所有单位集合
     * @param phoneNumberAll
     *            所有联系方式集合
     * @return 处理完成后社区居民信息集合
     */
    private List<CommunityResident> importExcelDataHandle(List<CommunityResidentExcelDTO> data,
        List<PhoneNumber> phoneNumbers, List<CommunityResidentPhoneNumber> communityResidentPhoneNumbers,
        List<Subcontractor> subcontractorAll, List<Company> companyAll, List<PhoneNumber> phoneNumberAll) {
        return data.stream().map(datum -> {
            CommunityResident communityResident = new CommunityResident();
            String companyName = StrUtil.cleanBlank(datum.getCommunityName());
            Company company =
                companyAll.stream().filter(c -> c.getName().contains(companyName)).findFirst().orElse(null);
            if (company == null) {
                throw new BusinessException("未找到对应的" + companyName + "单位读取失败！");
            }
            String subcontractorName = StrUtil.cleanBlank(datum.getSubcontractor()).replace(companyName, "");
            Subcontractor subcontractor = subcontractorAll.stream()
                .filter(s -> subcontractorName.equals(s.getName()) && s.getCompanyId().equals(company.getId()))
                .findFirst().orElse(null);
            if (subcontractor == null) {
                throw new BusinessException("未找到对应的分包人" + subcontractorName + "的信息，请重试！");
            }
            communityResident.setId(IdWorker.getId()).setName(StrUtil.cleanBlank(datum.getName()))
                .setAddress(StrUtil.cleanBlank(datum.getAddress())).setCompanyId(company.getId())
                .setSubcontractorId(subcontractor.getId());
            // 联系方式
            List<PhoneNumber> phones =
                CommonUtil.phoneNumber2List(phoneNumberAll, datum.getPhone1(), datum.getPhone2(), datum.getPhone3());
            communityResidentPhoneNumbers.addAll(phones.stream().map(phoneNumber -> {
                CommunityResidentPhoneNumber communityResidentPhoneNumber = new CommunityResidentPhoneNumber();
                phoneNumbers.add(phoneNumber);
                communityResidentPhoneNumber.setCommunityResidentId(communityResident.getId())
                    .setPhoneNumberId(phoneNumber.getId());
                return communityResidentPhoneNumber;
            }).toList());
            return communityResident;
        }).toList();
    }

    /**
     * 处理社区居民联系方式关联对象
     *
     * @param entity
     *            单位对象
     * @return 处理完成的对象集合
     */
    private List<CommunityResidentPhoneNumber> phoneNumbersHandler(CommunityResident entity) {
        LambdaQueryWrapper<PhoneNumber> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PhoneNumber::getPhoneNumber, entity.getPhoneNumbers());
        List<PhoneNumber> phoneNumbers = phoneNumberMapper.selectList(wrapper);
        return entity.getPhoneNumbers().stream().map(
            phoneNumber -> new CommunityResidentPhoneNumber().setCommunityResidentId(entity.getId()).setPhoneNumberId(
                phoneNumbers.stream().filter(p -> p.getPhoneNumber().equals(phoneNumber.getPhoneNumber())).findFirst()
                    .map(PhoneNumber::getId).orElse(phoneNumber.getId())))
            .collect(Collectors.toList());
    }
}
