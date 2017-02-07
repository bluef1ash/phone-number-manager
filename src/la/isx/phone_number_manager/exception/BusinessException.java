package la.isx.phone_number_manager.exception;
/**
 * 业务异常
 * 
 */
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = -5847549478247193367L;
	public BusinessException() {
		super();
	}
	public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}
	public BusinessException(String message) {
		super(message);
	}
	public BusinessException(Throwable cause) {
		super(cause);
	}
}