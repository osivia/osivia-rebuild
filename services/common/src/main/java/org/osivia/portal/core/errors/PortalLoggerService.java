/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.osivia.portal.core.errors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.osivia.portal.api.log.LoggerMessage;
import org.osivia.portal.core.error.ErrorDescriptor;
import org.osivia.portal.core.error.IPortalLogger;
import org.osivia.portal.core.error.IPortalLoggerService;

import org.osivia.portal.core.error.PortalLayout;
import org.springframework.stereotype.Service;


/**
 * The portal logger Service.
 * 
 * @author Jean-Sébastien Steux
 * @see IPortalLogger
 */
@Service("osivia:service=LoggerService")
public class PortalLoggerService implements IPortalLoggerService {

    /**
     * Default constructor.
     */
    public PortalLoggerService() {
        super();
    }

    /** The date format. */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

    /**
     * Concat.
     *
     * @param sb the sb
     * @param prefix the prefix
     * @param item the item
     */
    private void concat(StringBuffer sb, String prefix, String item, String defaultValue) {
        concat(sb, prefix, item, defaultValue, false);
    }

    /**
     * Concat.
     *
     * @param sb the sb
     * @param prefix the prefix
     * @param item the item
     * @param last the last
     */
    private void concat(StringBuffer sb, String prefix, String item, String defaultValue, boolean last) {
        if (StringUtils.isNotEmpty(item) || defaultValue != null) {
            if (prefix != null) {
                sb.append(prefix);
                sb.append(" ");
            }
            if(StringUtils.isNotEmpty(item))
                sb.append(item);
            else
                sb.append(defaultValue);                
            if (last == false)
                sb.append(" ");
        }   
        
        if( last)
            sb.append("\n");
    }

    /**
     * Format msg.
     *
     * @param param the param
     * @return the string
     */
    private String formatMsg (Object param){
        if (param instanceof Long ||  param instanceof Integer )
            return param.toString();
            else    {
                String formattedMsg = param.toString().replaceAll("\"", "'");
                int crIndex =  formattedMsg.indexOf('\n');
                if( crIndex != -1){
                    formattedMsg = formattedMsg.substring(0, crIndex);
                    
                }
        return  "\"" + formattedMsg + "\"";
            }
    }
    

    /* (non-Javadoc)
     * @see org.osivia.portal.core.error.IPortalLogger#log(org.osivia.portal.core.error.LoggerPatternLayout, org.apache.log4j.spi.LoggingEvent)
     */
    
    @Override
    public String log(PortalLayout layout, LogEvent event) {
        

        PortalLoggerContext loggerContext = null;

        
        /* Get logger context */
        
        // Applicative call at portal level
        if (event.getMessage() instanceof ErrorDescriptor) {
            ErrorDescriptor error = (ErrorDescriptor) event.getMessage();
            loggerContext = (PortalLoggerContext) error.getProperties().get("osivia.log.context");
        } 
        
        // default, build a new logger context
       
        if( loggerContext == null)  {
            loggerContext = new PortalLoggerContext();
        }

        
        String portlet = loggerContext.getPortlet();

        /* Get level */
        Level level = event.getLevel();
        
        // Only processAction are considered as errors
        if ( (portlet != null && !loggerContext.isAction()) && event.getLevel().equals(Level.ERROR)) {
            level = Level.WARN;
        }

        

        
        String page = loggerContext.getPage();
        String user = loggerContext.getUser();


        String dateFormatted = dateFormat.format(new Date());

        StringBuffer sb = new StringBuffer();

        concat(sb, null, dateFormatted, null);
        concat(sb, null, level.toString(), null);
        concat(sb, "user", user, "-");
        
        concat(sb, null, loggerContext.getPortalSessionID(), "-");       

  
        boolean displayPageContext = true;
        String displayMsg = null;
        
        Object logObject = null;
        if( event.getMessage().getParameters().length == 1)
            logObject = event.getMessage().getParameters()[0];

        if (logObject instanceof ErrorDescriptor) {
            String applicativeMessage = "";
            ErrorDescriptor error = (ErrorDescriptor) logObject;
            if (error.getMessage() != null) {
                applicativeMessage = error.getMessage();
            } else {
                if (error.getHttpErrCode() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                    applicativeMessage = "Technical error";
                }
                if (error.getHttpErrCode() == HttpServletResponse.SC_REQUEST_TIMEOUT)  {
                    applicativeMessage = "Timeout expired";
                }
            }
            displayMsg = "\"" + applicativeMessage + "\"";
        } else if (logObject instanceof LoggerMessage)   {
            LoggerMessage loggerMsg = (LoggerMessage) logObject;
            displayMsg = loggerMsg.getMsg();
            if( loggerMsg.isContextAware()) {
                displayPageContext = false;
            }
        }   else
            displayMsg = formatMsg(event.getMessage());
      
        if( displayPageContext){
            concat(sb, "page", page, null);
            concat(sb, "portlet", portlet, null);
        }
        
        concat(sb, null, displayMsg, null, true);

        return sb.toString();

    }




}
