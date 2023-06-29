package org.jboss.portal.core.logs;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.LoggerContext;

/**
* 
* As described here https://stackoverflow.com/questions/67075821/log4j-dont-log-after-redeploy
* 
* The Log4j loggerContext is shared between webapp, so if a web app is redeployed the loggerContext shutdowns for all webapps
* and all other webapps don't log any more.
* 
* The only issue is to subclass the default LoggerContext and ignore the shutdown 
* 
* @author jsste
*/

public class PortalLoggerContext extends LoggerContext {


    public PortalLoggerContext(String name, Object externalContext, URI configLocn) {
        super(name, externalContext, configLocn);

    }

    @Override
    public boolean stop(long timeout, TimeUnit timeUnit) {
        // Do nothing
        return false;

    }
    
    

}
