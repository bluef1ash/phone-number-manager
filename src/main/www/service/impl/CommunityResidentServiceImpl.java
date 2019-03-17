package www.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import constant.SystemConstant;
import exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import utils.CommonUtil;
import utils.DateUtil;
import utils.ExcelUtil;
import www.entity.Community;
import www.entity.CommunityResident;
import www.entity.Subcontractor;
import www.entity.SystemUser;
import www.service.CommunityResidentService;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 社区居民业务实现
 *
 * @author 廿二月的天
 */
@Service("communityResidentService")
public class CommunityResidentServiceImpl extends BaseServiceImpl<CommunityResident> implements CommunityResidentService {

    private Pattern communityPattern = Pattern.compile("(?iUs)^(.*[社区居委会])?(.*)$");

    @Resource(name = "transactionManager")
    private DataSourceTransactionManager transactionManager;

    @Override
    public CommunityResident findCommunityResidentAndCommunityById(Long id) {
        CommunityResident communityResident = communityResidentsDao.selectCommunityResidentAndCommunityById(id);
        String[] residentPhones = communityResident.getCommunityResidentPhones().split(",");
        switch (residentPhones.length) {
            case 1:
                communityResident.setCommunityResidentPhone1(residentPhones[0]);
                break;
            case 2:
                communityResident.setCommunityResidentPhone1(residentPhones[0]);
                communityResident.setCommunityResidentPhone2(residentPhones[1]);
                break;
            case 3:
                communityResident.setCommunityResidentPhone1(residentPhones[0]);
                communityResident.setCommunityResidentPhone2(residentPhones[1]);
                communityResident.setCommunityResidentPhone3(residentPhones[2]);
                break;
            default:
                throw new BusinessException("联系方式处理错误！");
        }
        return communityResident;
    }

    @Override
    public void createCommunityResident(CommunityResident communityResident) {
        communityResidentsDao.insertObject(multiplePhoneHandler(communityResident));
    }

    @Override
    public void updateCommunityResident(CommunityResident communityResident) {
        communityResidentsDao.updateObject(multiplePhoneHandler(communityResident));
    }

    @Override
    public Map<String, Object> findCommunityResidentByCommunityResident(SystemUser systemUser, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId, Long systemAdministratorId, CommunityResident communityResident, Long companyId, Long companyLocationId, Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        List<CommunityResident> communityResidents = null;
        if (companyLocationId != null && companyLocationId.equals(communityRoleId)) {
            communityResident.setCommunityId(companyId);
            PageHelper.startPage(pageNumber, pageDataSize);
            communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
        } else if (companyLocationId != null && companyLocationId.equals(subdistrictRoleId)) {
            PageHelper.startPage(pageNumber, pageDataSize);
            communityResidents = communityResidentsDao.selectCommunityResidentsByCommunityResident(communityResident);
        } else {
            if (systemUser.getRoleId().equals(systemAdministratorId)) {
                PageHelper.startPage(pageNumber, pageDataSize);
                communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
            } else if (systemUser.getRoleId().equals(subdistrictRoleId)) {
                PageHelper.startPage(pageNumber, pageDataSize);
                communityResidents = communityResidentsDao.selectCommunityResidentsByCommunityResident(communityResident);
            } else if (systemUser.getRoleId().equals(communityRoleId)) {
                communityResident.setCommunityId(systemUser.getRoleLocationId());
                communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
                PageHelper.startPage(pageNumber, pageDataSize);
            }
        }
        return findObjectsMethod(communityResidents);
    }

