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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.command.SignOutCommand;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.IDirProvider;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.directory.v2.service.PersonService;

import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.menubar.IMenubarService;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.tasks.ITasksService;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.theming.IInternalAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.Link;
import org.osivia.portal.api.urls.PortalUrlType;

import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.cms.Satellite;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.content.ViewContentCommand;
import org.osivia.portal.core.menubar.MenubarUtils;
import org.osivia.portal.core.page.PageCustomizerInterceptor;

import org.osivia.portal.core.page.PortalURLImpl;


/**
 * Toolbar attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class ToolbarAttributesBundle implements IInternalAttributesBundle {

    /** Toolbar menubar state items request attribute name. */
    private static final String TOOLBAR_MENUBAR_STATE_ITEMS_ATTRIBUTE = "osivia.toolbar.menubar.stateItems";



    /** Singleton instance. */
    private static ToolbarAttributesBundle instance;


    /** Bundle factory. */
    private final IBundleFactory bundleFactory;
    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;
    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Directory service locator. */
    private final IDirProvider directoryProvider;
    /** Menubar service. */
    private final IMenubarService menubarService;
    /** Tasks service. */
    private final ITasksService tasksService;
    /** Instance container. */
    private final InstanceContainer instanceContainer;

    /** Administration portal identifier. */
    private final PortalObjectId adminPortalId;

    /** Toolbar attributes names. */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private ToolbarAttributesBundle() {
        super();

        // Internationalization service
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
        // URL factory
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
        // Directory service locator
        this.directoryProvider = Locator.findMBean(IDirProvider.class, IDirProvider.MBEAN_NAME);
        // Menubar service
        this.menubarService = MenubarUtils.getMenubarService();
        // Tasks service
        this.tasksService = Locator.findMBean(ITasksService.class, ITasksService.MBEAN_NAME);
        // Instance container
        this.instanceContainer = Locator.findMBean(InstanceContainer.class, "portal:container=Instance");


        this.adminPortalId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);

        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_TOOLBAR_PRINCIPAL);
        this.names.add(Constants.ATTR_TOOLBAR_PERSON);
        this.names.add(Constants.ATTR_TOOLBAR_LOGIN_URL);
        this.names.add(Constants.ATTR_TOOLBAR_MY_SPACE_URL);
        this.names.add(Constants.ATTR_TOOLBAR_REFRESH_PAGE_URL);
        this.names.add(Constants.ATTR_TOOLBAR_SIGN_OUT_URL);
        this.names.add(Constants.ATTR_TOOLBAR_ADMINISTRATION_CONTENT);        
        this.names.add(Constants.ATTR_TOOLBAR_USER_CONTENT);
        this.names.add(Constants.ATTR_TOOLBAR_MY_PROFILE);
        this.names.add(Constants.ATTR_TOOLBAR_USER_SETTINGS_URL);
        this.names.add(Constants.ATTR_TOOLBAR_TASKS_URL);
        this.names.add(Constants.ATTR_TOOLBAR_TASKS_COUNT);
        this.names.add(TOOLBAR_MENUBAR_STATE_ITEMS_ATTRIBUTE);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static ToolbarAttributesBundle getInstance() {
        if (instance == null) {
            instance = new ToolbarAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(ControllerContext controllerContext, Page page, Map<String, Object> attributes) throws ControllerException {
        
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext.getServerInvocation().getServerContext().getClientRequest());
        // Server context
        ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();


        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(controllerContext.getServerInvocation().getRequest().getLocale());

        // Principal
        Principal principal = serverContext.getClientRequest().getUserPrincipal();
        attributes.put(Constants.ATTR_TOOLBAR_PRINCIPAL, principal);

        // Person
        Person person = null;
        PersonService personService = null;
        if (principal != null) {
            personService = this.directoryProvider.getDirService(PersonService.class);
            person = personService.getPerson(principal.getName());
            attributes.put(Constants.ATTR_TOOLBAR_PERSON, person);

            String ownAvatarDisabled = page.getPortal().getProperty("own.avatar.disabled");
            if (StringUtils.isNotBlank(ownAvatarDisabled) && ownAvatarDisabled.equals("true") && person != null) {
                person.setAvatar(null);
            }


            attributes.put(Constants.ATTR_TOOLBAR_PERSON, person);

        }



        // My profile
        String myProfileUrl = null;
        if (person != null) {

            // View profile
            try {

                Link l = personService.getMyCardUrl(portalControllerContext);

                if (l != null) {
                    myProfileUrl = l.getUrl();
                    attributes.put(Constants.ATTR_TOOLBAR_MY_PROFILE, myProfileUrl);
                }

            } catch (PortalException e) {
                // Do nothing
            }
        }


        // User settings
        String userSettingsInstance = "osivia-services-user-settings-instance";
        String userSettingsUrl;
        if ((person == null) || (this.instanceContainer.getDefinition(userSettingsInstance) == null)) {
            userSettingsUrl = null;
        } else {
            // Title
            String title = bundle.getString("USER_SETTINGS");

            // Window properties
            Map<String, String> properties = new HashMap<>();
            properties.put(InternalConstants.PROP_WINDOW_TITLE, title);
            properties.put("osivia.hideTitle", "1");
            properties.put("osivia.ajaxLink", "1");
            properties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, String.valueOf(true));

            try {
                userSettingsUrl = this.urlFactory.getStartPortletInNewPage(portalControllerContext, "user-settings", title, userSettingsInstance, properties,
                        null);
            } catch (PortalException e) {
                userSettingsUrl = null;
            }
        }
        attributes.put(Constants.ATTR_TOOLBAR_USER_SETTINGS_URL, userSettingsUrl);

        // Refresh page
        String refreshPageURL = this.urlFactory.getRefreshPageUrl(portalControllerContext);
        attributes.put(Constants.ATTR_TOOLBAR_REFRESH_PAGE_URL, refreshPageURL);

        // Logout
        SignOutCommand signOutCommand = new SignOutCommand();
        PortalURL signOutPortalUrl = new PortalURLImpl(signOutCommand, controllerContext, false, null);
        attributes.put(Constants.ATTR_TOOLBAR_SIGN_OUT_URL, signOutPortalUrl.toString());

        // Administration content
        String administrationContent = this.formatHTMLAdministration(controllerContext, page);
        attributes.put(Constants.ATTR_TOOLBAR_ADMINISTRATION_CONTENT, administrationContent);
        
        // Userbar content
        String userbarContent = this.formatHTMLUserbar(controllerContext, page, principal, person, serverContext.getPortalContextPath(), myProfileUrl,
                signOutPortalUrl.toString());
        attributes.put(Constants.ATTR_TOOLBAR_USER_CONTENT, userbarContent);

        // Tasks
        if (principal != null) {
            if (this.instanceContainer.getDefinition("osivia-services-tasks-instance") != null) {
                // Tasks URL
                String tasksUrl;
                try {
                    tasksUrl = this.urlFactory.getStartPortletUrl(portalControllerContext, "osivia-services-tasks-instance", null, PortalUrlType.MODAL);
                } catch (PortalException e) {
                    tasksUrl = null;
                }
                attributes.put(Constants.ATTR_TOOLBAR_TASKS_URL, tasksUrl);

                // Tasks count
                int tasksCount;
                try {
                    tasksCount = this.tasksService.getTasksCount(portalControllerContext);
                } catch (PortalException e) {
                    tasksCount = 0;
                }
                attributes.put(Constants.ATTR_TOOLBAR_TASKS_COUNT, tasksCount);
            }
        }

    }


    

    


   
    
  

  

    /**
     * Generate userbar HTML content.
     *
     * @param controllerContext controller context
     * @param page current page
     * @param principal principal
     * @param person directory person
     * @param mySpaceURL my space URL
     * @param signOutURL sign out URL
     * @return HTML data
     * @throws Exception
     */
    private String formatHTMLUserbar(ControllerContext controllerContext, Page page, Principal principal, Person person, String mySpaceURL, String myProfileUrl,
            String signOutURL) {
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(controllerContext.getServerInvocation().getRequest().getLocale());
        // CMS service
        // ICMSService cmsService = cmsServiceLocator.getCMSService();
        // CMS context
        // CMSServiceCtx cmsCtx = new CMSServiceCtx();
        // cmsCtx.setServerInvocation(controllerContext.getServerInvocation());
        // cmsCtx.setControllerContext(controllerContext);


        // User informations
        String userName;
        String userAvatarSrc = null;
        if (person != null) {
            userName = person.getDisplayName();
            if (person.getAvatar() != null) {
                userAvatarSrc = person.getAvatar().getUrl();
            } else {
                userAvatarSrc = null;
            }
        } else if (principal != null) {
            userName = principal.getName();
            // try {
            // userAvatarSrc = cmsService.getUserAvatar(cmsCtx, userName).getUrl();
            // } catch (CMSException e) {
            // userAvatarSrc = null;
            // }
        } else {
            userName = bundle.getString(InternationalizationConstants.KEY_USER_GUEST);
            // try {
            // userAvatarSrc = cmsService.getUserAvatar(cmsCtx, "nobody").getUrl();
            // } catch (CMSException e) {
            // userAvatarSrc = null;
            // }
        }


        // Userbar dropdown element
        Element userbarDropdown = DOM4JUtils.generateElement(HTMLConstants.LI, "nav-item dropdown", null);

        // Userbar menu title
        Element userbarDropdownTitle = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "nav-link dropdown-toggle", null);
        DOM4JUtils.addAttribute(userbarDropdownTitle, HTMLConstants.DATA_TOGGLE, "dropdown");
        if (userAvatarSrc != null) {
            Element avatar = DOM4JUtils.generateElement(HTMLConstants.IMG, "avatar", null);
            DOM4JUtils.addAttribute(avatar, HTMLConstants.SRC, userAvatarSrc);
            userbarDropdownTitle.add(avatar);
        }
        Element displayName = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, userName);
        userbarDropdownTitle.add(displayName);
        userbarDropdown.add(userbarDropdownTitle);

        // Userbar menu node
        Element userbarMenu = DOM4JUtils.generateElement(HTMLConstants.DIV, "dropdown-menu", null);
        userbarDropdown.add(userbarMenu);

        if (principal != null) {
            // My space
            // Element mySpace = DOM4JUtils.generateLinkElement(mySpaceURL, null, null, null,
            // bundle.getString(InternationalizationConstants.KEY_MY_SPACE_), "halflings star",
            // AccessibilityRoles.MENU_ITEM);
            // this.addSubMenuElement(userbarMenuUl, mySpace, null, null);


            if (person != null) {
                // View profile

                Element viewProfile = DOM4JUtils.generateLinkElement(myProfileUrl, null, null, "dropdown-item",
                        bundle.getString(InternationalizationConstants.KEY_MY_PROFILE), "glyphicons glyphicons-basic-user");
                userbarMenu.add(viewProfile);

            }


            // Logout
            Element signOut = DOM4JUtils.generateLinkElement(signOutURL, null, null, "dropdown-item", bundle.getString(InternationalizationConstants.KEY_LOGOUT),
                    "glyphicons glyphicons-basic-log-out");
            userbarMenu.add(signOut);
        } else {
            // Login
            Element login = DOM4JUtils.generateLinkElement(mySpaceURL, null, null, "dropdown-item", bundle.getString(InternationalizationConstants.KEY_LOGIN),
                    "glyphicons glyphicons-basic-log-in");
            userbarMenu.add(login);
        }


        // Write HTML content
        return DOM4JUtils.write(userbarDropdown);
    }


    
    private String formatHTMLAdministration(ControllerContext context, Page page) {

        // Administration root element
        Element administration = DOM4JUtils.generateElement(HTMLConstants.UL, "navbar-nav", StringUtils.EMPTY);

        if (PageCustomizerInterceptor.isAdministrator(context)) {

            // fonctionnal administration
            String customAdminPage = page.getProperty("osivia.portal.admin.page");
            if( customAdminPage != null)    {
                
                String[] tokens = customAdminPage.split("/");
                String pageInternalID = "";
                for(String token: tokens)    {
                    if( pageInternalID.length() > 0)
                        pageInternalID+="_";
                    pageInternalID += token.toUpperCase();
                }
               
                UniversalID myAccountDocumentId = new UniversalID( "idx", pageInternalID);
                String customAdminPageURL = urlFactory.getViewContentUrl(new PortalControllerContext(context.getServerInvocation().getServerContext().getClientRequest()), myAccountDocumentId);
                
                
                Element functionalhome = DOM4JUtils.generateLinkElement(customAdminPageURL, null, null, "nav-link", "", "glyphicons glyphicons-basic-cogwheel");
                administration.add(functionalhome);
            }
            
            // Configuration menu dropdown element
            Element configurationDropdown = DOM4JUtils.generateElement(HTMLConstants.LI, "nav-item dropdown", null);
            administration.add(configurationDropdown);

       }

     
        // Write HTML content
        return DOM4JUtils.write(administration);
    }

    
    
    /**
     * Add dropdown divider.
     * @param menu dropdown menu
     */
    private void addDropdownDivider(Element menu) {
        Element divider = DOM4JUtils.generateDivElement("dropdown-divider");
        menu.add(divider);
    }



    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
