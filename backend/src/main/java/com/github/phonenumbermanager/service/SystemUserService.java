package com.github.phonenumbermanager.service;

import java.util.List;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.github.phonenumbermanager.entity.SystemUser;

/**
 * 系统用户业务接口
 *
 * @author 廿二月的天
 */
public interface SystemUserService extends BaseService<SystemUser>, UserDetailsService {
    /**
     * 认证密码
     *
     * @param phoneNumber
     *            联系方式
     * @param password
     *            密码
     * @param clientIp
     *            客户端IP地址
     * @throws LoginException
     *             登录异常
     * @return 认证对象
     */
    Authentication authentication(String phoneNumber, String password, String clientIp) throws LoginException;

    /**
     * 判断权限
     *
     * @param request
     *            HTTP请求
     * @param authentication
     *            认证对象
     * @return 是否享受
     */
    boolean hasPermission(HttpServletRequest request, Authentication authentication);

    /**
     * 通过单位编号查找
     *
     * @param companyId
     *            单位编号
     * @return 系统用户集合
     */
    List<SystemUser> listCorrelationByCompanyId(Long companyId);
}
