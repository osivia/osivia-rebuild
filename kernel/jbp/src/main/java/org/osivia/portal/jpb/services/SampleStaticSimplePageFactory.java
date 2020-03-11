package org.osivia.portal.jpb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.jpb.services.TemplatePortalObjectContainer.ContainerContext;

public class SampleStaticSimplePageFactory implements CMSPageFactory {


    public static void createCMSPage(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObjectId pageId,
            Map<String, String> pageProperties) {
        new SampleStaticSimplePageFactory(container, containerContext, pageId, pageProperties);
    }

    public SampleStaticSimplePageFactory(TemplatePortalObjectContainer container, ContainerContext containerContext, PortalObjectId pageId,
            Map<String, String> pageProperties) {

        new CMSPage(container, containerContext, pageId, pageProperties, this);

    }


    public void createCMSWindows(CMSPage page, Map windows) {
        page.addWindow(windows, "winsimple" + page.getPageName(), "SampleInstance", "col-2", "0");

    }


    public List<PortalObjectId> getTemplatesID(CMSPage page) {
        List<PortalObjectId> templateIds = new ArrayList<>();
        return templateIds;
    }


}
