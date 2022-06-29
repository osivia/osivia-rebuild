package org.osivia.portal.core.theming.attributesbundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.page.WindowContext;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.path.PortletPathItem;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.page.PageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("osivia:service=HeaderMetadatasAttributesService")

public class HeaderMetadatasAttributesService implements IHeaderMetadatasAttributesBundle {

    @Autowired
    IPreviewModeService previewModeService;
    @Autowired
    ILocaleService localeService;

    private CMSService cmsService;


    private CMSService getCMSService() {
        if (cmsService == null) {
            cmsService = Locator.getService(CMSService.class);
        }

        return cmsService;
    }

    public String getTitle(PortalControllerContext portalControllerContext, Page page) throws PortalException {

        String title = null;



     

        // Maximized window context
        BreadcrumbItem last = null;

        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        Breadcrumb breadcrumb = (Breadcrumb) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "breadcrumb");
        if ((breadcrumb != null) && CollectionUtils.isNotEmpty(breadcrumb.getChildren())) {
            for (PortalObject window : page.getChildren(PortalObject.WINDOW_MASK)) {

                NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());
                WindowNavigationalState windowNavState = (WindowNavigationalState) controllerContext.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);
                
                if (windowNavState != null && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {
                    last = breadcrumb.getChildren().get(breadcrumb.getChildren().size() - 1);
                }
            }
        }

        if (last != null) {
            List<PortletPathItem> paths = last.getPortletPath();
            if( CollectionUtils.isNotEmpty(paths))  {
                title = paths.get( paths.size() -1).getLabel();
            }
        } 
        
        if( title == null ) {
            String contentId = page.getProperty("osivia.contentId");
            if (contentId != null) {
                UniversalID id = new UniversalID(contentId);

                CMSContext cmsContext = new CMSContext(portalControllerContext);

                cmsContext.setPreview(previewModeService.isPreviewing(portalControllerContext, id));
                cmsContext.setLocale(localeService.getLocale(portalControllerContext));


                
                // first navigation item
                NavigationItem navItem = getCMSService().getCMSSession(cmsContext).getNavigationItem(id);
                
                if( navItem.getDocumentId().equals(id))    {
                    title = navItem.getTitle();
                    
                    if( ! navItem.isRoot()) {
                        navItem = navItem.getParent();
                    }
                    else    {
                        navItem = null;
                    }
                }   else    {
                    Document doc = cmsService.getCMSSession(cmsContext).getDocument(id);       
                    title = (String) doc.getTitle();
                }

            }

        }

        return title;

    }
}
