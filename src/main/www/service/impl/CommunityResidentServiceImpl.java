package www.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import utils.CommonUtil;
import utils.DateUtil;
import utils.ExcelUtil;
import www.entity.*;
import www.service.CommunityResidentService;

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

    @Override
    public CommunityResident findCommunityResidentAndCommunityById(Integer id) {
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
    public Map<String, Object> findCommunityResidentByCommunityResident(SystemUser systemUser, Map<String, Object> configurationsMap, CommunityResident communityResident, Long companyId, Long companyRid, Integer pageNumber, Integer pageDataSize) throws Exception {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        List<CommunityResident> communityResidents = null;
        Long count = null;
        Long communityRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("community_role_id"));
        Long subdistrictRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("subdistrict_role_id"));
        if (companyRid.equals(communityRoleId)) {
            communityResident.setCommunityId(companyId);
            count = communityResidentsDao.countCommunityResidentsByCommunityResident(communityResident);
            PageHelper.startPage(pageNumber, pageDataSize);
            communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
        } else if (companyRid.equals(subdistrictRoleId)) {
            List<Community> newCommunities = communitiesDao.selectCommunitiesCorrelationBySubdistrictId(companyId);
            count = getCompany(communityResident, newCommunities);
            PageHelper.startPage(pageNumber, pageDataSize);
            communityResidents = communityResidentsDao.selectCommunityResidentsByCommunityResident(communityResident);
        } else {
            Long systemAdministratorId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_administrator_id"));
            if (systemUser.getRoleId().equals(systemAdministratorId)) {
                count = communityResidentsDao.countCommunityResidentsByCommunityResident(communityResident);
                PageHelper.startPage(pageNumber, pageDataSize);
                communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
            } else if (systemUser.getRoleId().equals(subdistrictRoleId)) {
                List<Community> communities = communitiesDao.selectCommunitiesCorrelationBySubdistrictId(systemUser.getRoleLocationId());
                count = getCompany(communityResident, communities);
                PageHelper.startPage(pageNumber, pageDataSize);
                communityResidents = communityResidentsDao.selectCommunityResidentsByCommunityResident(communityResident);
            } else if (systemUser.getRoleId().equals(communityRoleId)) {
                communityResident.setCommunityId(systemUser.getRoleLocationId());
                count = communityResidentsDao.countCommunityResidentsByCommunityResident(communityResident);
                communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
                PageHelper.startPage(pageNumber, pageDataSize);
            }
        }
        Map<String, Object> map = new HashMap<>(3);
        map.put("dataAndPagination", findObjectsMethod(communityResidents));
        map.put("count", count);
        return map;
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
        List<Subcontractor> subcontractors = subcontractorsDao.selectObjectsAll();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheet = workbook.getSheetAt(i);
            if (sheet != null) {
                // 遍历当前sheet中的所有行
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    row = sheet.getRow(j);
                    if (row != null && j >= sheet.getFirstRowNum() + readExcelStartRowNumber) {
                        CommunityResident resident = new CommunityResident();
                        // 居民姓名
                        resident.setCommunityResidentName(String.valueOf(ExcelUtil.getCellValue(row.getCell(excelCommunityResidentNameCellNumber), CellType.STRING)));
                        // 居民地址
                        // 处理地址和社区名称
                        String address = String.valueOf(ExcelUtil.getCellValue(row.getCell(excelAddressCellNumber), CellType.STRING));
                        Matcher matcher = communityPattern.matcher(address);
                        String communityAliasName = null;
                        String realAddress = null;
                        while (matcher.find()) {
                            communityAliasName = matcher.group(1);
                            realAddress = matcher.group(2);
                        }
                        resident.setCommunityResidentAddress(realAddress);
                        // 居民电话三个合一
                        String phone1 = CommonUtil.qj2bj(CommonUtil.replaceBlank(String.valueOf(ExcelUtil.getCellValue(row.getCell(excelPhone1CellNumber), CellType.STRING))));
                        String phone2 = CommonUtil.qj2bj(CommonUtil.replaceBlank(String.valueOf(ExcelUtil.getCellValue(row.getCell(excelPhone2CellNumber), CellType.STRING))));
                        String phone3 = CommonUtil.qj2bj(CommonUtil.replaceBlank(String.valueOf(ExcelUtil.getCellValue(row.getCell(excelPhone3CellNumber), CellType.STRING))));
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
                        resident.setCommunityResidentPhones(phones.toString());
                        String communityName = String.valueOf(ExcelUtil.getCellValue(row.getCell(excelCommunityCellNumber), CellType.STRING));
                        // 分包人
                        String subcontractor = String.valueOf(ExcelUtil.getCellValue(row.getCell(excelSubcontractorCellNumber), CellType.STRING)).replaceAll(communityName, "");
                        for (Subcontractor sub : subcontractors) {
                            if (subcontractor.equals(sub.getName())) {
                                resident.setSubcontractorId(sub.getSubcontractorId());
                            }
                        }
                        // 社区名称
                        Long communityId = communitiesDao.selectCommunityIdByCommunityName(communityName);
                        if (communityId == null) {
                            throw new BusinessException("社区编号" + communityName);
                        }
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
        return -1;
    }

    @Override
    public Map<String, Object> findCommunityResidentsAndCommunity(SystemUser systemUser, Map<String, Object> configurationsMap, Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        Long roleId = systemUser.getRoleId();
        Long roleLocationId = systemUser.getRoleLocationId();
        List<Long> roleLocationIds = new ArrayList<>();
        Long communityRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("community_role_id"));
        Long subdistrictRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("subdistrict_role_id"));
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
        Long count = communityResidentsDao.countCommunityResidentsAndCommunityByCommunityIds(roleLocationIds);
        PageHelper.startPage(pageNumber, pageDataSize);
        List<CommunityResident> data = communityResidentsDao.selectCommunityResidentsAndCommunityByCommunityIds(roleLocationIds);
        Map<String, Object> map = new HashMap<>(3);
        map.put("dataAndPagination", findObjectsMethod(data));
        map.put("count", count);
        return map;
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
    public JSONArray findCommunityResidentsAndCommunitiesBySystemUserId(Map<String, Object> configurationsMap, Long roleId, Long roleLocationId) {
        List<CommunityResident> communityResidents = null;
        List<Long> communityIds = new ArrayList<>();
        Long systemRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_role_id"));
        Long communityRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("community_role_id"));
        Long subdistrictRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("subdistrict_role_id"));
        if (roleId.equals(systemRoleId)) {
            communityResidents = communityResidentsDao.selectCommunityResidentsAndCommunitiesAndSubdistrictByCommunityIds(null);
        } else if (roleId.equals(communityRoleId)) {
            communityIds.add(roleLocationId);
            communityResidents = communityResidentsDao.selectCommunityResidentsAndCommunitiesAndSubdistrictByCommunityIds(communityIds);
        } else if (roleId.equals(subdistrictRoleId)) {
            List<Community> communities = communitiesDao.selectCommunitiesCorrelationBySubdistrictId(roleLocationId);
            for (Community community : communities) {
                communityIds.add(community.getCommunityId());
            }
            communityResidents = communityResidentsDao.selectCommunityResidentsAndCommunitiesAndSubdistrictByCommunityIds(communityIds);
        }
        if (communityResidents != null) {
            for (CommunityResident communityResident : communityResidents) {
                String communityName = communityResident.getCommunity().getCommunityName().replaceAll("居委会", "").replaceAll("社区", "");
                String subdistrictName = communityResident.getCommunity().getSubdistrict().getSubdistrictName().replaceAll("办事处", "").replaceAll("街道", "");
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
                Subcontractor subcontractor = subcontractorsDao.selectObjectById(communityResident.getSubcontractorId());
                communityResident.setSubcontractorName(communityName + subcontractor.getName());
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
        tableHead.put("communityResidentSubcontractor", "分包人");
        return tableHead;
    }

    @Override
    public Map<String, Object> computedCount(SystemUser systemUser) {
        String roleName = userRolesDao.selectRoleNameById(systemUser.getRoleId());
        Map<String, Object> jsonMap = new HashMap<>(4);
        // 饼图
        Map<String, Object> pieBaseData = new HashMap<>(3);
        Long countCommunityResidents;
        Long actualNumber;
        List<Long> pieData = new ArrayList<>();
        List<String> pieBackgroundColor = new ArrayList<>();
        Map<String, List<?>> pieDataMap = new HashMap<>(3);
        List<Map<String, List<?>>> pieDataSets = new ArrayList<>();
        List<String> pieLabels = new ArrayList<>();
        Map<String, Object> barChartMap = new HashMap<>(3);
        Map<String, Object> barData = new HashMap<>(4);
        List<String> barChartLabels = new ArrayList<>();
        List<Map<String, Object>> barDataSets = new ArrayList<>();
        List<Long> barChartData = new ArrayList<>();
        List<String> barBackgroundColor = new ArrayList<>();
        if (roleName.contains("社区") || roleName.contains("居委会")) {
            countCommunityResidents = communityResidentsDao.countCommunityResidentsByCommunityId(systemUser.getRoleLocationId());
            actualNumber = Long.valueOf(communitiesDao.selectActualNumberByCommunityId(systemUser.getRoleLocationId()));
            Community community = communitiesDao.selectObjectById(systemUser.getRoleLocationId());
            barChartLabels.add(community.getCommunityName());
            barChartData.add(countCommunityResidents);
            barData.put("label", "社区");
        } else if (roleName.contains("街道") || roleName.contains("办事处")) {
            countCommunityResidents = communityResidentsDao.countCommunityResidentsBySubdistrictId(systemUser.getRoleLocationId());
            actualNumber = Long.valueOf(communitiesDao.sumActualNumberBySubdistrictId(systemUser.getRoleLocationId()));
            List<Community> communities = communitiesDao.countCommunitiesBySubdistrictId(systemUser.getRoleLocationId());
            for (Community community : communities) {
                barChartLabels.add(community.getCommunityName());
                // 临时借用此变量
                barChartData.add(Long.valueOf(community.getActualNumber()));
            }
            barData.put("label", "社区");
        } else {
            countCommunityResidents = communityResidentsDao.countCommunityResidents();
            actualNumber = communitiesDao.sumActualNumber();
            List<Subdistrict> subdistricts = subdistrictsDao.countCommunityResidents();
            for (Subdistrict subdistrict : subdistricts) {
                barChartLabels.add(subdistrict.getSubdistrictName());
                // 临时借用此变量
                barChartData.add(subdistrict.getSubdistrictId());
            }
            barData.put("label", "街道");
        }
        pieData.add(countCommunityResidents);
        pieData.add(actualNumber - countCommunityResidents);
        pieBackgroundColor.add("#" + CommonUtil.randomHexString(6));
        pieBackgroundColor.add("#" + CommonUtil.randomHexString(6));
        pieLabels.add("数据库中现存数字");
        pieLabels.add("还应添加数");
        pieDataMap.put("data", pieData);
        pieDataMap.put("backgroundColor", pieBackgroundColor);
        pieDataSets.add(pieDataMap);
        pieBaseData.put("datasets", pieDataSets);
        pieBaseData.put("labels", pieLabels);
        jsonMap.put("pieChart", pieBaseData);
        // 柱状图
        for (int i = 0; i < barChartData.size(); i++) {
            barBackgroundColor.add("#" + CommonUtil.randomHexString(6));
        }
        barData.put("data", barChartData);
        barData.put("backgroundColor", barBackgroundColor);
        barDataSets.add(barData);
        barChartMap.put("labels", barChartLabels);
        barChartMap.put("datasets", barDataSets);
        jsonMap.put("barChart", barChartMap);
        // 提示数据
        Map<String, Long> tipMap = new HashMap<>(3);
        tipMap.put("databaseNumber", countCommunityResidents);
        tipMap.put("actualNumber", actualNumber);
        jsonMap.put("tipData", tipMap);
        return jsonMap;
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
     * 获取单位的数量
     *
     * @param communityResident 需要查找的社区居民信息对象
     * @param communities       社区集合
     * @return 单位的统计数量
     */
    private Long getCompany(CommunityResident communityResident, List<Community> communities) {
        List<Long> communityIds = new ArrayList<>();
        for (Community community : communities) {
            communityIds.add(community.getCommunityId());
        }
        communityResident.setCommunityIds(communityIds);
        return communityResidentsDao.countCommunityResidentsByCommunityResident(communityResident);
    }
}
