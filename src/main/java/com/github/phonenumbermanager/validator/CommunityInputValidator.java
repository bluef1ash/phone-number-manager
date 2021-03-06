package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.constant.PhoneCheckedTypes;
import com.github.phonenumbermanager.entity.Community;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.CommunityService;
import com.github.phonenumbermanager.utils.StringCheckedRegexUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * 社区添加/更新验证
 *
 * @author 廿二月的天
 */
public class CommunityInputValidator extends BaseInputValidator<Community> implements Validator {
    private CommunityService communityService;

    public CommunityInputValidator(CommunityService communityService, HttpServletRequest request) {
        this.communityService = communityService;
        this.request = request;
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "name", "community.name.required", "社区名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "landline", "community.landline.required", "社区联系方式不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "actualNumber", "community.actualNumber.required", "社区分包数不能为空！");
            Community community = (Community) target;
            // 联系方式合法
            if (!checkedPhone(community.getLandline())) {
                return false;
            }
            // 联系方式重复
            List<Community> isPhonesRepeat = communityService.find(community);
            return checkedListData(isPhonesRepeat, community.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("社区验证失败！");
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
     * @param communities 需要验证的社区居委会对象的集合
     * @param id          传入的编号
     * @return 是否验证成功
     */
    private boolean checkedListData(List<Community> communities, Serializable id) {
        if (communities != null && communities.size() > 0) {
            for (Community community : communities) {
                if (community.getId().equals(id)) {
                    break;
                }
                field = "id";
                errorCode = "community.id.errorCode";
                message = "所填内容已存在！";
                return false;
            }
        }
        return true;
    }
}
