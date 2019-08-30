package org.osivia.portal.jpb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.jpb.services.TemplatePortalObjectContainer.ContainerContext;

public class SampleStaticPageFactory implements CMSPageFactory {


    public static void createCMSPage(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObjectId pageId,
            Map<String, String> pageProperties) {
        new SampleStaticPageFactory(container, containerContext, pageId, pageProperties);
    }

    public SampleStaticPageFactory(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObjectId pageId,
            Map<String, String> pageProperties) {

        new CMSPage(container, containerContext, pageId, pageProperties, this);

    }


    public void createCMSWindows(CMSPage page, Map windows) {
        page.addWindow(windows, "winA" + page.getPageName(), "SampleInstance", "col-2", "0");
        page.addWindow(windows, "winB" + page.getPageName(), "SampleRemote", "col-2", "1");
        page.addWindow(windows, "winC" + page.getPageName(), "SampleInstance", "col-2", "2");
    }


    public List<PortalObjectId> getTemplatesID(CMSPage page) {
        List<PortalObjectId> templateIds = new ArrayList<>();
        return templateIds;
    }


}
