/*
 * (C) Copyright 2020 OSIVIA (http://www.osivia.com)
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
package org.osivia.portal.core.portlets.interceptors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPath.CanonicalFormat;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.ResourceInvocation;
import org.jboss.portal.portlet.invocation.response.FragmentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.UpdateNavigationalStateResponse;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.entity.DirectoryPerson;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.cms.edition.CMSEditionService;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.container.dynamic.DynamicTemplatePage;
import org.osivia.portal.core.container.dynamic.DynamicTemplateWindow;
import org.osivia.portal.core.container.dynamic.DynamicWindow;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PageProperties;
import org.springframework.beans.factory.annotation.Autowired;

import fr.toutatice.portail.cms.producers.test.AdvancedRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;


/**
 * Construction d'un intercepteur de porlet minimal
 *
 * @see PortletInvokerInterceptor
 */
public class ParametresPortletInterceptor extends PortletInvokerInterceptor {

    /**
     * Constructor.
     */
    public ParametresPortletInterceptor() {
        super();
    }


    @Autowired
    private CMSEditionService cmsEditionService;
    


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException {
        // Controller context
        ControllerContext controllerContext = (ControllerContext) invocation.getAttribute("controller_context");

        if (controllerContext != null) {
            Map<String, Object> attributes = invocation.getRequestAttributes();
            if (attributes == null) {
                attributes = new HashMap<String, Object>();
            }

            // Ajout de la window
            String windowId = invocation.getWindowContext().getId();

                PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);

                Window window = (Window) controllerContext.getController().getPortalObjectContainer().getObject(poid);

                 attributes.put("osivia.window", window);
                
                //unique window identifier
                if (window instanceof DynamicWindow) {
                    String uniqueID = ((DynamicWindow) window).getDynamicUniqueID();
                    if ((uniqueID != null) && (uniqueID.length() > 1)) {
                       invocation.setAttribute("osivia.window.path", windowId);
                       invocation.setAttribute("osivia.window.uniqueID", uniqueID);
                    }
                }

                if( window instanceof DynamicTemplateWindow)    {
                    ((DynamicTemplateWindow) window).getURI().equals("EditionInstance");
                    DynamicTemplatePage page = (DynamicTemplatePage) window.getPage();
                    PortalObjectId templateId = page.getTemplateId();
                    attributes.put("osivia.edition.templatePath", templateId.toString(PortalObjectPath.CANONICAL_FORMAT));
                    if( page.getCmsTemplateID() != null)
                        attributes.put("osivia.edition.cmsTemplatePath", page.getCmsTemplateID().toString(PortalObjectPath.CANONICAL_FORMAT));
                }
                

                // Ajout du controleur
                attributes.put("osivia.controller", controllerContext);
                
                // Ajout du mode admin
                if (PageCustomizerInterceptor.isAdministrator(controllerContext)) {
                    attributes.put(InternalConstants.ADMINISTRATOR_INDICATOR_ATTRIBUTE_NAME, true);
                }                


                if (!(invocation instanceof ResourceInvocation)) {

                    List<MenubarItem> menuBar = new ArrayList<MenubarItem>();

                    attributes.put(Constants.PORTLET_ATTR_MENU_BAR, menuBar);
                }
                
                WrappedPortalWindow portalWindow = new WrappedPortalWindow( window);
                attributes.put("osivia.portal.window", portalWindow);
                
                
               // HTTP Request
                HttpServletRequest httpRequest = controllerContext.getServerInvocation().getServerContext().getClientRequest();
                attributes.put(Constants.PORTLET_ATTR_HTTP_REQUEST, httpRequest);

                Boolean refresh =  PageProperties.getProperties().isCheckingSpaceContents() || PageProperties.getProperties().isRefreshingPage(); 
                 
                if( ! refresh) {
                    refresh = (Boolean) controllerContext.getAttribute(Scope.REQUEST_SCOPE,
                            "osivia.refreshWindow." + window.getId().toString(PortalObjectPath.SAFEST_FORMAT));
                    
                }
                
                if( BooleanUtils.isTrue(refresh))
                    attributes.put(Constants.PORTLET_ATTR_PAGE_REFRESH, refresh);
                
                
                Boolean recomputeModels = (Boolean) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE,   Constants.PORTLET_ATTR_RECOMPUTE_MODELS);      
                if( BooleanUtils.isTrue(recomputeModels))
                    attributes.put(Constants.PORTLET_ATTR_RECOMPUTE_MODELS, recomputeModels);
                
                // v2.0 : user datas
                Map<String, Object> userDatas = (Map<String, Object>) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.userDatas");
                if (userDatas != null) {
                    attributes.put(Constants.PORTLET_ATTR_USER_DATAS, userDatas);
                }

                // v3.3 : new user object
                DirectoryPerson person = (DirectoryPerson) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, Constants.ATTR_LOGGED_PERSON);
                if (person != null) {
                    attributes.put(Constants.ATTR_LOGGED_PERSON, person);
                }
                
                
                Person p2 = (Person) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, Constants.ATTR_LOGGED_PERSON_2);
                if (p2 != null) {
                    attributes.put(Constants.ATTR_LOGGED_PERSON_2, p2);
                }


                // Set attributes
                invocation.setRequestAttributes(attributes);
            
        }

        PortletInvocationResponse response = super.invoke(invocation);

        if (response instanceof UpdateNavigationalStateResponse) {
            Map<String, Object> attributes = ((UpdateNavigationalStateResponse) response).getAttributes();
            if (BooleanUtils.toBoolean(String.valueOf(attributes.get("osivia.ajax.preventRefresh")))) {
                controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.ajax.preventRefreshWindowId", invocation.getWindowContext().getId());
            }     
            
            if (Constants.PORTLET_VALUE_ACTIVATE.equals(attributes.get(Constants.PORTLET_ATTR_REFRESH_PAGE))) {
                controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.refreshpage", "true");
            }
            
            if (Constants.PORTLET_VALUE_ACTIVATE.equals(attributes.get(Constants.PORTLET_ATTR_UNSET_MAX_MODE))) {
                controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.unsetMaxMode", "true");
            }
            
            
            
            WrappedPortalWindow  portalWindow = (WrappedPortalWindow) invocation.getRequestAttributes().get("osivia.portal.window");
            if( portalWindow != null )  {
                Map<String, String> localProperties = portalWindow.getLocalProperties();
                if( localProperties.size() > 0) {

                    String windowId = invocation.getWindowContext().getId();
                    PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);                    
                    Window window = (Window) controllerContext.getController().getPortalObjectContainer().getObject(poid);
                    
                    cmsEditionService.updateModule(controllerContext, window, localProperties);
                }
            }
            
            
        }


        return response;
    }

}
