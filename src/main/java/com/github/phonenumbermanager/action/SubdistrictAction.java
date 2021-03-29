package com.github.phonenumbermanager.action;

import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.exception.JsonException;
import com.github.phonenumbermanager.service.PhoneNumberService;
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
    private PhoneNumberService phoneNumberService;
    @Resource
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (RequestMethod.POST.toString().equals(request.getMethod()) || RequestMethod.PUT.toString().equals(request.getMethod())) {
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
    @GetMapping({"", "/{page}"})
    public String subdistrictList(Model model, @PathVariable(required = false) Integer page) {
        model.addAttribute("subdistricts", subdistrictService.getCorrelation(page, null));
        return "subdistrict/list";
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
    @GetMapping("/edit/{id}")
    public String editSubdistrict(Model model, @PathVariable Long id) {
        Subdistrict subdistrict = subdistrictService.getCorrelation(id);
        model.addAttribute("subdistrict", subdistrict);
        return "subdistrict/edit";
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
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public String subdistrictCreateOrEditHandle(HttpServletRequest request, Model model, @Validated Subdistrict subdistrict, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            model.addAttribute("messageErrors", allErrors);
            return "subdistrict/edit";
        }
        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            if (subdistrictService.save(subdistrict)) {
                setPhoneNumbers(subdistrict.getPhoneNumbers(), PhoneNumberSourceTypeEnum.SUBDISTRICT, String.valueOf(subdistrict.getId()));
                phoneNumberService.saveBatch(subdistrict.getPhoneNumbers());
            } else {
                throw new BusinessException("添加街道失败！");
            }
        } else {
            if (subdistrictService.updateById(subdistrict)) {
                setPhoneNumbers(subdistrict.getPhoneNumbers(), PhoneNumberSourceTypeEnum.SUBDISTRICT, String.valueOf(subdistrict.getId()));
                phoneNumberService.saveOrUpdateBatch(subdistrict.getPhoneNumbers());
            } else {
                throw new BusinessException("修改街道失败！");
            }
        }
        return "redirect:/subdistrict";
    }

    /**
     * 使用AJAX技术通过街道编号删除
     *
     * @param id 对应编号
     * @return Ajax信息
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public Map<String, Object> deleteSubdistrictForAjax(@PathVariable Long id) {
        Map<String, Object> jsonMap = new HashMap<>(3);
        if (subdistrictService.removeById(id) && phoneNumberService.removeBySource(PhoneNumberSourceTypeEnum.SUBDISTRICT, id)) {
            jsonMap.put("state", 1);
            jsonMap.put("message", "删除街道成功！");
            return jsonMap;
        }
        throw new JsonException("删除街道失败！");
    }

    /**
     * 通过Ajax技术获取街道信息
     *
     * @return 街道信息
     */
    @GetMapping("/load")
    @ResponseBody
    public Map<String, Object> getSubdistrictForAjax() {
        Map<String, Object> jsonMap = new HashMap<>(3);
        jsonMap.put("state", 1);
        jsonMap.put("subdistricts", subdistrictService.list());
        return jsonMap;
    }
}
