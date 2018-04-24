package org.osivia.portal.services.common.url.service;

import javax.servlet.ServletRequest;

import org.osivia.portal.api.common.url.model.PortalURL;
import org.osivia.portal.api.common.url.service.PortalURLFactory;
import org.springframework.stereotype.Service;

@Service
public class PortalUrlFactoryImpl implements PortalURLFactory {

    @Override
    public PortalURL getPortalURL(ServletRequest request) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public String foo() {
        return "bar5";
    }

}
