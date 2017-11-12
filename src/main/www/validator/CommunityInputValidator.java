package www.validator;

import exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import utils.CommonUtil;
import www.entity.Community;
import www.entity.CommunityResident;
import www.service.CommunityResidentService;
import www.service.CommunityService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 社区添加/更新验证
 */
public class CommunityInputValidator implements Validator {
    private String message;
    private String field;
    private CommunityService communityService;
    private HttpServletRequest request;
    private String errorCode;

    public CommunityInputValidator() {
    }

    public CommunityInputValidator(CommunityService communityService, HttpServletRequest request) {
        this.communityService = communityService;
        this.request = request;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Community.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "communityName", "community.communityName.required", "社区名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "communityTelephone", "community.communityTelephone.required", "社区联系方式不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "actualNumber", "community.actualNumber.required", "社区分包数不能为空！");
            Community community = (Community) target;
            if (!checkInput(community)) {
                errors.rejectValue(field, errorCode, message);
            }
        } catch (Exception e) {
            throw new BusinessException("社区验证失败！");
        }
    }

    /**
     * 验证输入数据
     *
     * @param community
     * @return
     */
    private Boolean checkInput(Community community) throws Exception {
        // 联系方式合法
        if (!checkedPhone(community.getCommunityTelephone())) {
            return false;
        }
        // 联系方式重复
        List<Community> isPhonesRepeat = communityService.findCommunityByCommunity(community);
        return checkedListData(isPhonesRepeat);
    }

    /**
     * 验证联系方式是否合法
     *
     * @param phone
     * @return
     */
    private boolean checkedPhone(String phone) {
        Pattern regex = Pattern.compile("(?iUs)^(?:(?:[\\(（]?(?:[0-9]{3,4})?\\s*[\\)）-])?\\d{7,9}(?:[-转]\\d{2,6})?)|(?:[\\(\\+]?(?:86)?[\\)]?0?1[34578]{1}\\d{9})$");
        Matcher matcher = regex.matcher(phone);
        if (!matcher.matches()) {
            message = "输入的联系方式不合法，请检查后重试！";
            return false;
        }
        return true;
    }

    /**
     * 验证数据库返回数据是否为空
     *
     * @param communities
     * @return
     */
    private boolean checkedListData(List<Community> communities) {
        if (communities != null && communities.size() > 0) {
            field = "communityId";
            errorCode = "community.communityId.errorCode";
            message = "所填内容已存在！";
            return false;
        }
        return true;
    }
}
