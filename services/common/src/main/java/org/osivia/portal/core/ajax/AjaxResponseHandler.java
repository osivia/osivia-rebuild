/******************************************************************************
 * JBoss, a division of Red Hat *
 * Copyright 2009, Red Hat Middleware, LLC, and individual *
 * contributors as indicated by the @authors tag. See the *
 * copyright.txt in the distribution for a full listing of *
 * individual contributors. *
 * *
 * This is free software; you can redistribute it and/or modify it *
 * under the terms of the GNU Lesser General Public License as *
 * published by the Free Software Foundation; either version 2.1 of *
 * the License, or (at your option) any later version. *
 * *
 * This software is distributed in the hope that it will be useful, *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU *
 * Lesser General Public License for more details. *
 * *
 * You should have received a copy of the GNU Lesser General Public *
 * License along with this software; if not, write to the Free *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. *
 ******************************************************************************/

package org.osivia.portal.core.ajax;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.handler.AjaxResponse;
import org.jboss.portal.core.controller.handler.CommandForward;
import org.jboss.portal.core.controller.handler.HTTPResponse;
import org.jboss.portal.core.controller.handler.HandlerResponse;
import org.jboss.portal.core.controller.handler.ResponseHandler;
import org.jboss.portal.core.controller.handler.ResponseHandlerException;
import org.jboss.portal.core.controller.portlet.ControllerPageNavigationalState;
import org.jboss.portal.core.controller.portlet.ControllerPortletControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.render.RenderWindowCommand;
import org.jboss.portal.core.model.portal.command.response.MarkupResponse;
import org.jboss.portal.core.model.portal.command.response.PortletWindowActionResponse;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.content.WindowRendition;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateChange;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.core.navstate.NavigationalStateObjectChange;
import org.jboss.portal.core.theme.WindowContextFactory;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.response.PageResource;
import org.jboss.portal.theme.impl.render.dynamic.response.UpdatePageLocationResponse;
import org.jboss.portal.theme.impl.render.dynamic.response.UpdatePageStateResponse;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.ThemeContext;
import org.jboss.portal.web.ServletContextDispatcher;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.api.cms.service.SpaceCacheBean;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.menubar.IMenubarService;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.container.dynamic.DynamicTemplatePage;
import org.osivia.portal.core.layouts.DynamicLayoutService;
import org.osivia.portal.core.menubar.MenubarUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.RestorePageCommand;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;
import org.osivia.portal.core.resources.ResourceHandler;
import org.w3c.dom.Element;

/**
 * todo:
 * <p/>
 * 1/ interpret more responses types
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 12958 $
 */
public class AjaxResponseHandler implements ResponseHandler {

    /** . */
    private static final Logger log = Logger.getLogger(AjaxResponseHandler.class);

    /** . */
    private PortalObjectContainer portalObjectContainer;

    private DynamicLayoutService dynamicLayoutService;
    



    /** . */
    private PageService pageService;

    public PortalObjectContainer getPortalObjectContainer() {
        return portalObjectContainer;
    }

