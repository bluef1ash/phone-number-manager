package www.validator;

import constant.PhoneCheckedTypes;
import exception.BusinessException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import utils.StringCheckedRegexUtil;
import www.entity.Community;
import www.service.CommunityService;

import javax.servlet.http.HttpServletRequest;
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
            ValidationUtils.rejectIfEmpty(errors, "communityName", "community.communityName.required", "社区名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "communityTelephone", "community.communityTelephone.required", "社区联系方式不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "actualNumber", "community.actualNumber.required", "社区分包数不能为空！");
            Community community = (Community) target;
            // 联系方式合法
            if (!checkedPhone(community.getCommunityTelephone())) {
                return false;
            }
            // 联系方式重复
            List<Community> isPhonesRepeat = communityService.findCommunityByCommunity(community);
            return checkedListData(isPhonesRepeat);
        } catch (Exception e) {
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
        if (StringCheckedRegexUtil.checkPhone(phone) == PhoneCheckedTypes.FAILED) {
            message = "输入的联系方式不合法，请检查后重试！";
            return false;
        }
        return true;
    }

    /**
     * 验证数据库返回数据是否为空
     *
     * @param communities 需要验证的社区居委会对象的集合
     * @return 是否验证成功
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
