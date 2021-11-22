package com.github.phonenumbermanager.service.impl;

import org.springframework.stereotype.Service;

import com.github.phonenumbermanager.entity.CommunityResidentPhoneNumber;
import com.github.phonenumbermanager.mapper.CommunityResidentPhoneNumberMapper;
import com.github.phonenumbermanager.service.CommunityResidentPhoneNumberService;

/**
 * 社区居民与联系方式中间业务接口实现
 *
 * @author 廿二月的天
 */
@Service("communityResidentPhoneNumberService")
public class CommunityResidentPhoneNumberServiceImpl
    extends BaseServiceImpl<CommunityResidentPhoneNumberMapper, CommunityResidentPhoneNumber>
    implements CommunityResidentPhoneNumberService {}
