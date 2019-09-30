package com.github.phonenumbermanager.action;

import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.SubdistrictService;
import com.github.phonenumbermanager.validator.SubdistrictInputValidator;
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
 * 街道控制器
 *
 * @author 廿二月的天
 */
@Controller
@RequestMapping("/subdistrict")
public class SubdistrictAction extends BaseAction {
    @Resource
    private SubdistrictService subdistrictService;
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        StringBuffer requestUrl = request.getRequestURL();
        String url = requestUrl.substring(requestUrl.lastIndexOf("/"));
        if (VALID_URL.equals(url)) {
            binder.replaceValidators(new SubdistrictInputValidator(subdistrictService, request));
        }
    }

    /**
     * 街道列表
     *
     * @param model 前台模型
     * @param page  分页对象
     * @return 视图页面
     */
    @GetMapping("/list")
    public String subdistrictList(Model model, Integer page) {
        try {
            Map<String, Object> subdistricts = subdistrictService.find(page, null);
            model.addAttribute("subdistricts", subdistricts.get("data"));
            model.addAttribute("pageInfo", subdistricts.get("pageInfo"));
            return "subdistrict/list";
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加街道
     *
     * @return 视图页面
     */
    @GetMapping("/create")
    public String createSubdistrict() {
        return "subdistrict/edit";
    }

    /**
     * 编辑街道
     *
     * @param model 前台模型
     * @param id    编辑的对应编号
     * @return 视图页面
     */
    @GetMapping("/edit")
    public String editSubdistrict(Model model, @RequestParam Long id) {
        try {
            Subdistrict subdistrict = subdistrictService.find(id);
            model.addAttribute("subdistrict", subdistrict);
            return "subdistrict/edit";
        } catch (Exception e) {
            throw new BusinessException("系统异常！找不到数据，请稍后再试！", e);
        }
    }

    /**
     * 添加、修改街道处理
     *
     * @param request       HTTP请求对象
     * @param model         前台模型
     * @param subdistrict   街道对象
     * @param bindingResult 错误信息对象
     * @return 视图页面
     */
    @RequestMapping(value = "/handle", method = {RequestMethod.POST, RequestMethod.PUT})
    public String subdistrictCreateOrEditHandle(HttpServletRequest request, Model model, @Validated Subdistrict subdistrict, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            return "subdistrict/edit";
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            try {
                subdistrictService.create(subdistrict);
            } catch (Exception e) {
                throw new BusinessException("添加街道失败！", e);
            }
        } else {
            try {
                subdistrictService.update(subdistrict);
            } catch (Exception e) {
                throw new BusinessException("修改街道失败！", e);
            }
        }
        return "redirect:/subdistrict/list";
    }

    /**
     * 使用AJAX技术通过街道编号删除
     *
     * @param id 对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/ajax_delete")
    @ResponseBody
    public Map<String, Object> deleteSubdistrictForAjax(@RequestParam Long id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            subdistrictService.delete(id);
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除街道成功！");
            return jsonMap;
        } catch (BusinessException be) {
            be.printStackTrace();
            throw new JsonException(be);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("删除街道失败！", e);
        }
    }

    /**
     * 通过Ajax技术获取街道信息
     *
     * @return 街道信息
     */
    @PostMapping("/ajax_load")
    @ResponseBody
    public Map<String, Object> getSubdistrictForAjax() {
        Map<String, Object> jsonMap = new HashMap<>(3);
        try {
            jsonMap.put("state", 1);
            jsonMap.put("subdistricts", subdistrictService.find());
            return jsonMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonException("获取街道失败！", e);
        }
    }
}
