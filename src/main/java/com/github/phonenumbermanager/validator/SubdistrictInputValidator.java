package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.SubdistrictService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * 街道添加/更新验证
 *
 * @author 廿二月的天
 */
public class SubdistrictInputValidator extends BaseInputValidator<Subdistrict> implements Validator {
    private final SubdistrictService subdistrictService;

    public SubdistrictInputValidator(SubdistrictService subdistrictService, HttpServletRequest request) {
        this.subdistrictService = subdistrictService;
        this.request = request;
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "name", "subdistrict.name.required", "街道名称不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "phoneNumbers", "subdistrict.phoneNumbers.required", "街道联系方式不能为空！");
        Subdistrict subdistrict = (Subdistrict) target;
        // 联系方式合法
        if (!checkedPhones(subdistrict.getPhoneNumbers())) {
            return false;
        }
        try {
            // 联系方式重复
            List<Subdistrict> isPhonesRepeat = subdistrictService.get(subdistrict);
            return checkedListData(isPhonesRepeat, subdistrict.getId());
        } catch (Exception e) {
            throw new BusinessException("街道验证失败！");
        }
    }

    /**
     * 验证数据库返回数据是否为空
     *
     * @param subdistricts 需要验证的街道办事处对象的集合
     * @param id           传入的编号
     * @return 是否验证成功
     */
    private boolean checkedListData(List<Subdistrict> subdistricts, Serializable id) {
        if (subdistricts != null && subdistricts.size() > 0) {
            for (Subdistrict subdistrict : subdistricts) {
                if (subdistrict.getId().equals(id)) {
                    break;
                }
                field = "id";
                errorCode = "subdistrict.id.errorCode";
                message = "所填内容已存在！";
                return false;
            }
        }
        return true;
    }
}
