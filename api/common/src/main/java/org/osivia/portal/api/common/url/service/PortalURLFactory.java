package org.osivia.portal.api.common.url.service;

import javax.servlet.ServletRequest;

import org.osivia.portal.api.common.url.model.PortalURL;

/**
 * Portal URL factory.
 *
 * @author Cédric Krommenhoek
 */
public interface PortalURLFactory {

    /**
     * Get portal URL.
     *
     * @param request servlet request
     * @return portal URL
     */
    PortalURL getPortalURL(ServletRequest request);


    String foo();

}
