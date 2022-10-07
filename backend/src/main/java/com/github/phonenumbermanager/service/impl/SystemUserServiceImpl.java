package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.entity.SystemUserCompany;
import com.github.phonenumbermanager.exception.SystemClosedException;
import com.github.phonenumbermanager.mapper.CompanyMapper;
import com.github.phonenumbermanager.mapper.SystemUserCompanyMapper;
import com.github.phonenumbermanager.mapper.SystemUserMapper;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

/**
 * 系统用户业务实现
 *
 * @author 廿二月的天
 */
@AllArgsConstructor
@Component("systemUserService")
@Service
public class SystemUserServiceImpl extends BaseServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisUtil redisUtil;
    private final SystemUserCompanyMapper systemUserCompanyMapper;
    private final CompanyMapper companyMapper;
    private final ConfigurationService configurationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, SystemClosedException {
        SystemUser systemUser = baseMapper.selectAndCompaniesByUsername(username);
        if (systemUser == null) {
            throw new BadCredentialsException("找不到该系统用户！");
        }
        Map<String, Configuration> configurationMap = configurationService.mapAll();
        Long systemAdministratorId = Convert.toLong(configurationMap.get("system_administrator_id").getContent());
        boolean systemIsActive = Convert.toBool(configurationMap.get("system_is_active").getContent());
        if (!systemIsActive && !systemAdministratorId.equals(systemUser.getId())) {
            throw new BadCredentialsException("该系统已经禁止登录，请联系管理员！");
        }
        if (systemAdministratorId.equals(systemUser.getId())) {
            systemUser.setCompanies(null);
        }
        systemUser.setCredentialExpireTime(LocalDateTime.now().plusDays(7));
        return systemUser;
    }

    @Override
    public Authentication authentication(String username, String password, String clientIp) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(token);
        SystemUser systemUser = (SystemUser)authenticate.getPrincipal();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime plusDays = now.plusDays(7);
        systemUser.setPassword(null).setLoginTime(now).setCredentialExpireTime(plusDays).setLoginIp(clientIp);
        baseMapper.update(new SystemUser().setLoginTime(now).setCredentialExpireTime(plusDays).setLoginIp(clientIp),
            new UpdateWrapper<SystemUser>().eq("id", systemUser.getId()));
        redisUtil.setEx(SystemConstant.SYSTEM_USER_ID_KEY + "::" + systemUser.getId(), JSONUtil.toJsonStr(systemUser),
            7, TimeUnit.DAYS);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return authenticate;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(SystemUser systemUser) {
        systemUser.setId(IdWorker.getId()).setCredentialExpireTime(SystemConstant.DATABASE_MIX_DATETIME)
            .setLoginTime(SystemConstant.DATABASE_MIX_DATETIME);
        passwordHandle(systemUser);
        saveOrUpdateHandle(systemUser);
        return baseMapper.insert(systemUser) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(SystemUser systemUser) {
        passwordHandle(systemUser);
        systemUserCompanyMapper.delete(
            new LambdaQueryWrapper<SystemUserCompany>().eq(SystemUserCompany::getSystemUserId, systemUser.getId()));
        saveOrUpdateHandle(systemUser);
        return baseMapper.updateById(systemUser) > 0;
    }

    @SuppressWarnings("all")
    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        if (!SystemConstant.ANONYMOUS_USER.equals(authentication.getPrincipal())) {
            String uri = request.getRequestURI();
            SystemUser systemUser = (SystemUser)authentication.getPrincipal();
            Map<String, Configuration> configurationMap = configurationService.mapAll();
            if (ArrayUtil.contains(SystemConstant.PERMISSION_WHITELIST, uri) || systemUser.getId()
                .equals(Long.valueOf(configurationMap.get("system_administrator_id").getContent()))) {
                return true;
            }
            HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());
            List<Company> companies = (List<Company>)authentication.getAuthorities();
            if (companies != null && !companies.isEmpty()) {
                return companies.stream().flatMap(company -> company.getSystemPermissions().stream())
                    .anyMatch(systemPermission -> systemPermission.getUri().equals(uri)
                        && Arrays.asList(systemPermission.getHttpMethods()).contains(method));
            }
        }
        return false;
    }

    @Override
    public IPage<SystemUser> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize,
        JSONObject search, JSONObject sort) {
        List<Long> companyIds = new ArrayList<>();
        List<Company> companyAll = companyMapper.selectList(null);
        if (companies != null) {
            companyIds = CommonUtil.listRecursionCompanyIds(companies, companyAll, 0L);
        }
        IPage<SystemUser> systemUsers =
            baseMapper.selectCorrelationByCompanyIds(companyIds, new Page<>(pageNumber, pageDataSize), search, sort);
        systemUsers.getRecords().forEach(systemUser -> {
            if (systemUser.getCompanies() == null || systemUser.getCompanies().isEmpty()
                || systemUser.getCompanies().get(0).getId() == null) {
                systemUser.setCompanies(null);
            }
        });
        return systemUsers;
    }

    @Override
    public SystemUser getCorrelation(Long id) {
        return baseMapper.selectAndCompanyById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        return baseMapper.deleteById(id) > 0 && systemUserCompanyMapper
            .delete(new LambdaQueryWrapper<SystemUserCompany>().eq(SystemUserCompany::getSystemUserId, id)) > 0;
    }

    @Override
    public List<SystemUser> listCorrelationByCompanyId(Long companyId) {
        return baseMapper.selectByCompanyId(companyId);
    }

    /**
     * 保存和更新关联操作
     *
     * @param systemUser
     *            操作的对象
     * @return 成功行数
     */
    @SuppressWarnings("all")
    private int saveOrUpdateHandle(SystemUser systemUser) {
        List<SystemUserCompany> systemUserCompanies = systemUser.getCompanies().stream()
            .map(company -> new SystemUserCompany().setCompanyId(company.getId()).setSystemUserId(systemUser.getId()))
            .collect(Collectors.toList());
        return systemUserCompanyMapper.insertBatchSomeColumn(systemUserCompanies);
    }

    /**
     * 系统用户密码处理
     *
     * @param systemUser
     *            系统用户对象
     */
    private void passwordHandle(SystemUser systemUser) {
        if (StrUtil.isNotEmpty(systemUser.getPassword())) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()));
        }
    }
}
