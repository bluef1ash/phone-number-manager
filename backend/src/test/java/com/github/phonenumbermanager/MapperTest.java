package com.github.phonenumbermanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.phonenumbermanager.constant.enums.PhoneTypeEnum;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.PhoneNumberService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper 测试类
 *
 * @author 廿二月的天
 */
@SpringBootTest
@Slf4j
@AllArgsConstructor
public class MapperTest {
    private final ConfigurationService configurationService;
    private final PhoneNumberService phoneNumberService;

    @Test
    public void testVersion() {
        Configuration configuration =
            configurationService.getOne(new LambdaQueryWrapper<Configuration>().eq(Configuration::getId, 1));
        configuration.setOrderBy(null).setVersion(0).setCreateTime(null).setUpdateTime(null);
        configuration.setDescription("test version");
        configurationService.updateById(configuration);
    }

    @Test
    public void testIgnore() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPhoneNumber("13012345678").setPhoneType(PhoneTypeEnum.MOBILE);
        phoneNumberService.saveIgnore(phoneNumber);
        log.debug(phoneNumber.toString());
    }
}
