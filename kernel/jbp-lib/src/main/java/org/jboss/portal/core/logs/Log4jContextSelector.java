package org.jboss.portal.core.logs;


import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * This class can be used to define a custom logger repository. 
 * 
 * As described here https://stackoverflow.com/questions/67075821/log4j-dont-log-after-redeploy
 * 
 * The Log4j loggerContext is shared between webapp, so if a web app is redeployed the loggerContext shutdowns for all webapps
 * and all other webapps don't log any more.
 * 
 * The only issue is to subclass the default LoggerContext and ignore the shutdown 
 * 
 *  
 */
public class Log4jContextSelector implements ContextSelector {

    private static final LoggerContext CONTEXT = new LoggerContext("Default");

    private static final ConcurrentMap<String, LoggerContext> CONTEXT_MAP =
        new ConcurrentHashMap<>();

    private static final StatusLogger LOGGER = StatusLogger.getLogger();

    public Log4jContextSelector() {
    }

    @Override
    public void shutdown(String fqcn, ClassLoader loader, boolean currentContext, boolean allContexts) {
        LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
        if (ctx == null) {
            String loggingContextName = getContextName();
            if (loggingContextName != null) {
                ctx = CONTEXT_MAP.get(loggingContextName);
            }
        }
        if (ctx != null) {
            ctx.stop(DEFAULT_STOP_TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean hasContext(String fqcn, ClassLoader loader, boolean currentContext) {
        LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
        if (ctx == null) {
            String loggingContextName = getContextName();
            if (loggingContextName == null) {
                return false;
            }
            ctx = CONTEXT_MAP.get(loggingContextName);
        }
        return ctx != null && ctx.isStarted();
    }

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
        return getContext(fqcn, loader, currentContext, null);
    }

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext,
                                    final URI configLocation) {

        final LoggerContext lc = ContextAnchor.THREAD_CONTEXT.get();
        if (lc != null) {
            return lc;
        }

        String loggingContextName = "generic";


        return loggingContextName == null ? CONTEXT : locateContext(loggingContextName, null, configLocation);
    }

    private String getContextName() {
        String loggingContextName = "generic";

        return loggingContextName;
    }


    public LoggerContext locateContext(final String name, final Object externalContext, final URI configLocation) {
        if (name == null) {
            LOGGER.error("A context name is required to locate a LoggerContext");
            return null;
        }
        if (!CONTEXT_MAP.containsKey(name)) {
            final LoggerContext ctx = new PortalLoggerContext(name, externalContext, configLocation);
            CONTEXT_MAP.putIfAbsent(name, ctx);
        }
        return CONTEXT_MAP.get(name);
    }

    @Override
    public void removeContext(final LoggerContext context) {

        for (final Map.Entry<String, LoggerContext> entry : CONTEXT_MAP.entrySet()) {
            if (entry.getValue().equals(context)) {
                CONTEXT_MAP.remove(entry.getKey());
            }
        }
    }

    @Override
    public boolean isClassLoaderDependent() {
        return false;
    }


    public LoggerContext removeContext(final String name) {
        return CONTEXT_MAP.remove(name);
    }

    @Override
    public List<LoggerContext> getLoggerContexts() {
        return Collections.unmodifiableList(new ArrayList<>(CONTEXT_MAP.values()));
    }

}
