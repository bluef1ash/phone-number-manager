package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.constant.PhoneCheckedTypes;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.SubcontractorService;
import com.github.phonenumbermanager.utils.StringCheckedRegexUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * 社区分包人添加/更新验证
 *
 * @author 廿二月的天
 */
public class SubcontractorInputValidator extends BaseInputValidator<Subcontractor> implements Validator {
    private SubcontractorService subcontractorService;

    public SubcontractorInputValidator(SubcontractorService subcontractorService, HttpServletRequest request) {
        this.subcontractorService = subcontractorService;
        this.request = request;
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "name", "subcontractor.name.required", "社区分包人名称不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "mobile", "subcontractor.mobile.required", "社区分包人联系方式不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "communityId", "subcontractor.communityId.required", "社区分包人所属社区不能为空！");
        Subcontractor subcontractor = (Subcontractor) target;
        // 联系方式合法
        if (!checkedPhone(subcontractor.getMobile())) {
            return false;
        }
        try {
            List<Subcontractor> isPhonesRepeat = subcontractorService.find(subcontractor);
            return checkedListData(isPhonesRepeat, subcontractor.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("社区分包人验证失败！");
        }
    }

    /**
     * 验证联系方式是否合法
     *
     * @param phone 需要验证的联系方式
     * @return 联系方式是否合法
     */
    private boolean checkedPhone(String phone) {
        if (StringCheckedRegexUtils.checkPhone(phone) == PhoneCheckedTypes.FAILED) {
            message = "输入的联系方式不合法，请检查后重试！";
            return false;
        }
        return true;
    }

    /**
     * 验证数据库返回数据是否为空
     *
     * @param subcontractors 需要验证的社区分包人对象的集合
     * @param id             传入的编号
     * @return 是否验证成功
     */
    private boolean checkedListData(List<Subcontractor> subcontractors, Serializable id) {
        if (subcontractors != null && subcontractors.size() > 0) {
            for (Subcontractor subcontractor : subcontractors) {
                if (subcontractor.getId().equals(id)) {
                    break;
                }
                field = "id";
                errorCode = "subcontractor.id.errorCode";
                message = "所填内容已存在！";
                return false;
            }
        }
        return true;
    }
}
