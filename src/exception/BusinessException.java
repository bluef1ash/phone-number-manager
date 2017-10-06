package exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 业务异常
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
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
