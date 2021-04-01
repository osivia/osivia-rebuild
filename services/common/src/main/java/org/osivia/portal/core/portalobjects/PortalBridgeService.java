package org.osivia.portal.core.portalobjects;

import java.util.List;

import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.portlet.spi.ServerContext;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.portalobject.bridge.PortalBridge;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.springframework.stereotype.Service;

@Service
public class PortalBridgeService implements PortalBridge {
    

    @Override
    public PortalObjectId getPageId(PortalControllerContext portalContext) {
        return PortalObjectUtilsInternal.getPageId(ControllerContextAdapter.getControllerContext(portalContext));
    }
    
    @Override
    public Page getPage(PortalControllerContext portalContext) {
        return PortalObjectUtilsInternal.getPage(ControllerContextAdapter.getControllerContext(portalContext));
     }

    @Override
    public Portal getPortal(PortalControllerContext portalContext) {
        return PortalObjectUtilsInternal.getPortal(ControllerContextAdapter.getControllerContext(portalContext));

    }
    @Override
    public  boolean  isAdmin(PortalControllerContext portalCtx) {
        return PortalObjectUtilsInternal.isAdmin(ControllerContextAdapter.getControllerContext(portalCtx));
       
    }

    @Override
    public Window getMaximizedWindow(PortalControllerContext portalCtx, Page page) {
        return PortalObjectUtilsInternal.getMaximizedWindow(ControllerContextAdapter.getControllerContext(portalCtx), page);
    }

    @Override
    public PortalObject getObject(PortalControllerContext portalCtx, PortalObjectId id) throws IllegalArgumentException {
        return PortalObjectUtilsInternal.getObject(ControllerContextAdapter.getControllerContext(portalCtx), id);
    }

    @Override
    public List<?> getDomains(PortalControllerContext portalCtx) throws IllegalArgumentException {

        return (List<?>) ControllerContextAdapter.getControllerContext(portalCtx).getAttribute(Scope.SESSION_SCOPE, InternalConstants.USER_DOMAINS_ATTRIBUTE);
    }

    @Override
    public String getPortalContextPath(PortalControllerContext portalCtx) {
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalCtx);
        ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();
        return serverContext.getPortalContextPath();
    }

    @Override
    public void setPortalSessionAttribute(PortalControllerContext portalCtx, String name, Object object) {

        ControllerContextAdapter.getControllerContext(portalCtx).setAttribute(Scope.PRINCIPAL_SCOPE, name, object);
    }

    @Override
    public Object getPortalSessionAttribute(PortalControllerContext portalCtx, String name) {
        return  ControllerContextAdapter.getControllerContext(portalCtx).getAttribute(Scope.PRINCIPAL_SCOPE, name);
    }

    
}
