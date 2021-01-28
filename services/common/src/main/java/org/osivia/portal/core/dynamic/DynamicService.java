package org.osivia.portal.core.dynamic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.AttributeResolver;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.core.container.dynamic.DynamicPortalObjectContainer;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.springframework.stereotype.Service;

@Service
public class DynamicService implements IDynamicService {


    @Override
    public void startDynamicWindow(PortalControllerContext portalControllerContext, String parentPath, String windowName, String regionId, String portletInstance,
            Map<String, String> windowProperties) throws PortalException {

        ControllerContext ctx = ControllerContextAdapter.getControllerContext(portalControllerContext);

        DynamicPortalObjectContainer poc = (DynamicPortalObjectContainer) ctx.getController().getPortalObjectContainer();

        PortalObjectId pageId = PortalObjectId.parse(parentPath, PortalObjectPath.CANONICAL_FORMAT);


        Map<String, String> properties = new HashMap<String, String>();
        properties.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
        properties.put(ThemeConstants.PORTAL_PROP_REGION, regionId);

        for (String dynaKey : windowProperties.keySet()) {
            properties.put(dynaKey, windowProperties.get(dynaKey));
        }

        DynamicWindowBean windowBean = new DynamicWindowBean(pageId, windowName, portletInstance, properties, null);

        poc.addDynamicWindow(windowBean);

        PortalObjectId windowId = new PortalObjectId(pageId.getNamespace(), new PortalObjectPath(pageId.getPath().toString(PortalObjectPath.CANONICAL_FORMAT).concat("/").concat(windowName), PortalObjectPath.CANONICAL_FORMAT));

        // On force la maximisation
        NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, windowId);
        WindowNavigationalState windowNavState = WindowNavigationalState.create();
        Map<String, String[]> parameters = new HashMap<String, String[]>();


        WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.MAXIMIZED, windowNavState.getMode(),
                ParametersStateString.create(parameters));

        ctx.setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
    }

    @Override
    public String startDynamicPage(PortalControllerContext portalControllerContext, String parentPath, String pageName, Map<Locale, String> displayNames,
            String templatePath, Map<String, String> properties, Map<String, String> parameters, String pageRestorableName) {

        ControllerContext ctx = ControllerContextAdapter.getControllerContext(portalControllerContext);

        DynamicPortalObjectContainer poc = (DynamicPortalObjectContainer) ctx.getController().getPortalObjectContainer();

        // Récupération page
        PortalObjectId poid = PortalObjectId.parse(parentPath, PortalObjectPath.CANONICAL_FORMAT);
        PortalObject parent = ctx.getController().getPortalObjectContainer().getObject(poid);


        PortalObjectId potemplateid = PortalObjectId.parse(templatePath, PortalObjectPath.CANONICAL_FORMAT);
        
        System.out.println("StartdynamicPage template="+templatePath);        
        
        
        // Base path
        String basePath = properties.get("osivia.contentId");
        
        if( StringUtils.isEmpty(pageRestorableName))
            pageRestorableName = RestorablePageUtils.createRestorableName(ctx, pageName, potemplateid.toString(PortalObjectPath.SAFEST_FORMAT), basePath, displayNames,
                properties, parameters);
        

        DynamicPageBean pageBean = new DynamicPageBean(parent, pageRestorableName, pageName, displayNames, potemplateid, properties);
        poc.addDynamicPage(pageBean);
        
        
        
        

        PortalObjectId pageId = new PortalObjectId(parent.getId().getNamespace(),
                new PortalObjectPath(parent.getId().getPath().toString().concat("/").concat(pageRestorableName), PortalObjectPath.CANONICAL_FORMAT));
        
        // Remove portlets cache

        AttributeResolver resolver = ctx.getAttributeResolver(Scope.PRINCIPAL_SCOPE);
        Page page = (Page) ctx.getController().getPortalObjectContainer().getObject(pageId);
        for (PortalObject po : page.getChildren(PortalObject.WINDOW_MASK))    {
            Window window = (Window) po;
            String scopeKey = "cached_markup." + window.getId();    
            resolver.setAttribute(scopeKey, null);            
        }
        
        

        return pageId.toString(PortalObjectPath.CANONICAL_FORMAT);

    }

    @Override
    public void startDynamicWindow(PortalControllerContext portalControllerContext, String regionId, String portletInstance,
            Map<String, String> windowProperties) throws PortalException {

        
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        
        Page page = PortalObjectUtils.getPage(controllerContext);


        String parentPath = page.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
        
        String windowName = "dyna" + System.currentTimeMillis();

        startDynamicWindow(portalControllerContext, parentPath, windowName, regionId, portletInstance, windowProperties);


    }


}
