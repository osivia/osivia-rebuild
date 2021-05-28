package org.osivia.portal.core.layouts;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.servlet.BufferingRequestWrapper;
import org.jboss.portal.common.servlet.BufferingResponseWrapper;
import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.LayoutInfo;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.web.ServletContextDispatcher;
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


        // Server invocation
        ServerInvocation serverInvocation = controllerContext.getServerInvocation();
        // Server context
        ServerInvocationContext serverContext = serverInvocation.getServerContext();

        // Locales
        Locale[] locales = serverInvocation.getRequest().getLocales();

        // Request
        BufferingRequestWrapper request = new BufferingRequestWrapper(serverContext.getClientRequest(), serverContext.getClientRequest().getContextPath(), locales);

        // Response
        BufferingResponseWrapper response = new BufferingResponseWrapper(serverContext.getClientResponse());


        ServletContextDispatcher dispatcher = new ServletContextDispatcher(request, response, serverInvocation.getRequest().getServer().getServletContainer());
        MarkupInfo markupInfo = (MarkupInfo)serverInvocation.getResponse().getContentInfo();
        
        RenderPageCommand rpc = new RenderPageCommand(page.getId());
        ControllerResponse resp = controllerContext.execute(rpc);

        final PageRendition rendition = (PageRendition)resp;

        
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
        
        if( maximized)
            rendition.getPageResult().setLayoutState("maximized");
        
        try {
            rendition.render(markupInfo, dispatcher);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
     
        
        return response.getContent();

     }
    
   
  
}
