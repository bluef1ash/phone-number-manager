package www.action;

import annotation.RefreshCsrfToken;
import annotation.VerifyCSRFToken;
import exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.CsrfTokenUtil;
import www.entity.Configuration;
import www.service.ConfigurationService;
import www.validator.ConfigurationInputValidator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统管理控制器
 *
 * @author 廿二月的天
 */
@Controller
@RequestMapping("/system")
public class SystemAction {
    @Resource
    private ConfigurationService configurationService;
    private final HttpServletRequest request;

    @Autowired
    public SystemAction(HttpServletRequest request) {
        this.request = request;
    }

    @InitBinder
    public void initBinder(DataBinder binder) {
        String validFunction = (String) request.getSession().getAttribute("validFunction");
        if ("configurationCreateOrEditHandle".equals(validFunction)) {
            binder.replaceValidators(new ConfigurationInputValidator(configurationService, request));
        }
    }

    /**
     * 系统配置列表
     *
     * @param model 前台模型
     * @param page  分页页码
     * @return 视图页面
     */
    @RequestMapping(value = "/configuration/list", method = RequestMethod.GET)
    @RefreshCsrfToken
    public String configurationList(Model model, Integer page) {
        try {
            Map<String, Object> configurationsMap = configurationService.findObjects(page, null);
            model.addAttribute("configurations", configurationsMap.get("data"));
            model.addAttribute("pageInfo", configurationsMap.get("pageInfo"));
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
        return "configuration/list";
    }

    /**
     * 添加系统配置项
     *
     * @return 视图页面
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/configuration/create", method = RequestMethod.GET)
    public String createConfiguration() {
        return "configuration/edit";
    }

    /**
     * 编辑系统配置项
     *
     * @param model 前台模型
     * @param key   编辑的对应系统配置项关键字名称
     * @return 视图页面
     */
    @RefreshCsrfToken
    @RequestMapping(value = "/configuration/edit", method = RequestMethod.GET)
    public String editConfiguration(Model model, String key) {
        try {
            Configuration configuration = configurationService.findConfigurationByKey(key);
            model.addAttribute("configuration", configuration);
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
        return "configuration/edit";
    }

    /**
     * 添加、修改系统配置处理
     *
     * @param request       HTTP响应对象
     * @param model         前台模型
     * @param configuration 系统配置对象
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @VerifyCSRFToken
    @RequestMapping(value = "/configuration/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    public String configurationCreateOrEditHandle(HttpServletRequest request, Model model, @Validated Configuration configuration, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            try {
                // 输出错误信息
                List<ObjectError> allErrors = bindingResult.getAllErrors();
                model.addAttribute("messageErrors", allErrors);
                return "configuration/edit";
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("展示系统配置失败！", e);
            }
        }
        if ("POST".equals(request.getMethod())) {
            try {
                configurationService.createObject(configuration);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("添加系统配置失败！", e);
            }
        } else {
            try {
                configurationService.updateObject(configuration);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("修改系统配置失败！", e);
            }
        }
        return "redirect:/system/configuration/list.action";
    }

    /**
     * 使用AJAX技术通过系统配置编号删除
     *
     * @param session session对象
     * @param id 对应系统配置项关键字名称
     * @return Ajax信息
     */
    @RequestMapping(value = "/configuration/ajax_delete", method = RequestMethod.DELETE)
    @VerifyCSRFToken
    public @ResponseBody
    Map<String, Object> deleteConfigurationForAjax(HttpSession session, String id) {
        Map<String, Object> map = new HashMap<>(4);
        try {
            if (configurationService.deleteConfigurationByKey(id)) {
                map.put("state", 1);
                map.put("message", "删除系统配置成功！");
            } else {
                map.put("state", 0);
                map.put("message", "不允许删除内置系统配置");
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", 0);
            map.put("message", "删除系统配置失败！");
        }
        map.put("_token", CsrfTokenUtil.getTokenForSession(session, null));
        return map;
    }
}
