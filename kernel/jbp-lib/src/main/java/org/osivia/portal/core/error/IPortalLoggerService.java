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

package org.osivia.portal.core.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.core.LogEvent;



/**
 * The Interface IPortalLogger (internal to portal)
 *
 * @author Jean-Sébastien Steux
 */
public interface IPortalLoggerService {
    


    
    
    /**
     * Log custom implementation
     *
     * @param layout the layout
     * @param event the event
     * @return the string
     */
    public abstract String log( PortalLayout layout, LogEvent event);
    
};
