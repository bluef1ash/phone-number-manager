package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.text.ParseException;
import java.util.*;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.constant.PhoneTypeEnum;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.mapper.BaseMapper;
import com.github.phonenumbermanager.mapper.CommunityMapper;
import com.github.phonenumbermanager.service.BaseService;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.poi.excel.cell.CellUtil;

/**
 * 基础业务实现
 *
 * @param <M>
 *            <T> SERVICE泛型
 * @author 廿二月的天
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {
    @Resource
    private CommunityMapper communityMapper;

    @Override
    public List<T> get(T object) {
        return baseMapper.selectByObject(object);
    }

    @Override
    public List<T> get(String nameAddress, Serializable id, Serializable subdistrictId) {
        return baseMapper.selectByNameAndAddress(nameAddress, id, subdistrictId);
    }

    @Override
    public List<T> get(List<PhoneNumber> phoneNumbers, Serializable id, Serializable subdistrictId,
        PhoneNumberSourceTypeEnum sourceType) {
        return baseMapper.selectByPhones(phoneNumbers, id, subdistrictId, sourceType);
    }

    @Override
    public Map<String, Object> get(Serializable companyId, Serializable companyType, Serializable systemCompanyType,
        Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }

    @Override
    public IPage<T> getCorrelation(Integer pageNumber, Integer pageDataSize) {
        return null;
    }

    @Override
    public List<T> getCorrelation() {
        return null;
    }

    @Override
    public boolean save(List<List<Object>> data, Serializable subdistrictId, Map<String, Object> configurationsMap)
        throws ParseException {
        return false;
    }

    @Override
    public IPage<T> getCorrelation(SystemUser systemUser, PhoneNumberSourceTypeEnum phoneNumberSourceTypeEnum,
        Serializable communityCompanyType, Serializable subdistrictCompanyType, Integer pageNumber,
        Integer pageDataSize) {
        List<Serializable> companyIds = getCommunityIds(systemUser, communityCompanyType, subdistrictCompanyType);
        Page<T> page = new Page<>(pageNumber, pageDataSize);
        return baseMapper.selectAndCommunityByCommunityIds(page, companyIds, phoneNumberSourceTypeEnum);
    }

    @Override
    public T getCorrelation(Serializable id) {
        return null;
    }

    @Override
    public Map<String, Object> get(SystemUser systemUser, Serializable companyId, Serializable companyType,
        Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) {
        return null;
    }

    @Override
    public List<LinkedHashMap<String, Object>> getCorrelation(Serializable communityCompanyType,
        Serializable subdistrictCompanyType, List<Map<String, Object>> userData) {
        return null;
    }

    /**
     * 查找社区编号
     *
     * @param systemUser
     *            系统用户对象
     * @param communityCompanyType
     *            社区单位类型编号
     * @param subdistrictCompanyType
     *            街道单位类型编号
     * @return 社区编号数组
     */
    List<Serializable> getCommunityIds(SystemUser systemUser, Serializable communityCompanyType,
        Serializable subdistrictCompanyType) {
        Integer level = systemUser.getLevel().getValue();
        Long companyId = systemUser.getCompanyId();
        List<Serializable> communityIds = new ArrayList<>();
        if (level.equals(communityCompanyType)) {
            communityIds.add(companyId);
        } else if (level.equals(subdistrictCompanyType)) {
            List<Community> communities = communityMapper.selectCorrelationBySubdistrictId(companyId);
            for (Community community : communities) {
                communityIds.add(community.getId());
            }
        } else {
            communityIds = null;
        }
        return communityIds;
    }

    /**
     * 转换单元格字符串
     *
     * @param cell
     *            需要转换的字符串的单元格对象
     * @return 转换成功的字符串
     */
    String convertCellString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return Convert.toDBC(String.valueOf(CellUtil.getCellValue(cell, CellType.STRING, true)));
    }

    /**
     * 转换单元格数字类型
     *
     * @param cell
     *            需要转换的字符串的单元格对象
     * @return 转换成功的字符串
     */
    Integer convertCell(Cell cell) {
        return Integer.parseInt(String.valueOf(CellUtil.getCellValue(cell, CellType.NUMERIC, true)));
    }

    /**
     * 柱状图数据处理
     *
     * @param label
     *            饼图图例
     * @param companyLabel
     *            柱状图横坐标文字
     * @param formatter
     *            格式文字
     * @param object
     *            饼图数据内容
     * @return 处理后的对象
     */
    Map<String, Object> barChartDataHandler(String label, String companyLabel, String formatter,
        LinkedList<Map<String, Object>> object) {
        Map<String, Object> barChartMap = new HashMap<>(3);
        List<String> columns = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> data = new HashMap<>(3);
        List<String> titleLabel = new ArrayList<>();
        for (Map<String, Object> residentCount : object) {
            Map<String, Object> row = new HashMap<>(3);
            row.put(companyLabel, residentCount.get("name"));
            titleLabel.add((String)residentCount.get("name"));
            row.put(label, residentCount.get("value"));
            rows.add(row);
        }
        columns.add(companyLabel);
        columns.add(label);
        barChartMap.put("columns", columns);
        barChartMap.put("rows", rows);
        data.put("data", barChartMap);
        data.put("titleLabel", titleLabel);
        data.put("formatter", formatter);
        return data;
    }

    /**
     * 获取单位类型和单位编号
     *
     * @param systemUser
     *            登录的系统用户对象
     * @param companyId
     *            查找的范围单位的编号
     * @param companyType
     *            查找的范围单位的类别编号
     * @return 单位类型和单位编号集合
     */
    Map<String, Object> getCompany(SystemUser systemUser, Serializable companyId, Serializable companyType) {
        if (companyType == null || companyId == null) {
            companyType = systemUser.getLevel();
            companyId = systemUser.getCompanyId();
        }
        Map<String, Object> company = new HashMap<>(3);
        company.put("companyId", companyId);
        company.put("companyType", companyType);
        return company;
    }

    protected PhoneNumber phoneHandler(String phone) {
        if (PhoneUtil.isPhone(phone)) {
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setPhoneNumber(phone).setSourceType(PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT);
            if (PhoneUtil.isMobile(phone)) {
                phoneNumber.setPhoneType(PhoneTypeEnum.MOBILE);
            } else {
                phoneNumber.setPhoneType(PhoneTypeEnum.LANDLINE);
            }
            return phoneNumber;
        }
        return null;
    }
}
