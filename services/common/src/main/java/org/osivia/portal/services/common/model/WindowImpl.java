package org.osivia.portal.services.common.model;

import org.osivia.portal.api.common.model.Window;

/**
 * Window implementation.
 *
 * @author Cédric Krommenhoek
 * @see Window
 */
public class WindowImpl implements Window {

    /** Region name. */
    private String region;
    /** Window order. */
    private int order;

    /** Window identifier. */
    private final String id;
    /** Application name. */
    private final String applicationName;
    /** Portlet name. */
    private final String portletName;


    /**
     * Constructor.
     *
     * @param id window identifier
     * @param applicationName application name
     * @param portletName portlet name
     */
    public WindowImpl(String id, String applicationName, String portletName) {
        super();
        this.id = id;
        this.applicationName = applicationName;
        this.portletName = portletName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Window other) {
        if (other == null) {
            return 1;
        } else if (this.order != other.getOrder()) {
            return this.order - other.getOrder();
        } else {
            return this.id.compareTo(other.getId());
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return this.id;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getApplicationName() {
        return this.applicationName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getPortletName() {
        return this.portletName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getRegion() {
        return this.region;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return this.order;
    }


    /**
     * Setter for region.
     *
     * @param region the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }


    /**
     * Setter for order.
     *
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

}
