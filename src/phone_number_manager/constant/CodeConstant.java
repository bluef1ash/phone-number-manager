package constant;

import main.entity.ResultCode;

/**
 * 状态码常量
 */
public final class CodeConstant {
    public final static ResultCode CSRF_ERROR = new ResultCode("101", "CSRF ERROR:无效的token，或者token过期");
}