    public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }

    public PageService getPageService() {
        return pageService;
    }

    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }


    public DynamicLayoutService getDynamicLayoutService() {
        return dynamicLayoutService;
    }


    public void setDynamicLayoutService(DynamicLayoutService dynamicLayoutService) {
        this.dynamicLayoutService = dynamicLayoutService;
    }


    public HandlerResponse processCommandResponse(ControllerContext controllerContext, ControllerCommand commeand, ControllerResponse controllerResponse)
            throws ResponseHandlerException {
        try {
            
            controllerContext.getServerInvocation().getServerContext().getClientRequest().setAttribute("osivia.controllerContext", controllerContext);
            
            String redirectUrl = (String) controllerContext.getServerInvocation().getServerContext().getClientRequest().getAttribute("osivia.full_refresh_url");
            if( redirectUrl != null)
                 return HTTPResponse.sendRedirect(redirectUrl);
            
            if (controllerResponse instanceof PortletWindowActionResponse) {
                PortletWindowActionResponse pwr = (PortletWindowActionResponse) controllerResponse;
                StateString contentState = pwr.getContentState();
                WindowState windowState = pwr.getWindowState();
                Mode mode = pwr.getMode();
                ControllerCommand renderCmd = new InvokePortletWindowRenderCommand(pwr.getWindowId(), mode, windowState, contentState);
                if (renderCmd != null) {
                    controllerContext.getServerInvocation().getServerContext().getClientRequest().setAttribute("osivia.actionForward", Boolean.TRUE);
                    return new CommandForward(renderCmd, null);
                } else {
                    return null;
                }
            } else if (controllerResponse instanceof UpdatePageResponse) {
                UpdatePageResponse upw = (UpdatePageResponse) controllerResponse;
                
                // Portal controller context
                PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext.getServerInvocation().getServerContext().getClientRequest());
                

                PortalObjectId pageId = upw.getPageId();

                
                PortalObjectUtilsInternal.setPageId(controllerContext, pageId);
 
                PortalObjectId oldPageId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE,"osivia.initialPageId");
                
                Boolean pageChange = false;
                
                if( oldPageId != null) {
                    if( !oldPageId.equals(pageId))  {
                        pageChange = true;
                    }
                }   else
                    pageChange = true;
                
                   

                // Changes have been commited during the restore page state
                // ctx.getChanges doesn't contain modified windows
                // for examples, maximized window is not considered ads dirty
                if( commeand instanceof RestorePageCommand) {
                    pageChange = true;
                }
                
                
                boolean pageStructureModified = false;  
                boolean pushHistory = false;  

//                String pagePath = (String) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.pagePath");
//                if (pagePath != null) {
//                    pageId = PortalObjectId.parse("", pagePath, PortalObjectPath.CANONICAL_FORMAT);
//                    newAjaxPage = true;
//                }

                // Obtain page
                final Page page = (Page) portalObjectContainer.getObject(pageId);
                
                
                 Long lastUpdateTs = (Long) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE,"osivia.updateTs."+ pageId.toString(PortalObjectPath.CANONICAL_FORMAT));
                 if( lastUpdateTs != null) {
                     if (page.getUpdateTs() > lastUpdateTs)
                         pageStructureModified = true;
                 }
                 controllerContext.setAttribute(ControllerCommand.SESSION_SCOPE,"osivia.updateTs."+ pageId.toString(PortalObjectPath.CANONICAL_FORMAT), System.currentTimeMillis());
                 
                 
                 
                 
                 
                 
                 // Check if Space structure has been modified
                 String spaceId = page.getProperty("osivia.spaceId");
                 if (StringUtils.isNotEmpty(spaceId)) {
                     Long lastDisplayTs = (Long) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE,"osivia.lastDisplayTs."+ pageId.toString(PortalObjectPath.CANONICAL_FORMAT));


                     // Get Id
                     PortalControllerContext portalCtx = new PortalControllerContext(controllerContext.getServerInvocation().getServerContext().getClientRequest());
                     CMSController ctrl = new CMSController(portalCtx);

                     CMSSession session;
                     try {
                         session = Locator.getService(org.osivia.portal.api.cms.service.CMSService.class).getCMSSession(ctrl.getCMSContext());

                         SpaceCacheBean modifiedTs = session.getSpaceCacheInformations(new UniversalID(spaceId));
                         if ( modifiedTs.getLastSpaceModification() != null) {
                             if (lastDisplayTs == null || lastDisplayTs < modifiedTs.getLastSpaceModification()) {
                                 PageProperties.getProperties().setCheckingSpaceTS(modifiedTs.getLastSpaceModification());
                              }
                         }
                     } catch (CMSException e) {
                         throw new RuntimeException(e);
                     }

                     
                     controllerContext.setAttribute(ControllerCommand.SESSION_SCOPE,"osivia.lastDisplayTs."+ pageId.toString(PortalObjectPath.CANONICAL_FORMAT), System.currentTimeMillis());
                 }

                
                log.info("updateTs = "+page.getUpdateTs());
                
                
                
                boolean refreshPageStructure = false;              

                //
                NavigationalStateContext ctx = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

                // The windows marked dirty during the request
                Set dirtyWindowIds = new HashSet();

                // Whether we need a full refresh or not
                boolean fullRefresh = false;
                // Prevent menubar refresh indicator
                boolean preventMenubarRefresh = false;                

                // in case we have we have a page navigational state...
                Map<String, String[]> parameters = null;



                if (BooleanUtils.isNotTrue(pageChange)  && ctx.getChanges() != null )  {
                    for (Iterator i = ctx.getChanges(); i.hasNext();) {
                        NavigationalStateChange change = (NavigationalStateChange) i.next();

                        // A change that modifies potentially the page structure
                        if (!(change instanceof NavigationalStateObjectChange)) {
                            fullRefresh = true;
                            break;
                        }
                        NavigationalStateObjectChange update = (NavigationalStateObjectChange) change;

                        // Get the state key
                        NavigationalStateKey key = update.getKey();

                        // We consider only portal object types
                        Class type = key.getType();
                        if (type == WindowNavigationalState.class) {
                            // Get old window state
                            WindowNavigationalState oldNS = (WindowNavigationalState) update.getOldValue();
                            WindowState oldWindowState = oldNS != null ? oldNS.getWindowState() : null;

                            // Get new window state
                            WindowNavigationalState newNS = (WindowNavigationalState) update.getNewValue();
                            WindowState newWindowState = newNS != null ? newNS.getWindowState() : null;

                            // Check if window state requires a refresh
                            if (WindowState.MAXIMIZED.equals(oldWindowState)) {
                                if (!WindowState.MAXIMIZED.equals(newWindowState)) {
                                    refreshPageStructure = true;
                                    pushHistory = true;
                                }
                            } else if (WindowState.MAXIMIZED.equals(newWindowState)) {
                                refreshPageStructure = true;
                                pushHistory = true;
                            }

                            
                            // Render parameters modified
                            if( BooleanUtils.isNotTrue((Boolean)controllerContext.getServerInvocation().getServerContext().getClientRequest().getAttribute("osivia.actionForward")))  {
                                if (( newNS != null && oldNS == null) 
                                        || (newNS == null && oldNS != null)
                                        || (newNS != null && oldNS != null && newNS.getContentState() != null && !newNS.getContentState().equals(oldNS.getContentState())))  {
                                    pushHistory = true;
                                }
                            }
                                    
                            
                            // Collect the dirty window id
                            dirtyWindowIds.add(key.getId());
                        } else if (type == PageNavigationalState.class) {
                            
                            PageNavigationalState pns = (PageNavigationalState) update.getNewValue();
                            
                            // force full refresh for now... for JBPORTAL-2326
                            
                            // Mise en commentaire portage tomcat8
                            // Use case : éciter le rechargement page sur modification méta-données document
 
                            /*fullRefresh = true;

                            // TODO: implement proper propagation of PRPs and events
                            PageNavigationalState pns = (PageNavigationalState) update.getNewValue();

                            if (pns != null) {

                                // todo: fix-me, this is a hack to copy PRPs when we force the full refresh as parameters to ViewPageCommand...
                                Map<QName, String[]> qNameMap = pns.getParameters();
                                if (qNameMap != null && !qNameMap.isEmpty()) {
                                    parameters = new HashMap<String, String[]>(qNameMap.size());

                                    for (Map.Entry<QName, String[]> entry : qNameMap.entrySet()) {
                                        parameters.put(entry.getKey().toString(), entry.getValue());
                                    }
                                }



                            }
                            */                            
                        }
                    }
                }   else    {
                    // New Ajax Page
                    
                    refreshPageStructure = true;
                    pushHistory = true;

                 }
                
                if( BooleanUtils.isTrue(pageStructureModified))  {
                    // Modification of stucture
                    refreshPageStructure = true;
                }
                
                
                if (PageProperties.getProperties().isRefreshingPage() || PageProperties.getProperties().isCheckingSpaceContents()) {
                    controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.refreshCaches", Boolean.TRUE);
                    dirtyWindowIds.clear();
                    Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);
                    for (PortalObject window : windows) {
                        if (!dirtyWindowIds.contains(window)) {
                            dirtyWindowIds.add(window.getId());
                        }
                    }
                }
                
                
                
                
                // Check if Space content has been modified
                if (StringUtils.isNotEmpty(spaceId)) {

                    // Get Id
                    PortalControllerContext portalCtx = new PortalControllerContext(
                            controllerContext.getServerInvocation().getServerContext().getClientRequest());
                    CMSController ctrl = new CMSController(portalCtx);

                    CMSSession session;
                    try {
                        session = Locator.getService(org.osivia.portal.api.cms.service.CMSService.class).getCMSSession(ctrl.getCMSContext());

                        SpaceCacheBean modifiedTs = session.getSpaceCacheInformations(new UniversalID(spaceId));
                        if (modifiedTs.getLastContentModification() != null) {
                            // CMS Cache windows
                            for (PortalObject window : page.getChildren(PortalObject.WINDOW_MASK)) {
                                if( "spaceContent".equals(window.getProperty("osivia.cms.cache.scope")))  {
    
                                    Long lastSentTs = (Long) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE,
                                            "osivia.ajax.ts." + window.getId().toString(PortalObjectPath.SAFEST_FORMAT));
                                    if (lastSentTs == null || modifiedTs.getLastContentModification() > lastSentTs) {
                                        if (!dirtyWindowIds.contains(window.getId())) {
                                            dirtyWindowIds.add(window.getId());
                                        }
                                        
                                        // Needed for spring models
                                        controllerContext.setAttribute(Scope.REQUEST_SCOPE,
                                                "osivia.refreshWindow." + window.getId().toString(PortalObjectPath.SAFEST_FORMAT), Boolean.TRUE);
                                    }
                                }

                            }

                        }
                    } catch (CMSException e) {
                        throw new RuntimeException(e);
                    }

                }
                
                
                
                
  /*
                // CMS Cache windows
                for (PortalObject window : page.getChildren(PortalObject.WINDOW_MASK)) {
                    Long updateTs = requestCacheMgr.getCMSRequestUpdateTs(controllerContext, (Window) window);
                    if (updateTs != null) {
                        Long lastSentTs = (Long) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE,
                                "osivia.ajax.ts." + window.getId().toString(PortalObjectPath.SAFEST_FORMAT));
                        if (lastSentTs == null || updateTs > lastSentTs) {
                            if (!dirtyWindowIds.contains(window.getId())) {
                                dirtyWindowIds.add(window.getId());
                            }
                            controllerContext.setAttribute(Scope.REQUEST_SCOPE,
                                    "osivia.refreshWindow." + window.getId().toString(PortalObjectPath.SAFEST_FORMAT), Boolean.TRUE);
                        }
                    }
                }
 */               
                

                if (!fullRefresh) {
                    // Prevent Ajax refresh
                    String preventAjaxRefreshWindowId = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "osivia.ajax.preventRefreshWindowId");
                    if (StringUtils.isNotEmpty(preventAjaxRefreshWindowId)) {
                        PortalObjectId objectId = PortalObjectId.parse(preventAjaxRefreshWindowId, PortalObjectPath.CANONICAL_FORMAT);
                        preventMenubarRefresh = dirtyWindowIds.remove(objectId);
                    }                    
                }
                
                
                if( refreshPageStructure)   {
                    log.info("refresh page structure");
                }

                // Commit changes
                ctx.applyChanges();

                //
                if (!fullRefresh) {
                    ArrayList<PortalObject> refreshedWindows = new ArrayList<PortalObject>();


                    // Windows to refresh
                    if ( refreshPageStructure) {
                        Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);
                        for (PortalObject window : windows) {
                            refreshedWindows.add( window);
                        }
                        
                    }   else    {
                    
                        // New window
                        List<PortalObjectId> requestDirtyWindowIds = (List<PortalObjectId>) controllerContext.getServerInvocation().getServerContext()
                                .getClientRequest().getAttribute("osivia.dynamic.dirtyWindows");
                        if (requestDirtyWindowIds != null) {
                            for (PortalObjectId requestDirtyWindow : requestDirtyWindowIds) {
                                if (!dirtyWindowIds.contains(requestDirtyWindow)) {
                                    dirtyWindowIds.add(requestDirtyWindow);
                                }
                            }
                        }

                        
                        // Windows
                        Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);
                        
                        
                        for (Object dirtyWindowId : dirtyWindowIds) {
                            PortalObjectId poid = (PortalObjectId) dirtyWindowId;
                            String windowName = poid.getPath().getLastComponentName();
                            PortalObject child = page.getChild(windowName, Window.class);
                            if (child != null) {
                                refreshedWindows.add(child);
                            }
                        }
                    }

                    // Obtain layout
                    LayoutService layoutService = getPageService().getLayoutService();
                    PortalLayout layout = RenderPageCommand.getLayout(layoutService, page);
                    
                    
                    Integer viewId = PageMarkerUtils.generateViewState(controllerContext);
                    PageMarkerUtils.setViewState(controllerContext, viewId);

                    //
                    UpdatePageStateResponse updatePage = new UpdatePageStateResponse(ctx.getViewId());
                    updatePage.setSessionCheck((String) controllerContext.getServerInvocation().getServerContext().getClientRequest().getSession().getAttribute(InternalConstants.SESSION_CHECK));
                    
                    RestorePageCommand restoreCmd = new RestorePageCommand();
                    updatePage.setRestoreUrl(controllerContext.renderURL(restoreCmd, null, null));
                    
                    
                    ViewPageCommand vpc = new ViewPageCommand(pageId);
                    updatePage.setFullStateUrl(controllerContext.renderURL(vpc, null, null));
                    
                  
                    
                    // Regions
                    Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);
                    for (PortalObject window : windows) {
                        
                        NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());                        
                        WindowNavigationalState windowNavState = (WindowNavigationalState) controllerContext.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);

                        String region = window.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);
                        if ((windowNavState != null) && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {
                            region = "maximized";
                        }
                        
                        
                        
                        if (StringUtils.isNotEmpty(region)) {
                            List<String> regionList = updatePage.getRegions().get(region);
                            if (regionList == null) {
                                regionList = new ArrayList<String>();
                                updatePage.getRegions().put(region, regionList);
                            }
                            regionList.add(window.getId().toString(PortalObjectPath.SAFEST_FORMAT));
                        }
                    }
                    
 
                    // Layout
                    if (refreshPageStructure) {
                        String layoutCode = getDynamicLayoutService().getLayoutCode(controllerContext, page);

                        updatePage.setLayout(layoutCode);
                    }
                    
                    updatePage.setPageChanged(pageChange);
                    
                    // History indicator
                    updatePage.setPushHistory(pushHistory);


                    // Call to the theme framework
                    PageResult res = new PageResult(page.getName(), page.getProperties());

                    //
                    ServerInvocation invocation = controllerContext.getServerInvocation();

                    //
                    WindowContextFactory wcf = new WindowContextFactory(controllerContext);

                    //
                    ControllerPortletControllerContext portletControllerContext = new ControllerPortletControllerContext(controllerContext, page);
                    ControllerPageNavigationalState pageNavigationalState = portletControllerContext.getStateControllerContext()
                            .createPortletPageNavigationalState(true);

                    Set<PageResource> resources = new LinkedHashSet<PageResource>();
                    
                    
                    // Sort by order
                     List<Window> sortedWindows = new ArrayList<Window>();
                    for (Iterator i = refreshedWindows.iterator(); i.hasNext(); ) {
                        sortedWindows.add((Window) i.next());
                    }

                    Collections.sort(sortedWindows, new WindowComparator());
                    
                    //
                    for ( Window refreshedWindow: sortedWindows ) {
                        try {
                            RenderWindowCommand rwc = new RenderWindowCommand(pageNavigationalState, refreshedWindow.getId());
                            WindowRendition rendition = rwc.render(controllerContext);

                            //
                            if (rendition != null) {
                                ControllerResponse resp = rendition.getControllerResponse();

                                //
                                if (resp instanceof MarkupResponse) {
                                    WindowContext wc = wcf.createWindowContext(refreshedWindow, rendition);

                                    //
                                    res.addWindowContext(wc);
                                    
                                    
                                    this.refreshWindowContext(controllerContext, layout, updatePage, resources, res, wc);
                                    
                                  } else {
                                    //TODO:display error
                                    //updatePage.addFragment(refreshedWindow.getId().toString(PortalObjectPath.SAFEST_FORMAT), "An error occured during rendering");
                                }
                            } else {
                                // We'd better do a full refresh for now
                                // It could be handled as a portlet removal in the protocol between the client side and server side
                                fullRefresh = true;
                            }
                        } catch (Exception e) {
                            log.error("An error occured during the computation of window markup", e);

                            //
                            fullRefresh = true;
                        }
                        
                        if( fullRefresh)
                            break;
                    }
                    
                     
                    updatePage.setResources(resources);
                    
                    // Notifications & menubar refresh
                    if (!fullRefresh) {
                        try {
                            // Check if current page is a modal
                            boolean modal = false;

                            if( page instanceof DynamicTemplatePage)     {
                                PortalObjectId templateId = ((DynamicTemplatePage) page).getTemplateId();
                                if(templateId.getPath().getLastComponentName().equals("OSIVIA_PAGE_MODAL"))
                                    modal = true;
                            }
                            
                            if (!modal) {
                                // Notifications window context
                              
                                WindowContext notificationsWindowContext = NotificationsUtils.createNotificationsWindowContext(portalControllerContext);
                                res.addWindowContext(notificationsWindowContext);
                                this.refreshWindowContext(controllerContext, layout, updatePage, resources, res, notificationsWindowContext);
                                
                                List<String> notificationsRegionList = new ArrayList<String>();
                                notificationsRegionList.add(NotificationsUtils.WINDOW_ID);                            
                                updatePage.getRegions().put(NotificationsUtils.REGION_NAME, notificationsRegionList);

                                // Menubar region needs data in request
                                // If portlet is not displayed, menu can't be refreshed
                                if (!preventMenubarRefresh) {
                               
                                    // Menubar window context
                                    WindowContext menubarWindowContext = MenubarUtils.createContentNavbarActionsWindowContext(portalControllerContext);
                                    res.addWindowContext(menubarWindowContext);
                                    this.refreshWindowContext(controllerContext, layout, updatePage, resources, res, menubarWindowContext);
                                    
                                    List<String> regionList = new ArrayList<String>();
                                    regionList.add(IMenubarService.MENUBAR_WINDOW_ID);                            
                                    updatePage.getRegions().put(IMenubarService.MENUBAR_REGION_NAME, regionList);
                                                                       
                               }


                            }
                            
            
                            
                        } catch (Exception e) {
                            log.error("An error occured during the computation of window markup", e);

                            //
                            fullRefresh = true;
                        }
                    }                    

                    //
                    if (!fullRefresh) {
                        PageMarkerUtils.savePageState(controllerContext, updatePage.getViewState());                        
                        return new AjaxResponse(updatePage);
                    }
                }
                
                


                // We perform a full refresh
                ViewPageCommand rpc;
                if (parameters == null) {
                    rpc = new ViewPageCommand(page.getId());
                } else {
                    // if we have parameters from a PNS, feed them to ViewPageCommand (this is rather hackish)
                    rpc = new ViewPageCommand(page.getId(), parameters);
                }

                String url = controllerContext.renderURL(rpc, null, null);
                UpdatePageLocationResponse dresp = new UpdatePageLocationResponse(url);
                return new AjaxResponse(dresp);
            }else if (controllerResponse instanceof RedirectionResponse) { 
                
                String location = ((RedirectionResponse) controllerResponse).getLocation();
                
                if( location.equals("/back") || (location.equals("/back-refresh")) || (location.equals("/refresh")) )   {
                    // Default (http redirection)
                    UpdatePageLocationResponse dresp = new UpdatePageLocationResponse(((RedirectionResponse) controllerResponse).getLocation());
                    return new AjaxResponse(dresp);                   
                }
                

                boolean ajaxRedirect = false;
                String contextPath  = controllerContext.getServerInvocation().getServerContext().getClientRequest().getContextPath();
               
                String requestedLocation = ((RedirectionResponse) controllerResponse).getLocation();
                URL redirectURL;
                try {
                    redirectURL = new URL(requestedLocation);
                    if(redirectURL.getPath().startsWith(contextPath))
                        ajaxRedirect = true;
                            
                } catch (MalformedURLException e) {
                    // do nothing
                }

                if( ajaxRedirect)   {
                    // Ajax redirection
                    return HTTPResponse.sendRedirect(((RedirectionResponse) controllerResponse).getLocation());
                } 
                
                
                
                // Default (http redirection)
                UpdatePageLocationResponse dresp = new UpdatePageLocationResponse(((RedirectionResponse) controllerResponse).getLocation());
                return new AjaxResponse(dresp);
                
            }
            
            else {
                return null;
            }
        } catch (ControllerException e) {
            throw new ResponseHandlerException(e);
        }
    }

    private void refreshWindowContext(ControllerContext controllerContext, PortalLayout layout, UpdatePageStateResponse updatePage, Set<PageResource> resources,PageResult res,
            WindowContext wc)  throws Exception {
        
        // Server invocation
        ServerInvocation invocation = controllerContext.getServerInvocation();


        //
        MarkupInfo markupInfo = (MarkupInfo) invocation.getResponse().getContentInfo();

        // The buffer
        StringWriter buffer = new StringWriter();

        // Get a dispatcher
        ServletContextDispatcher dispatcher = new ServletContextDispatcher(invocation.getServerContext().getClientRequest(),
                invocation.getServerContext().getClientResponse(), controllerContext.getServletContainer());

        // Not really used for now in that context, so we can pass null (need to change that of course)
        ThemeContext themeContext = new ThemeContext(null, null);

        // get render context
        RendererContext rendererContext = layout.getRenderContext(themeContext, markupInfo, dispatcher, buffer);

        // Push page
        rendererContext.pushObjectRenderContext(res);

        // Push region
        Region region = res.getRegion2(wc.getRegionName());
        rendererContext.pushObjectRenderContext(region);

        // Render
        rendererContext.render(wc);
        
        List<Element> headElements = wc.getResult().getHeaderContent();
        if (headElements != null) {
            for (Element element : headElements) {
                if (!"title".equals(element.getNodeName().toLowerCase())) {
                       resources.add(ResourceHandler.getResource(element.toString()));
                }
            }
        }                                    

        // Pop region
        rendererContext.popObjectRenderContext();

        // Pop page
        rendererContext.popObjectRenderContext();

        // Add render to the page
        updatePage.addFragment(wc.getId(), buffer.toString());
        
        
        controllerContext.setAttribute(ControllerCommand.SESSION_SCOPE,"osivia.ajax.ts."+ wc.getId(), System.currentTimeMillis());
        
    }
}
