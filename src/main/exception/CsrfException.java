package exception;

/**
 * CSRF验证异常
 *
 * @author 廿二月的天
 */
public class CsrfException extends RuntimeException {
    public CsrfException() {
        super();
    }
    public CsrfException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    public CsrfException(String message, Throwable cause) {
        super(message, cause);
    }
    public CsrfException(String message) {
        super(message);
    }
    public CsrfException(Throwable cause) {
        super(cause);
    }
}
