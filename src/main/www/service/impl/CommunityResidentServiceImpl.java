package www.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import constant.SystemConstant;
import exception.BusinessException;
import www.entity.Community;
import www.entity.CommunityResident;
import www.entity.Subdistrict;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import utils.CommonUtil;
import utils.ExcelUtil;
import www.entity.SystemUser;
import www.service.CommunityResidentService;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
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

    private Pattern communityPattern = Pattern.compile("(?iUs)^(.*)(?:社区|居委会)(.*)$");

    @Override
    public CommunityResident findCommunityResidentAndCommunityById(Integer id) throws Exception {
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
    public int createCommunityResident(CommunityResident communityResident) throws Exception {
        return communityResidentsDao.insertObject(multiplePhoneHandler(communityResident));
    }

    @Override
    public int updateCommunityResident(CommunityResident communityResident) throws Exception {
        return communityResidentsDao.updateObject(multiplePhoneHandler(communityResident));
    }

    @Override
    public Map<String, Object> findCommunityResidentByCommunityResident(SystemUser systemUser, CommunityResident communityResident, String company, Integer pageNum, Integer pageSize) throws Exception {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        List<CommunityResident> communityResidents = null;
        Integer count = null;
        if (StringUtils.isNotEmpty(company)) {
            if (company.contains("社区") || company.contains("居委会")) {
                Community community = new Community();
                community.setCommunityName(company);
                communityResident.setCommunity(community);
                count = communityResidentsDao.countCommunityResidentsByCommunityResident(communityResident);
                PageHelper.startPage(pageNum, pageSize);
                communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
            } else if (company.contains("街道") || company.contains("办事处")) {
                List<Community> newCommunities = communitiesDao.selectCommunitiesBySubdistrictName(company);
                count = getCompany(communityResident, newCommunities);
                PageHelper.startPage(pageNum, pageSize);
                communityResidents = communityResidentsDao.selectCommunityResidentsByCommunityResidentAndCommunityIds(communityResident);
            }
        } else {
            switch (systemUser.getRoleId()) {
                case SystemConstant.SYSTEM_ADMINISTRATOR_ID:
                    count = communityResidentsDao.countCommunityResidentsByCommunityResident(communityResident);
                    PageHelper.startPage(pageNum, pageSize);
                    communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
                    break;
                case SystemConstant.SUBDISTRICT_ROLE_ID:
                    List<Community> communities = communitiesDao.selectCommunitiesBySubdistrictId(systemUser.getRoleLocationId());
                    count = getCompany(communityResident, communities);
                    PageHelper.startPage(pageNum, pageSize);
                    communityResidents = communityResidentsDao.selectCommunityResidentsByCommunityResidentAndCommunityIds(communityResident);
                    break;
                case SystemConstant.COMMUNITY_ROLE_ID:
                    communityResident.setCommunityId(systemUser.getRoleLocationId());
                    count = communityResidentsDao.countCommunityResidentsByCommunityResident(communityResident);
                    communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
                    PageHelper.startPage(pageNum, pageSize);
                    break;
                default:
                    break;
            }
        }
        Map<String, Object> map = new HashMap<>(3);
        map.put("dataAndPagination", findObjectsMethod(communityResidents));
        map.put("count", count);
        return map;
    }

    @Override
    public int addCommunityResidentFromExcel(Workbook workbook) throws Exception {
        Sheet sheet;
        Row row;
        List<CommunityResident> residents = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheet = workbook.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            // 遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if (row == null || row.getFirstCellNum() == j || ExcelUtil.isMergedRegion(sheet, j, 0) || String.valueOf(ExcelUtil.getCellValue(row.getCell(0))).contains("序号")) {
                    continue;
                }
                residents.add(residentHandler(row));
            }
        }
        // 截断表
        communityResidentsDao.truncateTable();
        return communityResidentsDao.insertBatchCommunityResidents(residents);
    }

    @Override
    public Map<String, Object> findCommunityResidentsAndCommunity(SystemUser systemUser, Integer pageNum, Integer pageSize) throws Exception {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        Integer roleId = systemUser.getRoleId();
        Integer roleLocationId = systemUser.getRoleLocationId();
        List<Integer> roleLocationIds = new ArrayList<>();
        switch (roleId) {
            case SystemConstant.COMMUNITY_ROLE_ID:
                roleLocationIds.add(roleLocationId);
                break;
            case SystemConstant.SUBDISTRICT_ROLE_ID:
                List<Community> communities = communitiesDao.selectCommunitiesBySubdistrictId(roleLocationId);
                for (Community community : communities) {
                    roleLocationIds.add(community.getCommunityId());
                }
                break;
            default:
                roleLocationIds = null;
                break;
        }
        Integer count = communityResidentsDao.countCommunityResidentsAndCommunityByCommunityIds(roleLocationIds);
        PageHelper.startPage(pageNum, pageSize);
        List<CommunityResident> data = communityResidentsDao.selectCommunityResidentsAndCommunityByCommunityIds(roleLocationIds);
        Map<String, Object> map = new HashMap<>(3);
        map.put("dataAndPagination", findObjectsMethod(data));
        map.put("count", count);
        return map;
    }

    @Override
    public List<CommunityResident> findCommunityResidentByNameAndAddress(String nameAddress, Integer communityResidentId) throws Exception {
        return communityResidentsDao.selectCommunityResidentByNameAndAddress(nameAddress, communityResidentId);
    }

    @Override
    public List<CommunityResident> findCommunityResidentByPhones(List<String> phones, Integer communityResidentId) throws Exception {
        return communityResidentsDao.selectCommunityResidentByPhones(phones, communityResidentId);
    }

    @Override
    public JSONArray findCommunityResidentsAndCommunitiesBySystemUserId(Integer roleId, Integer roleLocationId) throws Exception {
        List<CommunityResident> communityResidents = null;
        List<Integer> communityIds = new ArrayList<>();
        switch (roleId) {
            case SystemConstant.SYSTEM_ROLE_ID:
                communityResidents = communityResidentsDao.selectCommunityResidentsAndCommunitiesAndSubdistrictByCommunityIds(null);
                break;
            case SystemConstant.COMMUNITY_ROLE_ID:
                communityIds.add(roleLocationId);
                communityResidents = communityResidentsDao.selectCommunityResidentsAndCommunitiesAndSubdistrictByCommunityIds(communityIds);
                break;
            case SystemConstant.SUBDISTRICT_ROLE_ID:
                List<Community> communities = communitiesDao.selectCommunitiesBySubdistrictId(roleLocationId);
                for (Community community : communities) {
                    communityIds.add(community.getCommunityId());
                }
                communityResidents = communityResidentsDao.selectCommunityResidentsAndCommunitiesAndSubdistrictByCommunityIds(communityIds);
                break;
            default:
                break;
        }
        if (communityResidents != null) {
            String[] phones = null;
//            int index = 1;
            for (CommunityResident communityResident : communityResidents) {
                //communityResident.setIndexId(index);
                String communityName = communityResident.getCommunity().getCommunityName().replaceAll("居委会", "").replaceAll("社区", "");
                String subdistrictName = communityResident.getCommunity().getSubdistrict().getSubdistrictName().replaceAll("办事处", "").replaceAll("街道", "");
                communityResident.setSubdistrictName(subdistrictName);
                communityResident.setCommunityName(communityName);
                // 处理多个联系方式
                if (communityResident.getCommunityResidentPhones().contains(",")) {
                    phones = communityResident.getCommunityResidentPhones().split(",");
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
                communityResident.setCommunityResidentSubcontractor(communityName + communityResident.getCommunityResidentSubcontractor());
//                index++;
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
    public Map<String, Object> computedCount(HttpSession session) throws Exception {
        SystemUser systemUser = (SystemUser) session.getAttribute("systemUser");
        String roleName = userRolesDao.selectRoleNameById(systemUser.getRoleId());
        Map<String, Object> jsonMap = new HashMap<>(4);
        // 饼图
        Map<String, Object> pieBaseData = new HashMap<>(3);
        Integer countCommunityResidents;
        Integer actualNumber;
        List<Integer> pieData = new ArrayList<>();
        List<String> pieBackgroundColor = new ArrayList<>();
        Map<String, List<?>> pieDataMap = new HashMap<>(3);
        List<Map<String, List<?>>> pieDataSets = new ArrayList<>();
        List<String> pieLabels = new ArrayList<>();
        Map<String, Object> barChartMap = new HashMap<>(3);
        Map<String, Object> barData = new HashMap<>(4);
        List<String> barChartLabels = new ArrayList<>();
        List<Map<String, Object>> barDataSets = new ArrayList<>();
        List<Integer> barChartData = new ArrayList<>();
        List<String> barBackgroundColor = new ArrayList<>();
        if (roleName.contains("社区") || roleName.contains("居委会")) {
            countCommunityResidents = communityResidentsDao.countCommunityResidentsByCommunityId(systemUser.getRoleLocationId());
            actualNumber = communitiesDao.selectActualNumberByCommunityId(systemUser.getRoleLocationId());
            Community community = communitiesDao.selectObjectById(systemUser.getRoleLocationId());
            barChartLabels.add(community.getCommunityName());
            barChartData.add(countCommunityResidents);
            barData.put("label", "社区");
        } else if (roleName.contains("街道") || roleName.contains("办事处")) {
            countCommunityResidents = communityResidentsDao.countCommunityResidentsBySubdistrictId(systemUser.getRoleLocationId());
            actualNumber = communitiesDao.sumActualNumberBySubdistrictId(systemUser.getRoleLocationId());
            List<Community> communities = communitiesDao.countCommunitiesBySubdistrictId(systemUser.getRoleLocationId());
            for (Community community : communities) {
                barChartLabels.add(community.getCommunityName());
                // 临时借用此变量
                barChartData.add(community.getActualNumber());
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
        Map<String, Integer> tipMap = new HashMap<>(3);
        tipMap.put("databaseNumber", countCommunityResidents);
        tipMap.put("actualNumber", actualNumber);
        jsonMap.put("tipData", tipMap);
        return jsonMap;
    }

    /**
     * 从Excel导入数据库数据处理
     *
     * @param row Excel工作表的行对象
     * @return 处理后的社区居民对象
     */
    private CommunityResident residentHandler(Row row) {
        CommunityResident resident = new CommunityResident();
        // 处理地址和社区名称
        String address = String.valueOf(ExcelUtil.getCellValue(row.getCell(2)));
        Matcher matcher = communityPattern.matcher(address);
        String communityAliasName = null;
        String realAddress = null;
        while (matcher.find()) {
            communityAliasName = matcher.group(1);
            realAddress = matcher.group(2);
        }
        // 居民姓名
        resident.setCommunityResidentName(String.valueOf(ExcelUtil.getCellValue(row.getCell(1))));
        // 居民地址
        resident.setCommunityResidentAddress(realAddress);
        // 居民电话三个合一
        String phone1 = CommonUtil.qj2bj(CommonUtil.replaceBlank(String.valueOf(ExcelUtil.getCellValue(row.getCell(3)))));
        String phone2 = CommonUtil.qj2bj(CommonUtil.replaceBlank(String.valueOf(ExcelUtil.getCellValue(row.getCell(4)))));
        String phone3 = CommonUtil.qj2bj(CommonUtil.replaceBlank(String.valueOf(ExcelUtil.getCellValue(row.getCell(5)))));
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
        // 分包人
        if (communityAliasName == null) {
            throw new BusinessException("社区名称没有找到！");
        }
        resident.setCommunityResidentSubcontractor(String.valueOf(ExcelUtil.getCellValue(row.getCell(6))).replaceAll(communityAliasName, ""));
        // 社区名称
        Integer communityId = communitiesDao.selectCommunityIdByCommunityName(communityAliasName);
        if (communityId == null) {
            throw new BusinessException("社区编号" + communityAliasName);
        }
        resident.setCommunityId(communityId);
        return resident;
    }

    /**
     * 添加、修改到数据库前处理
     *
     * @param communityResident 需要处理的社区居民对象
     * @return 处理后的社区居民对象
     */
    private CommunityResident multiplePhoneHandler(CommunityResident communityResident) {
//        社区居民姓名
        communityResident.setCommunityResidentName(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentName())).replaceAll("—", "-"));
//        社区居民地址
        communityResident.setCommunityResidentAddress(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentAddress())).replaceAll("—", "-"));
//        社区分包人
        communityResident.setCommunityResidentSubcontractor(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentSubcontractor())).replaceAll("—", "-"));
//        联系方式数组处理
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
//        编辑时间
        communityResident.setCommunityResidentEditTime(new Timestamp(System.currentTimeMillis()));
        return communityResident;
    }

    /**
     * 获取单位的数量
     *
     * @param communityResident 需要查找的社区居民信息对象
     * @param communities 社区集合
     * @return 单位的统计数量
     * @throws Exception DAO层异常
     */
    private Integer getCompany(CommunityResident communityResident, List<Community> communities) throws Exception {
        int communitiesLength = communities.size();
        Integer[] communityIds = new Integer[communitiesLength];
        for (int i = 0; i < communitiesLength; i++) {
            communityIds[i] = communities.get(i).getCommunityId();
        }
        communityResident.setCommunityIds(communityIds);
        return communityResidentsDao.countCommunityResidentsByCommunityResident(communityResident);
    }
}
