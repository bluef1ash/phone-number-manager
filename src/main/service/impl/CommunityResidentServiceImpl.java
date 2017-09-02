package main.service.impl;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.BusinessException;
import main.entity.Community;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import main.entity.CommunityResident;
import main.service.CommunityResidentService;
import utils.CommonUtil;
import utils.ExcelUtil;

/**
 * 社区居民业务实现
 */
@Service("communityResidentService")
public class CommunityResidentServiceImpl extends BaseServiceImpl<CommunityResident> implements CommunityResidentService {

    @Override
    public Map<String, Object> findCommunityResidentAndCommunityById(Integer id) throws Exception {
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
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("communityResident", communityResident);
        return map;
    }

    @Override
    public int createCommunityResident(CommunityResident communityResident) throws Exception {
        return baseDao.insertObject(multiplePhoneHandler(communityResident));
    }

    @Override
    public int updateCommunityResident(CommunityResident communityResident) throws Exception {
        return baseDao.updateObject(multiplePhoneHandler(communityResident));
    }

    @Override
    public Map<String, Object> findCommunityResidentByCommunityResident(CommunityResident communityResident, Integer pageNum, String unit, Integer pageSize) throws Exception {
        String[] communities = unit.split("\\|");
        List<CommunityResident> communityResidents = null;
        if (Integer.parseInt(communities[1]) == 1) {
            communityResident.setCommunityId(Integer.parseInt(communities[0]));
            communityResidents = communityResidentsDao.selectObjectsByObject(communityResident);
        } else {
            List<Community> newCommunities = communitiesDao.selectCommunitiesBySubdistrictId(Integer.parseInt(communities[0]));
            int communitiesLength = newCommunities.size();
            Integer[] communityIds = new Integer[communitiesLength];
            for (int i = 0; i < communitiesLength; i++) {
                communityIds[i] = newCommunities.get(i).getCommunityId();
            }
            communityResident.setCommunityIds(communityIds);
            communityResidents = communityResidentsDao.selectCommunityResidentsByCommunityResidentAndCommunityIds(communityResident);
        }
        return findObjectsMethod(communityResidents, pageNum, pageSize);
    }

    @Override
    public int addCommunityResidentFromExcel(Workbook workbook) throws Exception {
        Sheet sheet = null;
        Row row = null;
        List<CommunityResident> residents = new ArrayList<CommunityResident>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheet = workbook.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            //遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if (row == null || row.getFirstCellNum() == j || ExcelUtil.isMergedRegion(sheet, j, 0) || String.valueOf(ExcelUtil.getCellValue(row.getCell(0))).contains("序号")) {
                    continue;
                }
                residents.add(residentHandler(row));
            }
        }
        int index = communityResidentsDao.truncateTable();
        return communityResidentsDao.insertBatchCommunityResidents(residents);
    }

    @Override
    public Map<String, Object> findCommunityResidentsAndCommunity(Integer pageNum, Integer pageSize) throws Exception {
        List<CommunityResident> data = communityResidentsDao.selectCommunityResidentsAndCommunity();
        return findObjectsMethod(data, pageNum, pageSize);
    }

    @Override
    public List<CommunityResident> findCommunityResidentByNameAndAddress(String nameAddress) throws Exception {
        return communityResidentsDao.selectCommunityResidentByNameAndAddress(nameAddress);
    }

    @Override
    public List<CommunityResident> findCommunityResidentByPhones(List<String> phones) throws Exception {
        return communityResidentsDao.selectCommunityResidentByPhones(phones);
    }

    /**
     * 从Excel导入数据库数据处理
     *
     * @param row
     * @return
     * @throws Exception
     */
    private CommunityResident residentHandler(Row row) throws Exception {
        CommunityResident resident = new CommunityResident();
        // 处理地址和社区名称
        Pattern regex = Pattern.compile("(?iUs)^(.*)(?:社区|居委会)(.*)$");
        String address = String.valueOf(ExcelUtil.getCellValue(row.getCell(2)));
        Matcher matcher = regex.matcher(address);
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
        String phone1 = CommonUtil.replaceBlank(String.valueOf(ExcelUtil.getCellValue(row.getCell(3))).trim());
        String phone2 = CommonUtil.replaceBlank(String.valueOf(ExcelUtil.getCellValue(row.getCell(4))).trim());
        String phone3 = CommonUtil.replaceBlank(String.valueOf(ExcelUtil.getCellValue(row.getCell(5))).trim());
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
        resident.setCommunityResidentSubcontractor(String.valueOf(ExcelUtil.getCellValue(row.getCell(6))).replaceAll(communityAliasName, ""));
        // 社区名称
        Integer communityId = communitiesDao.selectCommunityIdByCommunityName(communityAliasName);
        resident.setCommunityId(communityId);
        return resident;
    }

    /**
     * 联系方式数组处理
     *
     * @param communityResident
     * @return
     */
    private CommunityResident multiplePhoneHandler(CommunityResident communityResident) throws Exception {
        communityResident.setCommunityResidentName(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentName().trim())));
        communityResident.setCommunityResidentAddress(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentAddress().trim())));
        communityResident.setCommunityResidentSubcontractor(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentSubcontractor().trim())));
        // 联系方式数组处理
        StringBuilder tempPhone = new StringBuilder();
        if (!StringUtils.isEmpty(communityResident.getCommunityResidentPhone1())) {
            tempPhone.append(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone1().trim()))).append(",");
        }
        if (!StringUtils.isEmpty(communityResident.getCommunityResidentPhone2())) {
            tempPhone.append(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone2().trim()))).append(",");
        }
        if (!StringUtils.isEmpty(communityResident.getCommunityResidentPhone3())) {
            tempPhone.append(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone3().trim()))).append(",");
        }
        communityResident.setCommunityResidentPhones(tempPhone.toString().substring(0, tempPhone.length() - 1));
        communityResident.setCommunityResidentEditTime(new Timestamp(new Date().getTime()));
        return communityResident;
    }


}
