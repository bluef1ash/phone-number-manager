package listener;

import org.apache.logging.log4j.core.config.Configurator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Enumeration;

/**
 * LOG4J配置监听器
 *
 * @author 廿二月的天
 */
public class Log4j2ConfigListener implements ServletContextListener {
    private static final String KEY = "log4j.configurationFile";

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Configurator.initialize("Log4j2", getContextParam(sce));
    }

    private String getContextParam(ServletContextEvent event) {
        Enumeration<String> names = event.getServletContext().getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = event.getServletContext().getInitParameter(name);
            if (name.trim().equals(KEY)) {
                return value;
            }
        }
        return null;
    }
}
