package www.validator;

import constant.SystemConstant;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import utils.CommonUtil;
import www.entity.SystemUser;
import www.service.SystemUserService;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

/**
 * 登录表单验证
 *
 * @author 廿二月的天
 */
public class LoginInputValidator extends BaseInputValidator implements Validator {
    private SystemUserService systemUserService;
    private String captcha;

    public LoginInputValidator(SystemUserService systemUserService, HttpServletRequest request, String captcha) {
        this.request = request;
        this.systemUserService = systemUserService;
        this.captcha = captcha;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SystemUser.class);
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "username", "systemUser.username.required", "系统用户名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "password", "systemUser.password.required", "系统用户密码不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "captcha", "systemUser.captcha.required", "登录验证码不能为空！");
            SystemUser systemUser = (SystemUser) target;
            if (!captcha.equalsIgnoreCase(systemUser.getCaptcha())) {
                // 得到用户输入的验证码匹配失败
                field = "captcha";
                errorCode = "systemUser.captcha.error";
                message = "您输入的验证码错误！";
                return false;
            }
            List<SystemUser> systemUsers = systemUserService.findSystemUserByUserName(systemUser.getUsername());
            if (systemUsers.size() == 0) {
                field = "username";
                errorCode = "systemUser.username.error";
                message = "您输入的系统用户名称错误，无“" + systemUser.getUsername() + "”系统用户！";
                return false;
            }
            SystemUser user = systemUsers.get(0);
            if (!validPassword(systemUser.getPassword(), user.getPassword())) {
                field = "password";
                errorCode = "systemUser.password.error";
                message = "您输入的系统用户密码错误，请重新输入！";
                return false;
            }
            if (user.getIsLocked() == 1) {
                field = "password";
                errorCode = "systemUser.password.error";
                message = "登录失败！系统用户“" + systemUser.getUsername() + "”已被锁定，请联系管理员！";
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            field = "username";
            errorCode = "system.error";
            message = "系统错误！";
            return false;
        }
        return true;
    }

    /**
     * 验证口令是否合法
     *
     * @param password 前台表单传递已经加密的密码字符串
     * @param passwordInDb 数据库中的密码字符串
     * @return 验证是否成功
     * @throws Exception 验证异常
     */
    private boolean validPassword(String password, String passwordInDb) throws Exception {
        //将16进制字符串格式口令转换成字节数组
        byte[] pwdInDb = CommonUtil.hexString2Byte(passwordInDb);
        //声明盐变量
        byte[] salt = new byte[SystemConstant.SALT_LENGTH];
        //将盐从数据库中保存的口令字节数组中提取出来
        System.arraycopy(pwdInDb, 0, salt, 0, SystemConstant.SALT_LENGTH);
        //创建消息摘要对象
        MessageDigest md = MessageDigest.getInstance(SystemConstant.PASSWORD_MODE);
        //将盐数据传入消息摘要对象
        md.update(salt);
        //将口令的数据传给消息摘要对象
        md.update(password.getBytes("UTF-8"));
        //生成输入口令的消息摘要
        byte[] digest = md.digest();
        //声明一个保存数据库中口令消息摘要的变量
        byte[] digestInDb = new byte[pwdInDb.length - SystemConstant.SALT_LENGTH];
        //取得数据库中口令的消息摘要
        System.arraycopy(pwdInDb, SystemConstant.SALT_LENGTH, digestInDb, 0, digestInDb.length);
        //比较根据输入口令生成的消息摘要和数据库中消息摘要是否相同，口令正确返回口令匹配消息，口令不正确返回口令不匹配消息
        return Arrays.equals(digest, digestInDb);
    }
}
