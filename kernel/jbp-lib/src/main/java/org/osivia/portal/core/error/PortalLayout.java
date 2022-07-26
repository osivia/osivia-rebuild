package org.osivia.portal.core.error;

import java.nio.charset.Charset;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.osivia.portal.api.locator.Locator;


@Plugin(name = "PortalLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE, printObject = true)
public class PortalLayout  extends AbstractStringLayout
{
    protected PortalLayout( Charset charset )
    {
        super( charset );
    }

    @Override public String toSerializable( LogEvent event )
    {
        
        // Nuxeo service
        IPortalLoggerService portalLogger = Locator.getService( "osivia:service=LoggerService", IPortalLoggerService.class);
        return portalLogger.log(this, event);
    }
    
    @PluginFactory
    public static PortalLayout createLayout(@PluginAttribute(value = "charset", defaultString = "UTF-8") Charset charset) {
        return new PortalLayout(charset);
    }
}