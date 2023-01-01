package com.github.phonenumbermanager.listener;

import java.util.List;

import com.github.phonenumbermanager.dto.CommunityResidentExcelDTO;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.util.RedisUtil;

/**
 * 社区居民导入监听器
 *
 * @author 廿二月的天
 */
public class CommunityResidentImportReadListener extends ImportReadListener<CommunityResidentExcelDTO> {
    private final CommunityResidentService communityResidentService;

    public CommunityResidentImportReadListener(RedisUtil redisUtil, Long importId,
        CommunityResidentService communityResidentService, List<Company> companyAll,
        List<Subcontractor> subcontractorAll, List<PhoneNumber> phoneNumberAll) {
        super(redisUtil, importId, companyAll, subcontractorAll, phoneNumberAll);
        this.communityResidentService = communityResidentService;
    }

    protected void saveData() {
        communityResidentService.save(cachedDataList, companyAll, subcontractorAll, phoneNumberAll);
    }
}
