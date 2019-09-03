package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.constant.PhoneCheckedTypes;
import com.github.phonenumbermanager.entity.Subdistrict;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.SubdistrictService;
import com.github.phonenumbermanager.utils.StringCheckedRegexUtils;
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
    private SubdistrictService subdistrictService;

    public SubdistrictInputValidator(SubdistrictService subdistrictService, HttpServletRequest request) {
        this.subdistrictService = subdistrictService;
        this.request = request;
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "name", "subdistrict.name.required", "街道名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "landline", "subdistrict.landline.required", "街道联系方式不能为空！");
            Subdistrict subdistrict = (Subdistrict) target;
            // 联系方式合法
            if (!checkedPhone(subdistrict.getLandline())) {
                return false;
            }
            // 联系方式重复
            List<Subdistrict> isPhonesRepeat = subdistrictService.find(subdistrict);
            return checkedListData(isPhonesRepeat, subdistrict.getId());
        } catch (Exception e) {
            throw new BusinessException("街道验证失败！");
        }
    }

    /**
     * 验证联系方式是否合法
     *
     * @param phone 需要验证的联系方式
     * @return 是否验证成功
     */
    private boolean checkedPhone(String phone) {
        if (StringCheckedRegexUtils.checkPhone(phone) == PhoneCheckedTypes.FAILED) {
            field = "landline";
            errorCode = "subdistrict.landline.errorCode";
            message = "输入的联系方式不合法，请检查后重试！";
            return false;
        }
        return true;
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
