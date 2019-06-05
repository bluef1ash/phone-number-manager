package security;


import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.io.IOException;

/**
 * Session信息过期处理
 *
 * @author 廿二月的天
 */
public class SimpleRedirectSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {

    private final String destinationUrl;
    private final RedirectStrategy redirectStrategy;

    public SimpleRedirectSessionInformationExpiredStrategy(String invalidSessionUrl) {
        this(invalidSessionUrl, new DefaultRedirectStrategy());
    }

    public SimpleRedirectSessionInformationExpiredStrategy(String invalidSessionUrl, RedirectStrategy redirectStrategy) {
        this.destinationUrl = invalidSessionUrl;
        this.redirectStrategy = redirectStrategy;
    }

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent sessionInformationExpiredEvent) throws IOException {
        redirectStrategy.sendRedirect(sessionInformationExpiredEvent.getRequest(), sessionInformationExpiredEvent.getResponse(), destinationUrl);
    }
}
