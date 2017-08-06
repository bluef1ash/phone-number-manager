package exception;
/**
 * 参数异常
 *
 */
public class ParameterException extends RuntimeException {
	private static final long serialVersionUID = 226896736510442587L;
	public ParameterException() {
		super();
	}
	public ParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public ParameterException(String message, Throwable cause) {
		super(message, cause);
	}
	public ParameterException(String message) {
		super(message);
	}
	public ParameterException(Throwable cause) {
		super(cause);
	}
}