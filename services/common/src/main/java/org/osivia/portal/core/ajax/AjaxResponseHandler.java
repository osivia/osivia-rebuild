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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.ErrorResponse;
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
import org.osivia.portal.core.layouts.DynamicLayoutService;
import org.osivia.portal.core.page.RestorePageCommand;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.resources.ResourceHandler;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            

            
            
            if (controllerResponse instanceof PortletWindowActionResponse) {
                PortletWindowActionResponse pwr = (PortletWindowActionResponse) controllerResponse;
                StateString contentState = pwr.getContentState();
                WindowState windowState = pwr.getWindowState();
                Mode mode = pwr.getMode();
                ControllerCommand renderCmd = new InvokePortletWindowRenderCommand(pwr.getWindowId(), mode, windowState, contentState);
                if (renderCmd != null) {
                    return new CommandForward(renderCmd, null);
                } else {
                    return null;
                }
            } else if (controllerResponse instanceof UpdatePageResponse) {
                UpdatePageResponse upw = (UpdatePageResponse) controllerResponse;
                

                PortalObjectId pageId = upw.getPageId();

                
                PortalObjectUtils.setPageId(controllerContext, pageId);
                
                
                //TODO : conversation scope
                PortalObjectId oldPageId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE,"osivia.ajaxPageId");
                controllerContext.setAttribute(ControllerCommand.SESSION_SCOPE,"osivia.ajaxPageId", pageId);
                
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
                
                log.info("updateTs = "+page.getUpdateTs());
                
                
                
                boolean refreshPageStructure = false;              

                //
                NavigationalStateContext ctx = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

                // The windows marked dirty during the request
                Set dirtyWindowIds = new HashSet();

                // Whether we need a full refresh or not
                boolean fullRefresh = false;

                // in case we have we have a page navigational state...
                Map<String, String[]> parameters = null;


                if (BooleanUtils.isNotTrue(pageChange) && BooleanUtils.isNotTrue(pageStructureModified) && ctx.getChanges() != null ) {
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
                                }
                            } else if (WindowState.MAXIMIZED.equals(newWindowState)) {
                                refreshPageStructure = true;
                            }

                            // Collect the dirty window id
                            dirtyWindowIds.add(key.getId());
                        } else if (type == PageNavigationalState.class) {
                            // force full refresh for now... for JBPORTAL-2326
                            fullRefresh = true;

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

                                /*
                                 * CoordinationManager coordinationManager = controllerContext.getController().getCoordinationManager();
                                 * for (QName qName : pns.getParameters().keySet())
                                 * {
                                 * //
                                 * }
                                 */
                            }
                        }
                    }
                }   else    {
                    // New Ajax Page
                    
                    refreshPageStructure = true;

                    dirtyWindowIds.clear();
                    Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);
                    for (PortalObject window : windows) {
                        if (!dirtyWindowIds.contains(window)) {
                            dirtyWindowIds.add(window.getId());
                        }
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
                    
                    //
                    for (Iterator i = refreshedWindows.iterator(); i.hasNext() && !fullRefresh;) {
                        try {
                            Window refreshedWindow = (Window) i.next();
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
                    }
                    
                    updatePage.setResources(resources);

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
            
            else if (controllerResponse instanceof ErrorResponse) { 
                ErrorResponse errorResp = (ErrorResponse) controllerResponse;
                log.error("An error occured during the execution of the command", errorResp.getCause());
                return null;
            }
            
            else {
                return null;
            }
        } catch (ControllerException e) {
            throw new ResponseHandlerException(e);
        }
    }
}
