package org.osivia.portal.kernel.common.portal;

/**
 * Window definition.
 *
 * @author Cédric Krommenhoek
 */
public class WindowDefinition {

    /** Application name. */
    private final String applicationName;
    /** Portlet name. */
    private final String portletName;
    /** Window identifier. */
    private final String windowId;


    /**
     * Constructor.
     *
     * @param applicationName application name
     * @param portletName portlet name
     * @param windowId window identifier
     */
    public WindowDefinition(String applicationName, String portletName, String windowId) {
        super();
        this.applicationName = applicationName;
        this.portletName = portletName;
        this.windowId = windowId;
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

    /**
     * Getter for windowId.
     * 
     * @return the windowId
     */
    public String getWindowId() {
        return this.windowId;
    }

}
