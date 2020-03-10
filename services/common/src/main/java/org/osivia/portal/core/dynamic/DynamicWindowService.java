package org.osivia.portal.core.dynamic;

import java.util.HashMap;
import java.util.Map;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicWindowService;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;
import org.springframework.stereotype.Service;

@Service
public class DynamicWindowService implements IDynamicWindowService {

     
    @Override
    public void startDynamicWindow(PortalControllerContext portalControllerContext, String regionId, String portletInstance,
            Map<String, String> windowProperties) throws PortalException {
         
        ControllerContext ctx = ControllerContextAdapter.getControllerContext(portalControllerContext);
        
        DynamicPortalObjectContainer poc = (DynamicPortalObjectContainer) ctx.getController().getPortalObjectContainer();
        
        Window window  = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");
        
        if( window != null) {
    
            Map<String, String> properties = new HashMap<String, String>();
            properties.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
            properties.put(ThemeConstants.PORTAL_PROP_REGION, regionId);
    
            DynamicWindowBean windowBean = new DynamicWindowBean(window.getPage().getId(), "dyna"+System.currentTimeMillis(), "SampleInstance", properties, null);
    
            poc.addDynamicWindow(windowBean);
             
            
        }   else throw new PortalException("no window in request");

    }

}
