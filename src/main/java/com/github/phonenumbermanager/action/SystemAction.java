package com.github.phonenumbermanager.action;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.validator.ConfigurationInputValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (RequestMethod.POST.toString().equals(request.getMethod()) || RequestMethod.PUT.toString().equals(request.getMethod())) {
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
    @GetMapping({"/configuration", "/configuration/{page}"})
    public String configurationList(Model model, @PathVariable(required = false) Integer page) {
        page = page == null ? 1 : page;
        Page<Configuration> configurationPage = new Page<>(page, 10);
        model.addAttribute("configurations", configurationService.page(configurationPage, null));
        return "configuration/list";
    }

    /**
     * 添加系统配置项
     *
     * @return 视图页面
     */
    @GetMapping("/configuration/create")
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
    @GetMapping("/configuration/edit/{key}")
    public String editConfiguration(Model model, @PathVariable String key) {
        model.addAttribute("configuration", configurationService.getById(key));
        return "configuration/edit";
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
    @RequestMapping(value = "/configuration", method = {RequestMethod.POST, RequestMethod.PUT})
    public String configurationCreateOrEditHandle(HttpServletRequest request, Model model, @Validated Configuration configuration, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            return "configuration/edit";
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            if (!configurationService.save(configuration)) {
                throw new BusinessException("添加系统配置失败！");
            }
        } else {
            if (!configurationService.updateById(configuration)) {
                throw new BusinessException("修改系统配置失败！");
            }
        }
        return "redirect:/system/configuration";
    }

    /**
     * 使用AJAX技术通过系统配置编号删除
     *
     * @param key 对应系统配置项关键字名称
     * @return Ajax信息
     */
    @DeleteMapping("/configuration/{key}")
    @ResponseBody
    public Map<String, Object> deleteConfigurationForAjax(@PathVariable String key) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (configurationService.remove(key)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除系统配置成功！");
            return jsonMap;
        }
        throw new JsonException("删除系统配置失败！");
    }
}
