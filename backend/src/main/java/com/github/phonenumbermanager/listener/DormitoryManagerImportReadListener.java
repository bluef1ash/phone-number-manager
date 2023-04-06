package com.github.phonenumbermanager.listener;

import java.util.List;

import com.github.phonenumbermanager.dto.DormitoryManagerExcelDTO;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.util.RedisUtil;

/**
 * 社区居民楼片长导入监听器
 *
 * @author 廿二月的天
 */
public class DormitoryManagerImportReadListener extends ImportReadListener<DormitoryManagerExcelDTO> {
    private final DormitoryManagerService dormitoryManagerService;

    public DormitoryManagerImportReadListener(RedisUtil redisUtil, Long importId, List<Company> companyAll,
        List<Subcontractor> subcontractorAll, List<PhoneNumber> phoneNumberAll,
        DormitoryManagerService dormitoryManagerService) {
        super(redisUtil, importId, companyAll, subcontractorAll, phoneNumberAll);
        this.dormitoryManagerService = dormitoryManagerService;
    }

    protected void saveData() {
        dormitoryManagerService.save(cachedDataList, companyAll, subcontractorAll, phoneNumberAll);
    }
}
