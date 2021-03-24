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


    public static void setViewState(ControllerContext controllerContext, Integer viewState) {
        controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE,"view_id", viewState);
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
