package org.osivia.portal.api.common.url.model;

import java.util.Map;

/**
 * Portal URL.
 *
 * @author Cédric Krommenhoek
 */
public interface PortalURL {

    /**
     * Get context path.
     *
     * @return context path
     */
    String getContextPath();


    /**
     * Get parameters.
     * 
     * @return parameters
     */
    Map<String, String[]> getParameters();

}
