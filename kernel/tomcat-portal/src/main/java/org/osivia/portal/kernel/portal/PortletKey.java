package org.osivia.portal.kernel.portal;

/**
 * Portlet key;
 *
 * @author Cédric Krommenhoek
 */
public final class PortletKey {

    /** Application name. */
    private final String applicationName;
    /** Portlet name. */
    private final String portletName;


    /**
     * Constructor.
     *
     * @param applicationName application name
     * @param portletName portlet name
     */
    public PortletKey(String applicationName, String portletName) {
        super();
        this.applicationName = applicationName;
        this.portletName = portletName;
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

}
