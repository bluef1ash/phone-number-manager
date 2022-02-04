package com.github.phonenumbermanager.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
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
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.SystemClosedException;
import com.github.phonenumbermanager.mapper.SystemUserMapper;
import com.github.phonenumbermanager.service.*;
import com.github.phonenumbermanager.util.RedisUtil;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

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
    @Resource
    private SystemPermissionService systemPermissionService;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException, SystemClosedException {
        SystemUser systemUser = baseMapper.selectAndCompaniesByPhoneNumber(phoneNumber);
        if (systemUser == null) {
            throw new UsernameNotFoundException("找不到该手机号！");
        }
        @SuppressWarnings("all")
        Map<String, JSONObject> configurationMap =
            JSONUtil.parseObj(redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY)).toBean(Map.class);
        if (configurationMap == null || configurationMap.isEmpty()) {
            configurationMap = configurationCacheHandler();
        }
        Long systemAdministratorId = Convert.toLong(configurationMap.get("system_administrator_id").get("content"));
        boolean systemIsActive = Convert.toBool(configurationMap.get("system_is_active").get("content"));
        if (!systemIsActive && !systemAdministratorId.equals(systemUser.getId())) {
            throw new SystemClosedException("该系统已经禁止登录，请联系管理员！");
        }
        if (!systemUser.getCompanies().isEmpty() && systemUser.getCompanies().get(0).getId() == null) {
            systemUser.setCompanies(null);
        }
        systemUser.setCredentialExpireTime(LocalDateTime.now().plusDays(7));
        return systemUser;
    }

    @Override
    public Authentication authentication(String phoneNumber, String password, String clientIp) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(phoneNumber, password);
        Authentication authenticate =
            authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);
        SystemUser systemUser = (SystemUser)authenticate.getPrincipal();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime plusDays = now.plusDays(7);
        systemUser.setLoginTime(now).setCredentialExpireTime(plusDays).setLoginIp(clientIp);
        baseMapper.update(new SystemUser().setLoginTime(now).setCredentialExpireTime(plusDays).setLoginIp(clientIp),
            new UpdateWrapper<SystemUser>().eq("id", systemUser.getId()));
        redisUtil.setEx(SystemConstant.SYSTEM_USER_ID_KEY + systemUser.getId(), JSONUtil.toJsonStr(systemUser),
            plusDays.toEpochSecond(ZoneOffset.of("+8")), TimeUnit.SECONDS);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return authenticate;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(SystemUser systemUser) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()))
            .setCredentialExpireTime(SystemConstant.DATABASE_MIX_DATETIME)
            .setLoginTime(SystemConstant.DATABASE_MIX_DATETIME);
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
    public IPage<SystemUser> pageCorrelation(List<Company> companies, Integer pageNumber, Integer pageDataSize,
        JSONObject search, JSONObject sort) {
        List<Long> companyIds = new ArrayList<>();
        List<Company> companyAll = companyService.list();
        companyService.listRecursionCompanyIds(companyIds, companies, companyAll, 0L);
        IPage<SystemUser> systemUsers =
            baseMapper.selectCorrelationByCompanyIds(companyIds, new Page<>(pageNumber, pageDataSize), search, sort);
        systemUsers.getRecords().forEach(systemUser -> {
            if (systemUser.getCompanies() != null && !systemUser.getCompanies().isEmpty()
                && systemUser.getCompanies().get(0).getId() != null) {
                systemUser.getCompanies().forEach(company -> systemUser.getCompanyNames().add(company.getName()));
            } else {
                systemUser.setCompanies(null);
            }
        });
        return systemUsers;
    }

    @Override
    public SystemUser getCorrelation(Long id) {
        return baseMapper.selectAndCompanyById(id);
    }

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        if (SystemConstant.ANONYMOUS_USER.equals(authentication.getPrincipal())) {
            return false;
        }
        String uri = request.getRequestURI();
        SystemUser systemUser = (SystemUser)authentication.getPrincipal();
        @SuppressWarnings("all")
        Map<String, JSONObject> config =
            JSONUtil.parseObj(redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY)).toBean(Map.class);
        if (config == null || config.isEmpty()) {
            config = configurationCacheHandler();
        }
        JSONObject id = config.get("system_administrator_id");
        if (ArrayUtils.contains(SystemConstant.PERMISSION_PERMITS, uri)
            || systemUser.getId().equals(Long.valueOf((String)id.get("content")))) {
            return true;
        }
        HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities != null && authorities.size() > 0) {
            for (GrantedAuthority authority : authorities) {
                // TODO: 2021/11/26 0026 缓存
                List<SystemPermission> systemPermissions =
                    systemPermissionService.listByCompanyId(Long.valueOf(authority.getAuthority()));
                if (systemPermissions.stream()
                    .anyMatch(systemPermission -> uri.contains(systemPermission.getUri().replaceAll("//\\{.*}/", ""))
                        && Arrays.asList(systemPermission.getHttpMethods()).contains(method))) {
                    return true;
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

    /**
     * 配置项缓存处理
     *
     * @return 系统配置
     */
    @SuppressWarnings("all")
    private Map<String, JSONObject> configurationCacheHandler() {
        List<Configuration> configurationList = configurationService.list(null);
        Map<String, Configuration> configurationM =
            configurationList.stream().collect(Collectors.toMap(Configuration::getName, Function.identity()));
        String jsonStr = JSONUtil.toJsonStr(configurationM);
        redisUtil.set(SystemConstant.CONFIGURATIONS_MAP_KEY, jsonStr);
        return JSONUtil.toBean(jsonStr, Map.class);
    }
}
