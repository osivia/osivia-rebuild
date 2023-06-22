package org.osivia.portal.kernel.tomcat;

import java.io.IOException;
import java.util.Set;
import java.util.logging.LogManager;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.lang.Nullable;

/**
 * 
 * When log4j2 is installed in /opt/portal/log4j2/, juli does not initialised the logger by itself as the web app starts
 * 
 * It can lead to exceptions during undeployment phase (where the log configuration is out of context)
 * 
 *    java.io.FileNotFoundException: /opt/portal/temp/14-toutatice-applications-safran-portlet/WEB-INF/lib/xxxx.jar
 *    java.lang.LinkageError: loader (instance of  org/apache/catalina/loader/ParallelWebappClassLoader): attempted  duplicate class definition for name: "org/apache/catalina/loader/JdbcLeakPrevention"
 * 
 * @author jsste
 */

public class LogInitializerListener implements ServletContainerInitializer {
    private static final Log logger = LogFactory.getLog(LogInitializerListener.class);

    @Override
    public void onStartup(@Nullable Set<Class<?>> webAppInitializerClasses, ServletContext servletContext)
            throws ServletException {
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (SecurityException | IOException e) {
            logger.error("Log initializer can't read configuration");
        }
    }

}

