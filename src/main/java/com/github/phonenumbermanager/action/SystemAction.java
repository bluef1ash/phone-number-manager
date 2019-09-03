package com.github.phonenumbermanager.action;

import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.validator.ConfigurationInputValidator;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
public class SystemAction extends BaseAction {
    @Resource
    private ConfigurationService configurationService;
    private final HttpServletRequest request;

    @Autowired
    public SystemAction(HttpServletRequest request) {
        this.request = request;
    }

    @InitBinder
    public void initBinder(DataBinder binder) {
        StringBuffer requestUrl = request.getRequestURL();
        String url = requestUrl.substring(requestUrl.lastIndexOf("/"));
        if (VALID_URL.equals(url)) {
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
    public String configurationList(Model model, Integer page) {
        try {
            Map<String, Object> configurationsMap = configurationService.find(page, null);
            model.addAttribute("configurations", configurationsMap.get("data"));
            model.addAttribute("pageInfo", configurationsMap.get("pageInfo"));
            return "configuration/list";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加系统配置项
     *
     * @return 视图页面
     */
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
    @RequestMapping(value = "/configuration/edit", method = RequestMethod.GET)
    public String editConfiguration(Model model, String key) {
        try {
            Configuration configuration = configurationService.find(key);
            model.addAttribute("configuration", configuration);
            return "configuration/edit";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加、修改系统配置处理
     *
     * @param request       HTTP请求对象
     * @param model         前台模型
     * @param configuration 系统配置对象
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
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
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            try {
                configurationService.create(configuration);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("添加系统配置失败！", e);
            }
        } else {
            try {
                configurationService.update(configuration);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("修改系统配置失败！", e);
            }
        }
        return "redirect:/system/configuration/list";
    }

    /**
     * 使用AJAX技术通过系统配置编号删除
     *
     * @param key 对应系统配置项关键字名称
     * @return Ajax信息
     */
    @RequestMapping(value = "/configuration/ajax_delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, Object> deleteConfigurationForAjax(String key) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            if (configurationService.delete(key) > 0) {
                jsonMap.put("state", 1);
                jsonMap.put("message", "删除系统配置成功！");
            } else {
                jsonMap.put("state", 0);
                jsonMap.put("message", "不允许删除内置系统配置");
            }
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("删除系统配置失败！", e);
        }
    }
}
