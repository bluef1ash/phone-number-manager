package la.isx.phone_number_manager.exception;
/**
 * 非法操作异常
 * 
 */
public class IllegalOperationException extends RuntimeException {
	private static final long serialVersionUID = -4541265328073201293L;
	public IllegalOperationException() {
		super();
	}
	public IllegalOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public IllegalOperationException(String message, Throwable cause) {
		super(message, cause);
	}
	public IllegalOperationException(String message) {
		super(message);
	}
	public IllegalOperationException(Throwable cause) {
		super(cause);
	}
}