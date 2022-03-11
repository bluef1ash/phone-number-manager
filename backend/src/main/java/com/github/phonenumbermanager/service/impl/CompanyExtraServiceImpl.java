package com.github.phonenumbermanager.service.impl;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.github.phonenumbermanager.entity.CompanyExtra;
import com.github.phonenumbermanager.mapper.CompanyExtraMapper;
import com.github.phonenumbermanager.service.CompanyExtraService;

/**
 * 单位额外属性接口实现
 *
 * @author 廿二月的天
 */
@Service
public class CompanyExtraServiceImpl extends BaseServiceImpl<CompanyExtraMapper, CompanyExtra>
    implements CompanyExtraService {

    @Override
    public boolean saveBatch(Collection<CompanyExtra> entityList) {
        if (entityList != null && !entityList.isEmpty()) {
            if (entityList.size() == 1) {
                return super.saveBatch(entityList);
            }
            entityList.forEach(companyExtra -> {
                if (companyExtra.getDescription() == null) {
                    companyExtra.setDescription("");
                }
            });
            return baseMapper.insertBatchSomeColumn(entityList) > 0;
        }
        return false;
    }
}
