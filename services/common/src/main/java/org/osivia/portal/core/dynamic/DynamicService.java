package org.osivia.portal.core.dynamic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;

import org.jboss.portal.common.invocation.AttributeResolver;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.core.container.dynamic.DynamicPortalObjectContainer;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;
import org.springframework.stereotype.Service;

@Service
public class DynamicService implements IDynamicService {
	
    private static final Log log=  LogFactory.getLog(DynamicService.class);


    @Override
    public void startDynamicWindow(PortalControllerContext portalControllerContext, String parentPath, String windowName, String regionId, String portletInstance,
            Map<String, String> windowProperties, Map<String, String> windowParams) throws PortalException {

        ControllerContext ctx = ControllerContextAdapter.getControllerContext(portalControllerContext);

        DynamicPortalObjectContainer poc = (DynamicPortalObjectContainer) ctx.getController().getPortalObjectContainer();

        // Get current page
        PortalObjectId pageId = PortalObjectId.parse(parentPath, PortalObjectPath.CANONICAL_FORMAT);
        Page page = (Page) ctx.getController().getPortalObjectContainer().getObject(pageId);


        // Init properties
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

        
        if( windowParams != null)   {
            for (String keyParam : windowParams.keySet()) {
                parameters.put(keyParam, new String[]{windowParams.get(keyParam)});
            }        
        }
        
        
        
        WindowNavigationalState newNS;
        
        if (!StringUtils.equals(properties.get("osivia.windowState"), "normal")) {
            // On force la maximisation            
            newNS = WindowNavigationalState.bilto(windowNavState, WindowState.MAXIMIZED, windowNavState.getMode(),
                    ParametersStateString.create(parameters));            

        }   else    {

            newNS = WindowNavigationalState.bilto(windowNavState, WindowState.NORMAL, windowNavState.getMode(),
                    ParametersStateString.create(parameters));          
        }

        
        
        ctx.setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);      
        
        // Maj du breadcrumb
        Breadcrumb breadcrumb = (Breadcrumb) ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, "breadcrumb");

        if (breadcrumb == null) {
            breadcrumb = new Breadcrumb();
        }
        else    {
            breadcrumb.getChildren().clear();
        }

        // Ajout du nouvel item
        PageURL url = new PageURL(pageId, ctx);

        String name = properties.get("osivia.title");

        BreadcrumbItem item = new BreadcrumbItem(name, url.toString(), windowId, false);
        
        // Task identifier
        if (windowProperties != null) {
            String taskId = windowProperties.get(ITaskbarService.TASK_ID_WINDOW_PROPERTY);
            item.setTaskId(taskId);
        }            


        breadcrumb.getChildren().add(item);

        ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "breadcrumb", breadcrumb);
     
  
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
        
        log.debug("StartdynamicPage template="+templatePath);        
        
        
        // Base path
        String contentPath;
        
        if( StringUtils.equals(properties.get("osivia.pageType"), "template"))
            contentPath = properties.get("osivia.templateId");
        else
            contentPath = properties.get("osivia.contentId");    
        
        if( StringUtils.isEmpty(pageRestorableName))
            pageRestorableName = RestorablePageUtils.createRestorableName(ctx, pageName, contentPath, displayNames, properties, parameters);
        

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
        
        
        
        // Maj des paramètres publics de la page
        if(parameters == null)
            parameters = new HashMap<>();
            
        NavigationalStateContext nsContext = (NavigationalStateContext) ctx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        Map<QName, String[]> state = new HashMap<QName, String[]>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, entry.getKey()), new String[]{entry.getValue()});
        }
        nsContext.setPageNavigationalState(pageId.toString(), new PageNavigationalState(state));
        
        
        // Maj du breadcrumb
        ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "breadcrumb", null);

        return pageId.toString(PortalObjectPath.CANONICAL_FORMAT);

    }

    @Override
    public void startDynamicWindow(PortalControllerContext portalControllerContext, String regionId, String portletInstance,
            Map<String, String> windowProperties) throws PortalException {

        
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        
        Page page = PortalObjectUtilsInternal.getPage(controllerContext);


        String parentPath = page.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
        
        String windowName = windowProperties.get("osivia.windowName");
        if(windowName == null)
            windowName = "dyna" + System.currentTimeMillis();

        startDynamicWindow(portalControllerContext, parentPath, windowName, regionId, portletInstance, windowProperties, null);


    }

 
}
