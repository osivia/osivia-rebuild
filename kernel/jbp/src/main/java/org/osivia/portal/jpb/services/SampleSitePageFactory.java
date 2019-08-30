package org.osivia.portal.jpb.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.jpb.services.TemplatePortalObjectContainer.ContainerContext;

public class SampleSitePageFactory implements CMSPageFactory {



    public static void createCMSPage(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObjectId pageId,             Map<String, String> pageProperties) {
        new SampleSitePageFactory(container, containerContext, pageId, pageProperties);
    }

    public SampleSitePageFactory(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObjectId pageId,             Map<String, String> pageProperties) {

        new CMSPage(container, containerContext, pageId, pageProperties, this);

    }


    @Override
    public void createCMSWindows(CMSPage page, Map windows) {

        page.addWindow(windows, "win-" + page.getPageName(), "SampleInstance", page.getPageName(), "0");

    }

    @Override
    public List<PortalObjectId> getTemplatesID(CMSPage page) {
        List<PortalObjectId> templateIds = new ArrayList<>();

        PortalObjectPath mainTemplatePath = new PortalObjectPath("/portalA/pageA", PortalObjectPath.CANONICAL_FORMAT);
        templateIds.add(new PortalObjectId("", mainTemplatePath));



        return templateIds;
    }


}
