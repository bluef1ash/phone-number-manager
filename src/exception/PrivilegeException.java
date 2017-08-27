package exception;
/**
 * 权限异常
 * 
 */
public class PrivilegeException extends RuntimeException {
	private static final long serialVersionUID = 5120129511031601666L;
	public PrivilegeException() {
		super();
	}
	public PrivilegeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public PrivilegeException(String message, Throwable cause) {
		super(message, cause);
	}
	public PrivilegeException(String message) {
		super(message);
	}
	public PrivilegeException(Throwable cause) {
		super(cause);
	}
}