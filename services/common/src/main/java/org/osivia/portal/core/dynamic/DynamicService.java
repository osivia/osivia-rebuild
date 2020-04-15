package org.osivia.portal.core.dynamic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;
import org.springframework.stereotype.Service;

@Service
public class DynamicService implements IDynamicService {


    @Override
    public void startDynamicWindow(PortalControllerContext portalControllerContext, String regionId, String portletInstance,
            Map<String, String> windowProperties) throws PortalException {

        ControllerContext ctx = ControllerContextAdapter.getControllerContext(portalControllerContext);

        DynamicPortalObjectContainer poc = (DynamicPortalObjectContainer) ctx.getController().getPortalObjectContainer();

        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");

        if (window != null) {

            Map<String, String> properties = new HashMap<String, String>();
            properties.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
            properties.put(ThemeConstants.PORTAL_PROP_REGION, regionId);

            DynamicWindowBean windowBean = new DynamicWindowBean(window.getPage().getId(), "dyna" + System.currentTimeMillis(), "SampleInstance", properties,
                    null);

            poc.addDynamicWindow(windowBean);


        } else
            throw new PortalException("no window in request");
    }

    @Override
    public String startDynamicPage(PortalControllerContext portalControllerContext, String parentPath, String pageName, Map<Locale, String> displayNames,
            String templatePath, Map<String, String> properties, Map<String, String> parameters) {

        ControllerContext ctx = ControllerContextAdapter.getControllerContext(portalControllerContext);

        DynamicPortalObjectContainer poc = (DynamicPortalObjectContainer) ctx.getController().getPortalObjectContainer();

        // Récupération page
        PortalObjectId poid = PortalObjectId.parse(parentPath, PortalObjectPath.CANONICAL_FORMAT);
        PortalObject parent = ctx.getController().getPortalObjectContainer().getObject(poid);


        PortalObjectId potemplateid = PortalObjectId.parse(templatePath, PortalObjectPath.CANONICAL_FORMAT);

        DynamicPageBean pageBean = new DynamicPageBean(parent, pageName, pageName, displayNames, potemplateid, properties);

        poc.addDynamicPage(pageBean);
        
        PortalObjectId pageId = new PortalObjectId(parent.getId().getNamespace(),
                new PortalObjectPath(parent.getId().getPath().toString().concat("/").concat(pageName), PortalObjectPath.CANONICAL_FORMAT));

        return pageId.toString(PortalObjectPath.CANONICAL_FORMAT);

    }


}
