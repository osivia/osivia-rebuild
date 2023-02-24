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
package org.osivia.portal.core.pagemarker;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.core.container.dynamic.DynamicPortalObjectContainer;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;


/**
 * Utility class for page marker.
 */
public class PageMarkerUtils {

    protected static final Log windowlogger = LogFactory.getLog("PORTAL_WINDOW");

    protected static final Log logger = LogFactory.getLog(PageMarkerUtils.class);


    /**
     * Private constructor ; this class cannot be instanciated.
     */
    private PageMarkerUtils() {
    }


    /**
     * Utility method used to save the page state.
     *
     * @param controllerCtx
     *            controller context
     * @param page
     *            page to save
     */
    @SuppressWarnings("unchecked")
    public static void savePageState(ControllerContext controllerCtx, String viewState) {


        if (viewState != null) {

            PortalObjectId pageId = PortalObjectUtilsInternal.getPageId(controllerCtx);

            Page page = PortalObjectUtilsInternal.getPage(controllerCtx);

            if (page != null) {

                PageMarkerInfo markerInfo = new PageMarkerInfo(viewState);
                
                markerInfo.setLastTimeStamp(System.currentTimeMillis());                
                markerInfo.setPageId(pageId);

                Map<PortalObjectId, WindowStateMarkerInfo> windowInfos = new HashMap<PortalObjectId, WindowStateMarkerInfo>();
                markerInfo.setWindowInfos(windowInfos);

                // Sauvegarde des etats
                Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);

                Iterator<PortalObject> i = windows.iterator();
                while (i.hasNext()) {
                    Window window = (Window) i.next();

                    NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());

                    WindowNavigationalState ws = (WindowNavigationalState) controllerCtx.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);


                    if (ws == null) {
                        ws = new WindowNavigationalState(WindowState.NORMAL, Mode.VIEW, null, null);
                    }


                    windowInfos.put(window.getId(),
                            new WindowStateMarkerInfo(ws.getWindowState(), ws.getMode(), ws.getContentState(), ws.getPublicContentState()));
                }

                
                
                // Sauvegarde etat page
                NavigationalStateContext ctx = (NavigationalStateContext) controllerCtx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
                PageNavigationalState pns = ctx.getPageNavigationalState(page.getId().toString());
                markerInfo.setPageNavigationalState(pns);

                // Sauvegarde des fenêtres dynamiques
                DynamicPortalObjectContainer poc = (DynamicPortalObjectContainer) controllerCtx.getController().getPortalObjectContainer();                
                markerInfo.setDynamicWindows(poc.getDynamicWindows());

                // Sauvegarde des pages dynamiques
                markerInfo.setDynamicPages(poc.getDynamicPages());

                // sauvegarde breadcrumb
                Breadcrumb breadcrumb = (Breadcrumb) controllerCtx.getAttribute(ControllerCommand.REQUEST_SCOPE, "breadcrumb");
                if (breadcrumb != null) {
                    Breadcrumb savedBreadcrum = new Breadcrumb();
                    for (BreadcrumbItem bi : breadcrumb.getChildren()) {
                        savedBreadcrum.getChildren().add(bi);

                    }
                    markerInfo.setBreadcrumb(savedBreadcrum);
                }                
                  
                
                

                // Mémorisation marker dans la session
                Map<String, PageMarkerInfo> markers = (Map<String, PageMarkerInfo>) controllerCtx.getAttribute(Scope.SESSION_SCOPE, "markers");
                if (markers == null) {
                    markers = new LinkedHashMap<String, PageMarkerInfo>();
                    controllerCtx.setAttribute(Scope.SESSION_SCOPE, "markers", markers);
                }

