package org.osivia.portal.ws.portlet;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.servlet.ServletContext;

/**
 * Bootstrap entry point
 * 
 * @author Jean-Sébastien
 */
public class WSUtilPortlet extends GenericPortlet {
    
    
    public static ServletContext servletContext = null;

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        // Portlet context
        PortletContext portletContext = getPortletContext();

//        DriveRestController.portletContext = portletContext;
//        UserRestController.portletContext = portletContext;
//        EtablissementService.portletContext = portletContext;
        
        //Servlet + portlets cohabitations ....
        PortletAppUtils.refreshServletApplicationContext(servletContext);
  }
    
    
    
    @Override
    public void destroy() {
        //Servlet + portlets cohabitations ...        
        PortletAppUtils.removeServletApplicationContext(servletContext);
     }

    
    
}
