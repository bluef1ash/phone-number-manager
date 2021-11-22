package com.github.phonenumbermanager.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.SystemUserCompany;
import com.github.phonenumbermanager.exception.SystemClosedException;
import com.github.phonenumbermanager.mapper.SystemUserMapper;
import com.github.phonenumbermanager.service.CompanyService;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.SystemUserCompanyService;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.core.convert.Convert;

/**
 * 系统用户业务实现
 *
 * @author 廿二月的天
 */
@Service("systemUserService")
public class SystemUserServiceImpl extends BaseServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    @Resource
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private SystemUserCompanyService systemUserCompanyService;
    @Resource
    private ConfigurationService configurationService;
    @Resource
    private CompanyService companyService;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException, SystemClosedException {
        SystemUser systemUser = baseMapper.selectAndCompaniesByPhoneNumber(phoneNumber);
        @SuppressWarnings("all")
        Map<String, Configuration> configurationsMap =
            (Map<String, Configuration>)redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY);
        if (configurationsMap == null) {
            List<Configuration> configurations = configurationService.list(null);
            configurationsMap =
                configurations.stream().collect(Collectors.toMap(Configuration::getName, Function.identity()));
            redisUtil.set(SystemConstant.CONFIGURATIONS_MAP_KEY, configurationsMap);
        }
        Long systemAdministratorId = Convert.toLong(configurationsMap.get("system_administrator_id").getContent());
        boolean systemIsActive = Convert.toBool(configurationsMap.get("system_is_active").getContent());
        if (!systemIsActive && !systemAdministratorId.equals(systemUser.getId())) {
            throw new SystemClosedException("该系统已经禁止登录，请联系管理员！");
        }
        return systemUser;
    }

    @Override
    public Authentication authentication(String phoneNumber, String password, String clientIp) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(phoneNumber, password);
        Authentication authenticate =
            authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);
        SystemUser systemUser = (SystemUser)authenticate.getPrincipal();
        baseMapper
            .update(
                new SystemUser().setLoginTime(LocalDateTime.now())
                    .setCredentialExpireTime(LocalDateTime.now().plusDays(7)).setLoginIp(clientIp),
                new UpdateWrapper<SystemUser>().eq("username", systemUser.getId()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return authenticate;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(SystemUser systemUser) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()));
        systemUser.setLoginTime(LocalDateTime.MIN);
        return baseMapper.insert(systemUser) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(SystemUser systemUser) {
        if (StringUtils.isNotEmpty(systemUser.getPassword())) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()));
        }
        return baseMapper.updateById(systemUser) > 0;
    }

    @Override
    public List<SystemUser> listCorrelationPhoneNumber() {
        return baseMapper.selectAndPhoneNumber();
    }

    @Override
    public IPage<SystemUser> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize) {
        List<Long> companyIds = new ArrayList<>();
        List<Company> companyAll = companyService.list();
        companyService.listRecursionCompanyIds(companyIds, companies, companyAll, null);
        return baseMapper.selectCorrelationByCompanyIds(companyIds, new Page<>(pageNumber, pageDataSize));
    }

    @Override
    public SystemUser getCorrelation(Long id) {
        return baseMapper.selectAndCompanyById(id);
    }

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if (ArrayUtils.contains(SystemConstant.PRIVILEGE_PERMITS, uri)) {
            return true;
        }
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null && authorities.size() > 0) {
                for (GrantedAuthority authority : authorities) {
                    String authorityUri = authority.getAuthority();
                    String authorityMethod = null;
                    int indexOf = authorityUri.indexOf("|");
                    if (indexOf > 0) {
                        authorityUri = authorityUri.substring(0, indexOf - 1);
                        authorityMethod = authorityUri.substring(indexOf);
                    }
                    if (authorityUri.equals(uri)) {
                        if (authorityMethod != null && authorityMethod.indexOf(",") > 0) {
                            return ArrayUtils.contains(authorityMethod.split(","), method);
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCorrelationById(Long id) {
        return baseMapper.deleteById(id) > 0
            && systemUserCompanyService.remove(new QueryWrapper<SystemUserCompany>().eq("user_id", id));
    }

    @Override
    public List<SystemUser> listCorrelationByCompanyId(Long companyId) {
        return baseMapper.selectByCompanyId(companyId);
    }
}