                // On mémorise les 50 dernières entrées
                if (markers.size() > 50) {
                    try {
                        // Tri pour avoir les markers qui n'ont pas été accédés depuis
                        // le + longtemps
                        List<PageMarkerInfo> list = new LinkedList<PageMarkerInfo>(markers.values());

                        Collections.sort(list, new Comparator<PageMarkerInfo>() {

                            public int compare(PageMarkerInfo o1, PageMarkerInfo o2) {
                                return o1.getLastTimeStamp().compareTo(o2.getLastTimeStamp());
                            }
                        });

                        markers.remove(list.get(0).getViewState());
                    } catch (ClassCastException e) {
                        // Déploiement à chaud
                        markers = new LinkedHashMap<String, PageMarkerInfo>();
                        controllerCtx.setAttribute(Scope.SESSION_SCOPE, "markers", markers);
                    }
                }
                markers.put(viewState, markerInfo);
            }
        }
    }


    /**
     * Utility method used to restore the page state.
     *
     * @param controllerContext
     *            controller context
     * @param requestPath
     *            request path
     * @return new path
     */
    @SuppressWarnings("unchecked")
    public static void restorePageState(ControllerContext controllerContext, String viewState) {

    		controllerContext.getServerInvocation().getServerContext().getClientRequest().setAttribute("initial.view.state", viewState);
             
            Map<String, PageMarkerInfo> markers = (Map<String, PageMarkerInfo>) controllerContext.getAttribute(Scope.SESSION_SCOPE, "markers");
            if (markers != null) {
                PageMarkerInfo markerInfo;

                try {

                    markerInfo = markers.get(viewState);

                } catch (ClassCastException e) {
                    markerInfo = null;
                    // Cas d'un redéploiement à chaud
                }


                if (markerInfo != null) {
                    markerInfo.setLastTimeStamp(System.currentTimeMillis());
                    
                    // Restauration des pages dynamiques
                    if (markerInfo.getDynamicPages() != null) {
                        DynamicPortalObjectContainer poc = (DynamicPortalObjectContainer) controllerContext.getController().getPortalObjectContainer(); 
                        poc.setDynamicPages(markerInfo.getDynamicPages());
                    }


                    restorePageState(controllerContext, markerInfo);


                }
            }


            DynamicPortalObjectContainer.clearCache();
     }

    
    
    /**
     * Restore the last pagemarker associated to current Page
     * 
     * (non AJAX mode)
     * @param controllerContext
     * @param requestPath
     */
    public static void restorePageStateByRequest(ControllerContext controllerContext, String requestPath) { 
		// /ac-rennes/DEFAULT__ctx__locale_fr/_dyn_c3BhY2VfREVGQVVMVF9fY3R4X19sb2NhbGVfZnI=.Y29udGVudDphYy1yZW5uZXM6REVGQVVMVF9URU1QTEFURVNfUFVCTElTSA==.b3NpdmlhLmNvbnRlbnRJZCUzRGFjLXJlbm5lcyUzQURFRkFVTFRfX2N0eF9fbG9jYWxlX2ZyJTI2b3NpdmlhLmNvbnRlbnQucHJldmlldyUzRGZhbHNlJTI2b3NpdmlhLnBhZ2VUeXBlJTNEdGVtcGxhdGUlMjZvc2l2aWEuaW5pdGlhbFdpbmRvd1JlZ2lvbiUzRGNvbC0xJTI2b3NpdmlhLmluaXRpYWxXaW5kb3dQcm9wcyUzRG9zaXZpYS5hamF4TGluayUyNTNEMSUyNTI2b3NpdmlhLmhpZGVUaXRsZSUyNTNEMSUyNTI2b3NpdmlhLmhpZGVEZWNvcmF0b3JzJTI1M0QxJTI1MjZ0aGVtZS5keW5hLnBhcnRpYWxfcmVmcmVzaF9lbmFibGVkJTI1M0R0cnVlJTI1MjZvc2l2aWEudGl0bGUlMjUzRE1vbiUyQnByb2ZpbCUyNTI2dWlkRmljaGVQZXJzb25uZSUyNTNEaG93bGFuZC5zaW1wc29uJTI2b3NpdmlhLm5hdmlnYXRpb25JZCUzRGFjLXJlbm5lcyUzQURFRkFVTFRfX2N0eF9fbG9jYWxlX2ZyJTI2b3NpdmlhLmNvbnRlbnQubG9jYWxlJTNEZnIlMjZvc2l2aWEuaW5pdGlhbFdpbmRvd0luc3RhbmNlJTNEdG91dGF0aWNlLWlkZW50aXRlLWZpY2hlcGVyc29ubmUtcG9ydGFpbFBvcnRsZXRJbnN0YW5jZSUyNm9zaXZpYS50ZW1wbGF0ZUlkJTNEYWMtcmVubmVzJTNBREVGQVVMVF9URU1QTEFURVNfUFVCTElTSCUyNm9zaXZpYS5zcGFjZUlkJTNEYWMtcmVubmVzJTNBREVGQVVMVF9fY3R4X19sb2NhbGVfZnI=.X19OX18=.ZnIlM0RkZWZhdWx0/content
		String tokens[] = requestPath.split("/");
		if (tokens.length > 3) {
			String pageName = tokens[3];
			if (pageName.startsWith("_dyn_")) {
				Map<String, PageMarkerInfo> markers = (Map<String, PageMarkerInfo>) controllerContext
						.getAttribute(Scope.SESSION_SCOPE, "markers");
				if (markers != null) {
					// Tri pour avoir les markers les plus récents
					List<PageMarkerInfo> list = new LinkedList<PageMarkerInfo>(markers.values());

					Collections.sort(list, new Comparator<PageMarkerInfo>() {

						public int compare(PageMarkerInfo o1, PageMarkerInfo o2) {
							return - o1.getLastTimeStamp().compareTo(o2.getLastTimeStamp());
						}
					});
					
					for( PageMarkerInfo info: list) {
						if( info.getPageId().toString(PortalObjectPath.CANONICAL_FORMAT).contains(pageName))	{
							//the user can manually refresh, no warn
						    //logger.warn("non ajax request "+ requestPath);
							
							restorePageState(controllerContext, info.getViewState());
							// In non ajax mode JBP will increment view_state in the response
							PageMarkerUtils.setViewState(controllerContext, Integer.parseInt(info.getViewState()) -1);
							return;
						}
					}
				}

			}

    	}
    	
    }

    public static void setViewState(ControllerContext controllerContext, Integer viewState) {
        controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE,"view_id", viewState);
    }
    
    public static Integer getViewState(ControllerContext controllerContext) {
        return (Integer) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE,"view_id");
    }


    private static Page restorePageState(ControllerContext controllerContext, PageMarkerInfo markerInfo) {
        // Restautation etat page
        NavigationalStateContext ctx = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        // Restauration des pages dynamiques
        DynamicPortalObjectContainer poc = (DynamicPortalObjectContainer) controllerContext.getController().getPortalObjectContainer();                

        // Restautation etat page

        PageNavigationalState pns = markerInfo.getPageNavigationalState();
        if (pns != null) {
           //
            ctx.setPageNavigationalState(markerInfo.getPageId().toString(), pns);
         }
        
        
        Page page = null;
        if (markerInfo.getPageId() != null) {
            page = (Page) controllerContext.getController().getPortalObjectContainer().getObject(markerInfo.getPageId());
            
            poc.setDynamicWindows(markerInfo.getDynamicWindows());
            
            // Restauration des etats des windows
            for (PortalObject po : page.getChildren(PortalObject.WINDOW_MASK)) {


                Window child = (Window) po;

                WindowStateMarkerInfo wInfo = markerInfo.getWindowInfos().get(child.getId());

                if (wInfo != null) {


                    // On stocke directement dans le
                    // scope session
                    // pour se rebrancher sur le
                    // traitement standard de jboss
                    // portal

                    WindowNavigationalState newNS = new WindowNavigationalState(wInfo.getWindowState(), wInfo.getMode(), wInfo.getContentState(),
                            wInfo.getPublicContentState());

                    NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, child.getId());

                    controllerContext.setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
                }
            }
            
        }
        

        ctx.applyChanges();

        if (markerInfo.getPageId() != null) {
            PortalObjectUtilsInternal.setPageId(controllerContext, markerInfo.getPageId());
            
            HttpServletRequest request = controllerContext.getServerInvocation().getServerContext().getClientRequest();
            
            // Page initialization
            if( !"footer".equals(request.getHeader("ajax_context")))
            	controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.initialPageId", markerInfo.getPageId());
        }

        
        // restauration breadcrumb
        Breadcrumb savedBreadcrum = markerInfo.getBreadcrumb();
        if (savedBreadcrum != null) {
            Breadcrumb breadcrumb = new Breadcrumb();
            for (BreadcrumbItem bi : savedBreadcrum.getChildren()) {
                breadcrumb.getChildren().add(bi);
            }
            controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "breadcrumb", breadcrumb);
        }
        
        return page;
    }
    
    @SuppressWarnings("unchecked")
    public static Integer generateViewState( ControllerContext controllerContext) {

        String lastPageMarker = (String) controllerContext.getAttribute(Scope.SESSION_SCOPE, "lastPageMarker");

        if (lastPageMarker != null) {
            int test = Integer.parseInt(lastPageMarker);

            Map<String, PageMarkerInfo> markers = (Map<String, PageMarkerInfo>) controllerContext.getAttribute(Scope.SESSION_SCOPE, "markers");

            if (markers != null) {
                do {
                    test++;
                    lastPageMarker = Integer.toString(test);
                } while (markers.get(lastPageMarker) != null);
            } else {
                lastPageMarker = "1";
            }
        } else {
            lastPageMarker = "1";
        }

        controllerContext.setAttribute(Scope.SESSION_SCOPE, "lastPageMarker", lastPageMarker);

        return Integer.parseInt(lastPageMarker);
    }
    
    
    


}
