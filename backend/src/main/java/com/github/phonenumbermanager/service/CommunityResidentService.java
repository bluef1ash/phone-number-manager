package com.github.phonenumbermanager.service;

import java.util.List;

import com.github.phonenumbermanager.dto.CommunityResidentExcelDTO;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.Subcontractor;

/**
 * 社区居民信息业务接口
 *
 * @author 廿二月的天
 */
public interface CommunityResidentService extends BaseService<CommunityResident> {
    /**
     * 通过联系方式集合关联查找
     *
     * @param id
     *            社区居民编号
     * @param phoneNumbers
     *            联系方式集合
     * @return 查找到的数据
     */
    List<CommunityResident> listByPhoneNumbers(Long id, List<PhoneNumber> phoneNumbers);

    /**
     * 导入数据
     *
     * @param communityResidentExcelDTOs
     *            需要导入的数据
     * @param companyAll
     *            所有单位集合
     * @param subcontractorAll
     *            所有社区分包人集合
     * @param phoneNumberAll
     *            所有联系方式集合
     * @return 导入成功 / 失败
     */
    boolean save(List<CommunityResidentExcelDTO> communityResidentExcelDTOs, List<Company> companyAll,
        List<Subcontractor> subcontractorAll, List<PhoneNumber> phoneNumberAll);
}