    @Override
    public int addCommunityResidentFromExcel(Workbook workbook, Long subdistrictId, Map<String, Object> configurationsMap) {
        Sheet sheet;
        Row row;
        List<CommunityResident> residents = new ArrayList<>();
        Long readExcelStartRowNumber = CommonUtil.convertConfigurationLong(configurationsMap.get("read_excel_start_row_number"));
        Integer excelCommunityResidentNameCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_community_resident_name_cell_number"));
        Integer excelAddressCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_address_cell_number"));
        Integer excelPhone1CellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_phone1_cell_number"));
        Integer excelPhone2CellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_phone2_cell_number"));
        Integer excelPhone3CellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_phone3_cell_number"));
        Integer excelCommunityCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_community_cell_number"));
        Integer excelSubcontractorCellNumber = CommonUtil.convertConfigurationInteger(configurationsMap.get("excel_subcontractor_cell_number"));
        Set<Subcontractor> subcontractorSet = new HashSet<>();
        List<Subcontractor> subcontractors = subcontractorsDao.selectObjectsAll();
        List<Community> communities = communitiesDao.selectCommunitiesBySubdistrictId(subdistrictId);
        Map<String, Long> communityMap = new HashMap<>(communities.size() + 1);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheet = workbook.getSheetAt(i);
            if (sheet != null) {
                // 遍历当前sheet中的所有行
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    row = sheet.getRow(j);
                    if (row != null && j >= sheet.getFirstRowNum() + readExcelStartRowNumber) {
                        CommunityResident resident = new CommunityResident();
                        // 居民姓名
                        resident.setCommunityResidentName(convertCellString(row.getCell(excelCommunityResidentNameCellNumber)));
                        // 居民地址
                        // 处理地址和社区名称
                        String address = convertCellString(row.getCell(excelAddressCellNumber));
                        Matcher matcher = communityPattern.matcher(address);
                        String realAddress = null;
                        while (matcher.find()) {
                            realAddress = matcher.group(2);
                        }
                        resident.setCommunityResidentAddress(realAddress);
                        // 居民电话三个合一
                        Cell phone1Cell = row.getCell(excelPhone1CellNumber);
                        Cell phone2Cell = row.getCell(excelPhone2CellNumber);
                        Cell phone3Cell = row.getCell(excelPhone3CellNumber);
                        String phones = residentPhoneHandler(phone1Cell, phone2Cell, phone3Cell);
                        resident.setCommunityResidentPhones(phones);
                        // 社区名称
                        String communityName = convertCellString(row.getCell(excelCommunityCellNumber));
                        Long communityId;
                        if (!communityMap.containsKey(communityName)) {
                            communityId = communitiesDao.selectCommunityIdByCommunityName(communityName);
                            if (communityId == null) {
                                throw new BusinessException("找不到社区名称为“" + communityName + "”的社区，请创建此社区后，重新导入！");
                            }
                            communityMap.put(communityName, communityId);
                        } else {
                            communityId = communityMap.get(communityName);
                        }
                        // 分包人
                        String subcontractorName = convertCellString(row.getCell(excelSubcontractorCellNumber)).replaceAll(communityName, "");
                        Long subcontractorId = addSubcontractorHandler(subcontractorName, subcontractors, communityId, subcontractorSet);
                        resident.setSubcontractorId(subcontractorId);
                        resident.setCommunityId(communityId);
                        residents.add(resident);
                    }
                }
            }
        }
        if (residents.size() > 0) {
            communityResidentsDao.deleteCommunityResidentsBySubdistrictId(subdistrictId);
            return communityResidentsDao.insertBatchCommunityResidents(residents);
        }
        return 0;
    }

    @Override
    public Map<String, Object> findCommunityResidentsAndCommunity(SystemUser systemUser, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId, Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        Long roleId = systemUser.getRoleId();
        Long roleLocationId = systemUser.getRoleLocationId();
        List<Long> roleLocationIds = new ArrayList<>();
        if (roleId.equals(communityRoleId)) {
            roleLocationIds.add(roleLocationId);
        } else if (roleId.equals(subdistrictRoleId)) {
            List<Community> communities = communitiesDao.selectCommunitiesCorrelationBySubdistrictId(roleLocationId);
            for (Community community : communities) {
                roleLocationIds.add(community.getCommunityId());
            }
        } else {
            roleLocationIds = null;
        }
        PageHelper.startPage(pageNumber, pageDataSize);
        List<CommunityResident> data = communityResidentsDao.selectCommunityResidentsAndCommunityByCommunityIds(roleLocationIds);
        return findObjectsMethod(data);
    }

    @Override
    public List<CommunityResident> findCommunityResidentByNameAndAddress(String nameAddress, Long communityResidentId) {
        return communityResidentsDao.selectCommunityResidentByNameAndAddress(nameAddress, communityResidentId);
    }

    @Override
    public List<CommunityResident> findCommunityResidentByPhones(List<String> phones, Long communityResidentId) {
        return communityResidentsDao.selectCommunityResidentByPhones(phones, communityResidentId);
    }

    @Override
    public JSONArray findCommunityResidentsAndCommunitiesBySystemUserId(Map<String, Object> configurationsMap, List<Map<String, Object>> userData) {
        Long communityRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("community_role_id"));
        Long subdistrictRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("subdistrict_role_id"));
        List<CommunityResident> communityResidents = communityResidentsDao.selectCommunityResidentsByUserData(userData, communityRoleId, subdistrictRoleId);
        if (communityResidents != null) {
            for (CommunityResident communityResident : communityResidents) {
                String communityName = communityResident.getCommunity().getCommunityName().replaceAll(SystemConstant.COMMUNITY_ALIAS_NAME, "").replaceAll(SystemConstant.COMMUNITY_NAME, "");
                String subdistrictName = communityResident.getCommunity().getSubdistrict().getSubdistrictName().replaceAll(SystemConstant.SUBDISTRICT_ALIAS_NAME, "").replaceAll(SystemConstant.SUBDISTRICT_NAME, "");
                communityResident.setSubdistrictName(subdistrictName);
                communityResident.setCommunityName(communityName);
                // 处理多个联系方式
                if (communityResident.getCommunityResidentPhones().contains(",")) {
                    String[] phones = communityResident.getCommunityResidentPhones().split(",");
                    if (phones.length == 2) {
                        communityResident.setCommunityResidentPhone1(phones[0]);
                        communityResident.setCommunityResidentPhone2(phones[1]);
                    } else {
                        communityResident.setCommunityResidentPhone1(phones[0]);
                        communityResident.setCommunityResidentPhone2(phones[1]);
                        communityResident.setCommunityResidentPhone3(phones[2]);
                    }
                } else {
                    communityResident.setCommunityResidentPhone1(communityResident.getCommunityResidentPhones());
                }
                // 处理地址
                // 处理分包人
                communityResident.setSubcontractorName(communityName + communityResident.getSubcontractor().getName());
            }
        }
        return (JSONArray) JSON.toJSON(communityResidents);
    }

    @Override
    public Map<String, String> getPartStatHead() {
        Map<String, String> tableHead = new LinkedHashMap<>();
        tableHead.put("subdistrictName", "街道");
        tableHead.put("communityName", "社区");
        tableHead.put("communityResidentName", "户主姓名");
        tableHead.put("communityResidentAddress", "家庭地址");
        tableHead.put("communityResidentPhone1", "电话1");
        tableHead.put("communityResidentPhone2", "电话2");
        tableHead.put("communityResidentPhone3", "电话3");
        tableHead.put("subcontractorName", "分包人");
        return tableHead;
    }

    @Override
    public Map<String, Object> computedCount(SystemUser systemUser, Integer getType, Long companyId, Long companyType, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId) {
        Map<String, Object> computed = new HashMap<>(3);
        if (getType == null || getType == 0) {
            computed.put("baseMessage", getBaseMessage(companyId, companyType, systemRoleId, communityRoleId, subdistrictRoleId));
            computed.put("barChart", getChartBar(systemUser, companyId, companyType, systemRoleId, communityRoleId, subdistrictRoleId));
        } else if (getType == 1) {
            computed.put("baseMessage", getBaseMessage(companyId, companyType, systemRoleId, communityRoleId, subdistrictRoleId));
        } else {
            computed.put("barChart", getChartBar(systemUser, companyId, companyType, systemRoleId, communityRoleId, subdistrictRoleId));
        }
        return computed;
    }

    /**
     * 添加、修改到数据库前处理
     *
     * @param communityResident 需要处理的社区居民对象
     * @return 处理后的社区居民对象
     */
    private CommunityResident multiplePhoneHandler(CommunityResident communityResident) {
        // 社区居民姓名
        communityResident.setCommunityResidentName(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentName())).replaceAll("—", "-"));
        // 社区居民地址
        communityResident.setCommunityResidentAddress(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentAddress())).replaceAll("—", "-"));
        // 联系方式数组处理
        StringBuilder tempPhone = new StringBuilder();
        if (!StringUtils.isEmpty(communityResident.getCommunityResidentPhone1())) {
            tempPhone.append(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone1())).replaceAll("—", "-")).append(",");
        }
        if (!StringUtils.isEmpty(communityResident.getCommunityResidentPhone2())) {
            tempPhone.append(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone2())).replaceAll("—", "-")).append(",");
        }
        if (!StringUtils.isEmpty(communityResident.getCommunityResidentPhone3())) {
            tempPhone.append(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone3())).replaceAll("—", "-")).append(",");
        }
        communityResident.setCommunityResidentPhones(tempPhone.toString().substring(0, tempPhone.length() - 1));
        // 编辑时间
        communityResident.setCommunityResidentEditTime(DateUtil.getTimestamp(new Date()));
        return communityResident;
    }

    /**
     * 社区居民联系方式处理
     *
     * @param phone1Cell 联系方式一单元格对象
     * @param phone2Cell 联系方式二单元格对象
     * @param phone3Cell 联系方式三单元格对象
     * @return 联系方式拼接字符串
     */
    private String residentPhoneHandler(Cell phone1Cell, Cell phone2Cell, Cell phone3Cell) {
        String phone1 = convertCellString(phone1Cell);
        String phone2 = convertCellString(phone2Cell);
        String phone3 = convertCellString(phone3Cell);
        StringBuilder phones = new StringBuilder();
        if (!StringUtils.isEmpty(phone1)) {
            phones.append(phone1);
        }
        if (!StringUtils.isEmpty(phone2)) {
            phones.append(",").append(phone2);
        }
        if (!StringUtils.isEmpty(phone3)) {
            phones.append(",").append(phone3);
        }
        return phones.toString();
    }

    /**
     * 分包人处理
     *
     * @param subcontractorName 分包人姓名
     * @param subcontractors    从数据库查询的分包人集合对象
     * @param communityId       所属社区编号
     * @param subcontractorSet  分包人集合
     * @return 分包人编号
     */
    private Long addSubcontractorHandler(String subcontractorName, List<Subcontractor> subcontractors, Long communityId, Set<Subcontractor> subcontractorSet) {
        Long subcontractorId = null;
        for (Subcontractor subcontractor : subcontractors) {
            if (subcontractorName.equals(subcontractor.getName())) {
                subcontractorId = subcontractor.getSubcontractorId();
            }
        }
        if (subcontractorId == null && subcontractorSet.size() > 0) {
            for (Subcontractor subcontractor : subcontractorSet) {
                if (subcontractorName.equals(subcontractor.getName())) {
                    subcontractorId = subcontractor.getSubcontractorId();
                }
            }
        }
        if (subcontractorId == null) {
            Subcontractor newSubcontractor = new Subcontractor();
            newSubcontractor.setName(subcontractorName);
            newSubcontractor.setTelephone("");
            newSubcontractor.setCommunityId(communityId);
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            subcontractorId = subcontractorsDao.insertObject(newSubcontractor);
            transactionManager.commit(status);
            newSubcontractor.setSubcontractorId(subcontractorId);
            subcontractorSet.add(newSubcontractor);
        }
        return subcontractorId;
    }

    /**
     * 转换单元格字符串
     *
     * @param cell 需要转换的字符串的单元格对象
     * @return 转换成功的字符串
     */
    private String convertCellString(Cell cell) {
        return CommonUtil.qj2bj(CommonUtil.replaceBlank(String.valueOf(ExcelUtil.getCellValue(cell, CellType.STRING))));
    }

    /**
     * 获取录入统计信息
     *
     * @param companyId         单位编号
     * @param companyType       单位类型
     * @param systemRoleId      系统用户角色编号
     * @param communityRoleId   社区级用户角色编号
     * @param subdistrictRoleId 街道级用户角色编号
     * @return 统计信息对象
     */
    private Map<String, Object> getBaseMessage(Long companyId, Long companyType, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId) {
        Map<String, Object> baseMessage = new HashMap<>(3);
        Long addCount = null;
        Long haveToCount = null;
        boolean isSystemRole = companyType == null || companyId == null || companyType.equals(systemRoleId);
        if (isSystemRole) {
            addCount = communityResidentsDao.countCommunityResidents();
            haveToCount = communitiesDao.sumActualNumber();
        } else if (companyType.equals(communityRoleId)) {
            addCount = communityResidentsDao.countCommunityResidentsByCommunityId(companyId);
            haveToCount = communitiesDao.selectActualNumberByCommunityId(companyId);
        } else if (companyType.equals(subdistrictRoleId)) {
            addCount = communityResidentsDao.countCommunityResidentsBySubdistrictId(companyId);
            haveToCount = communitiesDao.sumActualNumberBySubdistrictId(companyId);
        }
        baseMessage.put("addCount", addCount);
        baseMessage.put("haveToCount", haveToCount);
        return baseMessage;
    }

    /**
     * 获取柱状图数据
     *
     * @param systemUser        正在登录中的系统用户对象
     * @param companyId         单位编号
     * @param companyType       单位类型
     * @param systemRoleId      系统用户角色编号
     * @param communityRoleId   社区级用户角色编号
     * @param subdistrictRoleId 街道级用户角色编号
     * @return 柱状图数据
     */
    private Map<String, Object> getChartBar(SystemUser systemUser, Long companyId, Long companyType, Long systemRoleId, Long communityRoleId, Long subdistrictRoleId) {
        Map<String, Object> barChartMap = new HashMap<>(3);
        List<String> columns = new ArrayList<>();
        String companyLabel;
        String label = "社区居民人数";
        Map<String, Map<String, Object>> communityResidents;
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> data = new HashMap<>(3);
        List<String> titleLabel = new ArrayList<>();
        if (companyType == null || companyType.equals(systemRoleId)) {
            companyLabel = "街道";
            communityResidents = communityResidentsDao.countCommunityResidentsForGroupSubdistrict();
        } else if (communityRoleId.equals(systemUser.getRoleId())) {
            companyLabel = "社区";
            communityResidents = communityResidentsDao.countCommunityResidentsGroupByCommunityId(companyId);
        } else {
            companyLabel = "社区";
            communityResidents = communityResidentsDao.countCommunityResidentsForGroupCommunity(companyId);
        }
        for (Map<String, Object> residentCount : communityResidents.values()) {
            Map<String, Object> row = new HashMap<>(3);
            row.put(companyLabel, residentCount.get("name"));
            titleLabel.add((String) residentCount.get("name"));
            row.put(label, residentCount.get("value"));
            rows.add(row);
        }
        columns.add(companyLabel);
        columns.add(label);
        barChartMap.put("columns", columns);
        barChartMap.put("rows", rows);
        data.put("data", barChartMap);
        data.put("titleLabel", titleLabel);
        return data;
    }
}
