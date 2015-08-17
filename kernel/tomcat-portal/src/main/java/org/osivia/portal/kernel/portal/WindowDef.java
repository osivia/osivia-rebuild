package org.osivia.portal.kernel.portal;

import java.util.Set;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;

/**
 * Window definition.
 *
 * @author Cédric Krommenhoek
 */
public class WindowDef {

    /** Portlet name. */
    private final String portletName;
    /** Application name. */
    private final String applicationName;
    /** Window identifier. */
    private final String windowId;
    /** Initial mode. */
    private final Mode initialMode;
    /** Supported modes. */
    private final Set<Mode> supportedModes;
    /** Supported window states. */
    private final Set<WindowState> supportedWindowStates;


    /**
     * Constructor.
     *
     * @param portletName portlet name
     * @param applicationName application name
     * @param windowId window identifier
     * @param initialMode initial mode
     * @param supportedModes supported modes
     * @param supportedWindowStates supported window states
     */
    public WindowDef(String portletName, String applicationName, String windowId, Mode initialMode, Set<Mode> supportedModes,
            Set<WindowState> supportedWindowStates) {
        super();
        this.portletName = portletName;
        this.applicationName = applicationName;
        this.windowId = windowId;
        this.initialMode = initialMode;
        this.supportedModes = supportedModes;
        this.supportedWindowStates = supportedWindowStates;
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
     * Getter for applicationName.
     * 
     * @return the applicationName
     */
    public String getApplicationName() {
        return this.applicationName;
    }

    /**
     * Getter for windowId.
     * 
     * @return the windowId
     */
    public String getWindowId() {
        return this.windowId;
    }

    /**
     * Getter for initialMode.
     * 
     * @return the initialMode
     */
    public Mode getInitialMode() {
        return this.initialMode;
    }

    /**
     * Getter for supportedModes.
     * 
     * @return the supportedModes
     */
    public Set<Mode> getSupportedModes() {
        return this.supportedModes;
    }

    /**
     * Getter for supportedWindowStates.
     * 
     * @return the supportedWindowStates
     */
    public Set<WindowState> getSupportedWindowStates() {
        return this.supportedWindowStates;
    }

}
