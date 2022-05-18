package org.osivia.portal.core.apps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.portlet.info.PortletIconInfo;
import org.jboss.portal.core.portlet.info.PortletInfoInfo;
import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.portlet.state.PropertyMap;
import org.osivia.portal.api.apps.App;
import org.osivia.portal.api.apps.IAppsService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.constants.InternalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(IAppsService.MBEAN_NAME)
public class AppsService implements IAppsService {

    @Autowired
    InstanceContainer instanceContainer;

    
    
    @Override
    public List<App> getApps(PortalControllerContext portalCtx) {
        

        List<App> apps = new ArrayList<>();
        Locale locale = portalCtx.getHttpServletRequest().getLocale();
        for (InstanceDefinition instance : instanceContainer.getDefinitions()) {


            Portlet portlet;
            try {
                PropertyMap properties = instance.getProperties();
                if (properties != null) {
                    List<String> hideProperties = properties.get(InternalConstants.HIDE_PORTLET_IN_MENU_PROPERTY);
                    if ((hideProperties != null) && (hideProperties.contains(String.valueOf(true)))) {
                        // Hide portlet in menu
                        continue;
                    }
                }

                portlet = instance.getPortlet();
  
            } catch (PortletInvokerException e) {
                // Portlet undeployed
                continue;
            }
            
            // Portlet icon
            String iconLocation = null;
            String iconSize = PortletIconInfo.SMALL;
            PortletInfo info = portlet.getInfo();
            PortletInfoInfo infoInfo = info.getAttachment(PortletInfoInfo.class);
            if ((infoInfo != null) && (infoInfo.getPortletIconInfo() != null) && (infoInfo.getPortletIconInfo().getIconLocation(iconSize) != null)) {
                iconLocation = infoInfo.getPortletIconInfo().getIconLocation(iconSize);
            }

            App app = new App(instance.getId(), instance.getDisplayName().getString(locale, true), iconLocation);
            apps.add(app);
            
            
            Collections.sort(apps, new Comparator<App>() {
                @Override
                public int compare(App p1, App p2) {
                    return p1.getDisplayName().compareTo(p2.getDisplayName());
                }
            });
        }
        return apps;
    }

}
