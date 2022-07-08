/*
 * (C) Copyright 2020 OSIVIA (http://www.osivia.com)
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
 *
 */

package org.osivia.portal.core.content;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;
import org.springframework.beans.factory.annotation.Autowired;



public class ViewContentCommandFactoryService extends AbstractCommandFactory implements ViewContentCommandFactory {
    
    @Autowired
    private IPreviewModeService previewModeService;
    
    
    /** The locale service. */
    @Autowired
    private ILocaleService localeService;


    private IPreviewModeService getPreviewModeService() {

        return previewModeService;       
    }
    
    private ILocaleService getLocaleService() {

        return localeService;       
    }
    

    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {

        ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();
        
        if( requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }
        
        String[] names = requestPath.split("/");
        
        

        String contentId = names[0] + "/" + names[1];

        
        boolean preview = false;
        Locale locale = Locale.FRENCH; 
        
        if( names.length == 4) {
            locale = new Locale(names[2]);
            preview = BooleanUtils.toBoolean(names[3]);
        }

        
        Map<String,String> pageParams = null;
        if (parameterMap != null)
        {
          
           try
           {
              if (parameterMap.get("pageParams") != null)
              {
                  String sPageParms = URLDecoder.decode(parameterMap.get("pageParams")[0], "UTF-8");
                  pageParams =  WindowPropertiesEncoder.decodeProperties(sPageParms);
              }
           }
           catch (UnsupportedEncodingException e)
           {
              throw new RuntimeException(e);
           }      
        }
        
        
        ViewContentCommand cmsCommand = new ViewContentCommand(contentId, locale, preview, null, pageParams, null, null);
        return cmsCommand;
    }

}

