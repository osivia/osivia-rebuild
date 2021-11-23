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
package org.osivia.portal.core.theming.attributesbundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.theme.page.WindowContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.DocumentType;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.menubar.IMenubarService;
import org.osivia.portal.api.menubar.MenubarContainer;
import org.osivia.portal.api.menubar.MenubarDropdown;
import org.osivia.portal.api.menubar.MenubarGroup;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.path.PortletPathItem;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.taskbar.TaskbarTask;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.theming.IInternalAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;

import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.DomainContextualization;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.PortalURLImpl;

import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;

import org.osivia.portal.core.web.IWebIdService;
import org.springframework.stereotype.Service;

/**
 * Breadcrumb attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class BreadcrumbAttributesBundle implements IInternalAttributesBundle {

    /** Singleton instance. */
    private static BreadcrumbAttributesBundle instance;

    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;
    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Portal object container. */
    private final PortalObjectContainer portalObjectContainer;
    /** WebId service. */
    private final IWebIdService webIdService;
    /** Menubar service. */
    private final IMenubarService menubarService;

    private final CMSService cmsService;
    
    private final ITaskbarService taskbarService;
    

    /** Attributes names. */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private BreadcrumbAttributesBundle() {
        super();

        // URL Factory
        this.urlFactory = Locator.getService(IPortalUrlFactory.MBEAN_NAME, IPortalUrlFactory.class);
        // CMS service locator
        this.cmsServiceLocator = Locator.getService(ICMSServiceLocator.MBEAN_NAME, ICMSServiceLocator.class);
        // Portal object container
        this.portalObjectContainer = Locator.getService("portal:container=PortalObject", PortalObjectContainer.class);
        // Webid service
        this.webIdService = Locator.getService(IWebIdService.MBEAN_NAME, IWebIdService.class);
        // Menubar service
        this.menubarService = Locator.getService(IMenubarService.MBEAN_NAME, IMenubarService.class);

        this.cmsService = Locator.getService(CMSService.class);
        this.taskbarService= Locator.getService(ITaskbarService.MBEAN_NAME, ITaskbarService.class);

        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_BREADCRUMB);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static BreadcrumbAttributesBundle getInstance() {
        if (instance == null) {
            instance = new BreadcrumbAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(ControllerContext controllerContext, Page page, Map<String, Object> attributes) throws ControllerException {
        // Breadcrumb
        Breadcrumb breadcrumb = this.computeBreadcrumb(controllerContext, page);
        attributes.put(Constants.ATTR_BREADCRUMB, breadcrumb);
    }


    /**
     * Compute breadcrumb.
     *
     * @param renderPageCommand render page command
     * @param pageRendition page rendition
     * @return breadcrumb
     * @throws ControllerException
     */
    @SuppressWarnings("unchecked")
    private Breadcrumb computeBreadcrumb(ControllerContext controllerContext, Page page) throws ControllerException {


        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(
                controllerContext.getServerInvocation().getServerContext().getClientRequest());
        // State context
        NavigationalStateContext stateContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        // Window context map
        // TODO : window context map
        // Map<?, ?> windowContextMap = pageRendition.getPageResult().getWindowContextMap();
        Map<?, ?> windowContextMap = new HashMap<>();


        // CMS context
        CMSContext cmsContext = new CMSContext(portalControllerContext);


        // Current page state
        PageNavigationalState pageState = stateContext.getPageNavigationalState(page.getId().toString());

        // Breadcrumb memo
        Breadcrumb breadcrumbMemo = (Breadcrumb) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "breadcrumb");
        if (breadcrumbMemo == null) {
            breadcrumbMemo = new Breadcrumb();
            controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "breadcrumb", breadcrumbMemo);
        }

        // Breadcrum initialization
        Breadcrumb breadcrumb = new Breadcrumb();



        if( "template".equals(page.getProperty("osivia.pageType") )) {
   
            
        }   else    {
             try {
                 
                 
                // CMS Publication
                String sContentId = page.getProperties().get("osivia.contentId");
                if ((sContentId != null)) {
                    
                    // current document
                    UniversalID contentId = new UniversalID(sContentId);
                    String title;
                    
                    // first navigation item
                    NavigationItem navItem = cmsService.getCMSSession(cmsContext).getNavigationItem(contentId);
                    
                    boolean hideItem = false;
                    
                    if( navItem.getDocumentId().equals(contentId))    {
                        title = navItem.getTitle();
                        
                        if( !navItem.isVisible())
                            hideItem = true;
                        
                        if( ! navItem.isRoot()) {
                            navItem = navItem.getParent();
                        }
                        else    {
                            navItem = null;
                        }
                    }   else    {
                        Document doc = cmsService.getCMSSession(cmsContext).getDocument(contentId);       
                        title = (String) doc.getTitle();
                    }
                    
                    if( hideItem == false)
                        addDocToBreadcrumb(portalControllerContext, cmsContext, breadcrumb, contentId, title);
                    
                    
                    // Browse navigation tree
                    if( navItem != null) {
                       boolean isRoot = false;
                       do {
                           if( navItem.isVisible())
                               addDocToBreadcrumb(portalControllerContext, cmsContext, breadcrumb, navItem.getDocumentId(), navItem.getTitle());
                              
                            if( ! navItem.isRoot())
                                navItem = navItem.getParent();
                            else
                                isRoot = true;
                        } while( ! isRoot);
                    }
               }
            } catch (Exception e) {
                throw new ControllerException(e);
            }
         }        

        // Find first non navigation portlet index
        int firstPortletIndex = -1;
        int i = 0;
        for (BreadcrumbItem item : breadcrumbMemo.getChildren()) {
            if (!item.isNavigationPlayer()) {
                firstPortletIndex = i;
                break;
            }
            i++;
        }
        
        
        
         
        
        // Update current item
        if (breadcrumbMemo.getChildren().size() > 0) {
       
           Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);
            for (PortalObject window : windows) {

                NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());
                WindowNavigationalState windowNavState = (WindowNavigationalState) controllerContext.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE,
                        nsKey);


                if (windowNavState != null && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {
                    
                    BreadcrumbItem last = breadcrumbMemo.getChildren().get(breadcrumbMemo.getChildren().size() - 1);
                    
                    // Update path
                    List<PortletPathItem> portletPath = (List<PortletPathItem>) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE,
                            Constants.PORTLET_ATTR_PORTLET_PATH);
                    if (portletPath != null) {
                        // Valorize labels and path related URLs

                        for (PortletPathItem pathItem : portletPath) {
                            // Set the content as a render parameter
                            ParametersStateString parameters = ParametersStateString.create();
                            for (Entry<String, String> name : pathItem.getRenderParams().entrySet()) {
                                parameters.setValue(name.getKey(), name.getValue());
                            }

                            // Add public parameters
                            if (pageState != null) {
                                Map<QName, String[]> ps = pageState.getParameters();
                                for (Entry<QName, String[]> pageEntry : ps.entrySet()) {
                                    if (parameters.getValue(pageEntry.getKey().toString()) == null) {
                                        if (pageEntry.getValue().length > 0) {
                                            if (!"init-state".equals(pageEntry.getKey().toString()) && !"unsetMaxMode".equals(pageEntry.getKey().toString())) {
                                                parameters.setValue(pageEntry.getKey().toString(), pageEntry.getValue()[0]);
                                            }
                                        }
                                    }
                                }
                            }


                            PortalObjectId targetWindowId = window.getId();

                            ControllerCommand renderCmd = new InvokePortletWindowRenderCommand(targetWindowId, Mode.VIEW, null, parameters);

                            // Perform a render URL on the target window
                            String url = new PortalURLImpl(renderCmd, controllerContext, null, null).toString();
                            pathItem.setUrl(url);
                           
                            String label = pathItem.getLabel();
                            pathItem.setLabel(label);
                            last.setPortletPath(portletPath);

                        }
                     }
                }
            }
        }

       
        // Add memorized items
        for (BreadcrumbItem itemMemo : breadcrumbMemo.getChildren()) {

            if (!itemMemo.isNavigationPlayer()) {
                if (itemMemo.getPortletPath() != null) {
                    // Add corresponding item to portlet path
                    for (PortletPathItem pathItem : itemMemo.getPortletPath()) {
                        BreadcrumbItem pathDisplayItem = new BreadcrumbItem(pathItem.getLabel(), pathItem.getUrl(), itemMemo.getId(), true);
                        breadcrumb.getChildren().add(pathDisplayItem);
                    }
                } else {
                    // No portlet path : add corresponding item to portlet title
                    breadcrumb.getChildren().add(itemMemo);
                }
            }
        }
        
        

        return breadcrumb;
    }


    private void addDocToBreadcrumb(PortalControllerContext portalControllerContext, CMSContext cmsContext, Breadcrumb breadcrumb, UniversalID contentId, String title)
            throws CMSException {

        String url = urlFactory.getViewContentUrl(portalControllerContext, cmsContext, contentId);
        BreadcrumbItem contentItem = new BreadcrumbItem(title, url, "", true);
        breadcrumb.getChildren().add(0,contentItem);

    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
