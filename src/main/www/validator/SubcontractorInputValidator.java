package www.validator;

import exception.BusinessException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import utils.StringCheckedRegexUtil;
import www.entity.Subcontractor;
import www.service.SubcontractorService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 社区分包人添加/更新验证
 *
 * @author 廿二月的天
 */
public class SubcontractorInputValidator extends BaseInputValidator implements Validator {
    private SubcontractorService subcontractorService;

    public SubcontractorInputValidator() {
    }

    public SubcontractorInputValidator(SubcontractorService subcontractorService, HttpServletRequest request) {
        this.subcontractorService = subcontractorService;
        this.request = request;
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "name", "subcontractor.name.required", "社区分包人名称不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "telephone", "subcontractor.telephone.required", "社区分包人联系方式不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "communityId", "subcontractor.communityId.required", "社区分包人所属社区不能为空！");
        Subcontractor subcontractor = (Subcontractor) target;
        // 联系方式合法
        if (!checkedPhone(subcontractor.getTelephone())) {
            return false;
        }
        try {
            List<Subcontractor> isPhonesRepeat = subcontractorService.findObjects(subcontractor);
            return checkedListData(isPhonesRepeat);
        } catch (Exception e) {
            throw new BusinessException("社区分包人验证失败！");
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(Subcontractor.class);
    }

    /**
     * 验证联系方式是否合法
     *
     * @param phone 需要验证的联系方式
     * @return 联系方式是否合法
     */
    private boolean checkedPhone(String phone) {
        if (!StringCheckedRegexUtil.checkPhone(phone)) {
            message = "输入的联系方式不合法，请检查后重试！";
            return false;
        }
        return true;
    }

    /**
     * 验证数据库返回数据是否为空
     *
     * @param subcontractors 需要验证的社区分包人对象的集合
     * @return 是否验证成功
     */
    private boolean checkedListData(List<Subcontractor> subcontractors) {
        if (subcontractors != null && subcontractors.size() > 0) {
            field = "subcontractorId";
            errorCode = "subcontractor.subcontractorId.errorCode";
            message = "所填内容已存在！";
            return false;
        }
        return true;
    }
}
