package org.osivia.portal.cms.portlets.menu.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.Link;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.cms.portlets.menu.model.MenuOptions;
import org.osivia.portal.cms.portlets.menu.model.NavigationDisplayItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;

@Service
public class MenuService implements IMenuService {


    /** CMS service. */
    @Autowired
    private CMSService cmsService;
    
    
    @Autowired
    private IPortalUrlFactory portalUrlFactory;   
    


    /**
     * Get menu options.
     *
     * @param nuxeoController Nuxeo controller
     * @return menu options
     */
    private MenuOptions getMenuOptions(NuxeoController nuxeoController) {
        // Request
        PortletRequest request = nuxeoController.getRequest();


        // Base path
        String basePath = nuxeoController.getBasePath();
        // Current path
        String currentPath = nuxeoController.getItemNavigationPath();

        // Open levels
        int openLevels = 10;


        // Start level
        int startLevel = 0;


        // Max levels
        int maxLevels = 10;


        return new MenuOptions(basePath, currentPath, openLevels, startLevel, maxLevels);
    }


    /**
     * Check if item is selected regarding paths.
     *
     * @param currentPath current path, may be null
     * @param itemPath item path
     * @return true if item is selected
     */
    private boolean isSelected(String currentPath, String itemPath) {
        boolean selected = StringUtils.startsWith(currentPath, itemPath);

        if (selected) {
            String[] splittedCurrentPath = StringUtils.split(currentPath, "/");
            String[] splittedItemPath = StringUtils.split(itemPath, "/");

            for (int i = 0; i < Math.min(splittedCurrentPath.length, splittedItemPath.length); i++) {
                if (!StringUtils.equals(splittedCurrentPath[i], splittedItemPath[i])) {
                    selected = false;
                    break;
                }
            }
        }

        return selected;
    }

    
    private List<NavigationDisplayItem> getNavigationDisplayItemChildren(PortalControllerContext portalControllerContext, NuxeoController nuxeoController, CMSContext cmsContext, MenuOptions options,
            NavigationItem navigationItem, int level) throws PortalException {


        // Navigation display item children
        List<NavigationDisplayItem> navigationDisplayItemChildren;

        if ((!options.isLazy() && (level < options.getMaxLevels()))) {
            List<NavigationItem> navigationItemChildren = navigationItem.getChildren();
            navigationDisplayItemChildren = new ArrayList<NavigationDisplayItem>(navigationItemChildren.size());

            for (NavigationItem navigationItemChild : navigationItemChildren) {

                    NavigationDisplayItem navigationDisplayItemChild = this.getNavigationDisplayItem(portalControllerContext,nuxeoController, cmsContext, options, navigationItemChild,
                            level + 1);

                    if (navigationDisplayItemChild != null) {
                        navigationDisplayItemChildren.add(navigationDisplayItemChild);
                    }

            }
        } else {
            navigationDisplayItemChildren = new ArrayList<>(0);
        }


        return navigationDisplayItemChildren;
    }

    
    
    /**
     * Get navigation display item.
     *
     * @param nuxeoController Nuxeo controller
     * @param cmsContext CMS context
     * @param options menu options
     * @param navigationItem recursive navigation item
     * @param level recursive level
     * @return navigation display item
     * @throws PortalException 
     */
    private NavigationDisplayItem getNavigationDisplayItem(PortalControllerContext portalControllerContext, NuxeoController nuxeoController, CMSContext cmsContext, MenuOptions options,
            NavigationItem navigationItem, int level) throws PortalException {
        // Nuxeo document
        // Nuxeo document
        Document document = (Document) cmsService.getCMSSession(cmsContext).getDocument(navigationItem.getDocumentId());
        org.nuxeo.ecm.automation.client.model.Document nuxeoDocument = (org.nuxeo.ecm.automation.client.model.Document) document.getNativeItem();

        // Nuxeo document link
        Link link = new Link(portalUrlFactory.getViewContentUrl(portalControllerContext, document.getId()), false);

        // Selected item indicator
        boolean selected = false;
        // Current item indicator
        boolean current = false;



        if (this.isSelected(options.getCurrentPath(), nuxeoDocument.getPath())) {
            selected = true;

            if (StringUtils.equals(options.getCurrentPath(),  nuxeoDocument.getPath())) {
                current = true;
            }
        }

        // Primary path selected indicator
        boolean primaryPathSelected = (selected && !current);

        // Navigation display item
        NavigationDisplayItem navigationDisplayItem;

        if ((level + 1) >= options.getStartLevel()) {
            navigationDisplayItem = new NavigationDisplayItem(document, link.getUrl(), selected, current, navigationItem);

            // Add children
            List<NavigationDisplayItem> navigationDisplayChildren = this.getNavigationDisplayItemChildren(portalControllerContext,nuxeoController, cmsContext, options, navigationItem,
                    level);
            navigationDisplayItem.getChildren().addAll(navigationDisplayChildren);
        } else if (selected) {
            navigationDisplayItem = null;

            // Search selected child
            List<NavigationDisplayItem> navigationDisplayChildren = this.getNavigationDisplayItemChildren(portalControllerContext,nuxeoController, cmsContext, options, navigationItem,
                    level);

            for (NavigationDisplayItem displayItemChild : navigationDisplayChildren) {
                if (displayItemChild.isSelected()) {
                    navigationDisplayItem = displayItemChild;
                    break;
                }
            }

            if ((navigationDisplayItem == null) && (level == 0)) {
                navigationDisplayItem = new NavigationDisplayItem(document, link.getUrl(), selected, current,  navigationItem);
            }
        } else {
            navigationDisplayItem = null;
        }


        // Last selected indicator
        if (primaryPathSelected) {
            boolean lastSelected = true;
            for (NavigationDisplayItem item : navigationDisplayItem.getChildren()) {
                if (item.isSelected()) {
                    lastSelected = false;
                    break;
                }
            }
            navigationDisplayItem.setLastSelected(lastSelected);
        }


        return navigationDisplayItem;
    }


    @Override
    public NavigationDisplayItem getDisplayItem(PortalControllerContext portalControllerContext) throws PortletException {


        try {
            NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

            MenuOptions options = getMenuOptions(nuxeoController);



            // Navigation item
            NavigationItem navigationItem;

            UniversalID spaceId = nuxeoController.getSpaceId();
            CMSContext cmsContext = nuxeoController.getCMSContext();            
            navigationItem = cmsService.getCMSSession(cmsContext).getNavigationItem(spaceId);


            // Navigation display item
            NavigationDisplayItem navigationDisplayItem;
            if (navigationItem == null) {
                navigationDisplayItem = null;
            } else {

                navigationDisplayItem = this.getNavigationDisplayItem(portalControllerContext,nuxeoController, cmsContext, options, navigationItem, 0);
            }
            return navigationDisplayItem;

        } catch (Exception e) {
            throw new PortletException(e);
        }
    }

}
