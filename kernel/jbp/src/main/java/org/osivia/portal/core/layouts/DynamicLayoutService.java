package org.osivia.portal.core.layouts;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.theme.ThemeConstants;

public class DynamicLayoutService implements IDynamicLayoutService {

    @Override
    public String getLayoutCode(ControllerContext controllerContext, Page page) throws ControllerException {
       
        String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);  
        String layoutCode = page.getProperty("osivia.layout."+layoutId+".code");
        return layoutCode;
    }
    
    
  
}
