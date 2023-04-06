package com.github.phonenumbermanager.service;

import java.util.List;

import com.github.phonenumbermanager.dto.DormitoryManagerExcelDTO;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.Subcontractor;

/**
 * 社区楼长业务接口
 *
 * @author 廿二月的天
 */
public interface DormitoryManagerService extends BaseService<DormitoryManager> {

    /**
     * 导入数据
     *
     * @param dormitoryManagerExcelDTOs
     *            需要导入的数据
     * @param companyAll
     *            所有单位集合
     * @param subcontractorAll
     *            所有社区分包人集合
     * @param phoneNumberAll
     *            所有联系方式集合
     * @return 导入是否成功
     */
    boolean save(List<DormitoryManagerExcelDTO> dormitoryManagerExcelDTOs, List<Company> companyAll,
        List<Subcontractor> subcontractorAll, List<PhoneNumber> phoneNumberAll);
}
