package org.osivia.portal.core.layouts;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.servlet.BufferingRequestWrapper;
import org.jboss.portal.common.servlet.BufferingResponseWrapper;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.theme.LayoutInfo;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.core.constants.InternalConstants;

public class DynamicLayoutService implements IDynamicLayoutService {

    @Override
    public String getLayoutCode(ControllerContext controllerContext, Page page) throws ControllerException {
       
//        String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);  
//        String layoutCode = page.getProperty("osivia.layout."+layoutId+".code");
       
        String layoutCode = checkLayout(controllerContext, page);
        
        return layoutCode;
    }
    
    
    /**
     * Check layout attributes.
     *
     * @param controllerContext controller context
     * @param page page
     * @throws ControllerException
     */
    private String checkLayout(ControllerContext controllerContext, Page page) throws ControllerException {
        // Layout
        LayoutService layoutService = controllerContext.getController().getPageService().getLayoutService();
        String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
        PortalLayout layout = layoutService.getLayout(layoutId, false);
        if (layout == null) {
            throw new ControllerException("Layout " + layoutId + "not found for page " + page.toString());
        }
        LayoutInfo layoutInfo = layout.getLayoutInfo();
        String uri = layoutInfo.getURI();


        // Search maximized window
        boolean maximized = false;
        Collection<PortalObject> children = page.getChildren(PortalObject.WINDOW_MASK);
        for (PortalObject child : children) {
            Window window = (Window) child;
            NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());
            WindowNavigationalState windowNavState = (WindowNavigationalState) controllerContext
                    .getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);

            if ((windowNavState != null) && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {
                maximized = true;
                break;
            }
        }


        // At this time, windows displaying is only checked for index and maximized state
        if (maximized) {
            uri = layoutInfo.getURI("maximized");
        }


        // Context path
        String contextPath = layoutInfo.getContextPath();

        // Server invocation
        ServerInvocation serverInvocation = controllerContext.getServerInvocation();
        // Server context
        ServerInvocationContext serverContext = serverInvocation.getServerContext();
        // Servlet context
        ServletContext servletContext = serverContext.getClientRequest().getSession().getServletContext().getContext(contextPath);
        // Locales
        Locale[] locales = serverInvocation.getRequest().getLocales();

        // Request
        BufferingRequestWrapper request = new BufferingRequestWrapper(serverContext.getClientRequest(), contextPath, locales);
        request.setAttribute(ThemeConstants.ATTR_LAYOUT_HTML_EXTRACTOR, true);

        // Response
        BufferingResponseWrapper response = new BufferingResponseWrapper(serverContext.getClientResponse());

        // Request dispatcher
        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(uri);
        try {
            requestDispatcher.include(request, response);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
        
        return response.getContent();

     }
    
    
  
}
