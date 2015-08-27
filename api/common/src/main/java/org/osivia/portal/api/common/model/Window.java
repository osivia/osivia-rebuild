package org.osivia.portal.api.common.model;

/**
 * Window interface.
 *
 * @author Cédric Krommenhoek
 * @see Comparable
 */
public interface Window extends Comparable<Window> {

    /**
     * Get window identifier.
     *
     * @return window identifier
     */
    String getId();


    /**
     * Get application name.
     *
     * @return application name
     */
    String getApplicationName();


    /**
     * Get portlet name.
     *
     * @return portlet name
     */
    String getPortletName();


    /**
     * Get region.
     *
     * @return region
     */
    String getRegion();


    /**
     * Get window order.
     *
     * @return window order
     */
    int getOrder();

}
