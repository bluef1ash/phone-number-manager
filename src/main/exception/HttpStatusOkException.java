package exception;

/**
 * HTTP正常状态异常
 *
 * @author 廿二月的天
 */
public class HttpStatusOkException extends RuntimeException {

    private static final long serialVersionUID = -2242010762244365156L;

    public HttpStatusOkException() {
        super();
    }

    public HttpStatusOkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public HttpStatusOkException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatusOkException(String message) {
        super(message);
    }

    public HttpStatusOkException(Throwable cause) {
        super(cause);
    }
}
