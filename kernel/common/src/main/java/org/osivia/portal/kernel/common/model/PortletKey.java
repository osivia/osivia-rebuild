package org.osivia.portal.kernel.common.model;

import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.info.PortletInfo;
import org.osivia.portal.api.common.model.Window;

/**
 * Portlet key.
 *
 * @author Cédric Krommenhoek
 */
public class PortletKey {

    /** Application name. */
    private final String applicationName;
    /** Portlet name. */
    private final String portletName;


    /**
     * Constructor.
     *
     * @param window window
     */
    public PortletKey(Window window) {
        super();
        this.applicationName = window.getApplicationName();
        this.portletName = window.getPortletName();
    }


    /**
     * Constructor.
     *
     * @param portlet portlet
     */
    public PortletKey(Portlet portlet) {
        super();

        PortletInfo info = portlet.getInfo();
        this.applicationName = info.getApplicationName();
        this.portletName = info.getName();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.applicationName == null) ? 0 : this.applicationName.hashCode());
        result = prime * result + ((this.portletName == null) ? 0 : this.portletName.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PortletKey other = (PortletKey) obj;
        if (this.applicationName == null) {
            if (other.applicationName != null) {
                return false;
            }
        } else if (!this.applicationName.equals(other.applicationName)) {
            return false;
        }
        if (this.portletName == null) {
            if (other.portletName != null) {
                return false;
            }
        } else if (!this.portletName.equals(other.portletName)) {
            return false;
        }
        return true;
    }


    /**
     * Getter for applicationName.
     *
     * @return the applicationName
     */
    public String getApplicationName() {
        return this.applicationName;
    }

    /**
     * Getter for portletName.
     *
     * @return the portletName
     */
    public String getPortletName() {
        return this.portletName;
    }

}
