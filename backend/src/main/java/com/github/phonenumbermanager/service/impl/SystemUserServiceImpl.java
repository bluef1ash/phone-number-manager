package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.exception.SystemClosedException;
import com.github.phonenumbermanager.mapper.CompanyMapper;
import com.github.phonenumbermanager.mapper.PhoneNumberMapper;
import com.github.phonenumbermanager.mapper.SystemUserCompanyMapper;
import com.github.phonenumbermanager.mapper.SystemUserMapper;
import com.github.phonenumbermanager.service.SystemUserService;
import com.github.phonenumbermanager.util.CommonUtil;
import com.github.phonenumbermanager.util.RedisUtil;
import com.github.phonenumbermanager.vo.SelectListVo;

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
    private final PhoneNumberMapper phoneNumberMapper;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException, SystemClosedException {
        SystemUser systemUser = baseMapper.selectAndCompaniesByPhoneNumber(phoneNumber);
        if (systemUser == null) {
            throw new UsernameNotFoundException("找不到该手机号！");
        }
        @SuppressWarnings("all")
        Map<String, JSONObject> configurationMap =
            JSONUtil.parseObj(redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY)).toBean(Map.class);
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
        baseMapper.insert(systemUser);
        return saveOrUpdateHandle(systemUser) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(SystemUser systemUser) {
        if (StrUtil.isNotEmpty(systemUser.getPassword())) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()));
        }
        systemUserCompanyMapper.delete(
            new LambdaQueryWrapper<SystemUserCompany>().eq(SystemUserCompany::getSystemUserId, systemUser.getId()));
        baseMapper.updateById(systemUser);
        return saveOrUpdateHandle(systemUser) > 0;
    }

    @SuppressWarnings("all")
    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        if (!SystemConstant.ANONYMOUS_USER.equals(authentication.getPrincipal())) {
            String uri = request.getRequestURI();
            SystemUser systemUser = (SystemUser)authentication.getPrincipal();
            Map<String, JSONObject> config =
                JSONUtil.parseObj(redisUtil.get(SystemConstant.CONFIGURATIONS_MAP_KEY)).toBean(Map.class);
            JSONObject id = config.get("system_administrator_id");
            if (ArrayUtil.contains(SystemConstant.PERMISSION_PERMITS, uri)
                || systemUser.getId().equals(Long.valueOf((String)id.get("content")))) {
                return true;
            }
            HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());
            List<Company> companies = (List<Company>)authentication.getAuthorities();
            if (companies != null && companies.size() > 0) {
                List<SystemPermission> systemPermissions =
                    (List<SystemPermission>)redisUtil.get(SystemConstant.SYSTEM_PERMISSIONS_KEY);
                Optional<List<Company>> optionalCompanies = systemPermissions.stream()
                    .filter(systemPermission -> uri.contains(systemPermission.getUri().replaceAll("//\\{.*}/", ""))
                        && Arrays.asList(systemPermission.getHttpMethods()).contains(method))
                    .map(SystemPermission::getCompanies).findFirst();
                if (optionalCompanies.isPresent()) {
                    return companies.stream().anyMatch(company -> optionalCompanies.get().stream()
                        .anyMatch(company1 -> company.getId().equals(company1.getId())));
                }
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
            CommonUtil.listRecursionCompanyIds(companyIds, companies, companyAll, 0L);
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

    @Override
    public List<SelectListVo> treeSelectList(Long parentId) {
        SystemUser systemUser = (SystemUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<SelectListVo> selectListVos = new ArrayList<>();
        Set<Long> parentIds = companyMapper.selectList(new LambdaQueryWrapper<Company>().select(Company::getParentId))
            .stream().map(Company::getParentId).collect(Collectors.toSet());
        if (parentId == null) {
            if (systemUser.getCompanies() == null) {
                selectListVos = companyMapper.selectList(new LambdaQueryWrapper<Company>().eq(Company::getParentId, 0L))
                    .stream().map(company -> new SelectListVo().setValue(company.getId()).setLabel(company.getName())
                        .setIsLeaf(false))
                    .collect(Collectors.toList());
            } else {
                List<Long> ids = systemUser.getCompanies().stream().map(Company::getId).collect(Collectors.toList());
                selectListVos.addAll(getSelectVos(ids, parentIds));
            }
        } else {
            List<Long> ids = new ArrayList<>();
            ids.add(parentId);
            selectListVos.addAll(getSelectVos(ids, parentIds));
        }
        return selectListVos;
    }

    /**
     * 保存和更新关联操作
     *
     * @param systemUser
     *            操作的对象
     * @return 成功行数
     */
    private int saveOrUpdateHandle(SystemUser systemUser) {
        PhoneNumber phoneNumber = phoneNumberMapper.selectOne(new LambdaQueryWrapper<PhoneNumber>()
            .eq(PhoneNumber::getPhoneNumber, systemUser.getPhoneNumber().getPhoneNumber()));
        if (phoneNumber == null) {
            phoneNumberMapper.insert(systemUser.getPhoneNumber());
            systemUser.setPhoneNumberId(systemUser.getPhoneNumber().getId());
        } else {
            systemUser.setPhoneNumberId(phoneNumber.getId());
        }
        List<SystemUserCompany> systemUserCompanies = systemUser.getCompanies().stream()
            .map(company -> new SystemUserCompany().setCompanyId(company.getId()).setSystemUserId(systemUser.getId()))
            .collect(Collectors.toList());
        return systemUserCompanyMapper.insertBatchSomeColumn(systemUserCompanies);
    }

    /**
     * 获取表单对象集合
     *
     * @param ids
     *            查询的编号集合
     * @param parentIds
     *            所有父级编号
     * @return 表单对象集合
     */
    private List<SelectListVo> getSelectVos(List<Long> ids, Set<Long> parentIds) {
        List<SelectListVo> selectListVos = new ArrayList<>();
        LambdaQueryWrapper<Company> companyWrapper = new LambdaQueryWrapper<>();
        ids.stream().filter(parentIds::contains).forEach(id -> companyWrapper.eq(Company::getParentId, id));
        List<Long> subIds = ids.stream().filter(id -> !parentIds.contains(id)).collect(Collectors.toList());
        if (companyWrapper.nonEmptyOfWhere()) {
            selectListVos.addAll(companyMapper.selectList(companyWrapper).stream().map(
                company -> new SelectListVo().setLabel(company.getName()).setValue(company.getId()).setIsLeaf(false))
                .collect(Collectors.toList()));
        }
        if (!subIds.isEmpty()) {
            selectListVos.addAll(baseMapper.selectListByCompanyIds(subIds).stream()
                .map(user -> new SelectListVo().setLabel(user.getUsername()).setValue(user.getId()).setIsLeaf(true))
                .collect(Collectors.toList()));
        }
        return selectListVos;
    }
}
