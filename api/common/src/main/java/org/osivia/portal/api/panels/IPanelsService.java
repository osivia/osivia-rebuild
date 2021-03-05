package org.osivia.portal.api.panels;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Panels service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IPanelsService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=PanelsService";

    /** Displayed panels indicator request attribute name. */
    String REQUEST_ATTRIBUTE = "osivia.panels.display";
    /** Page request attribute name. */
    String PAGE_REQUEST_ATTRIBUTE = "osivia.panels.page";


    /**
     * Open panel.
     *
     * @param portalControllerContext portal controller context
     * @param panel panel
     * @param player panel player
     * @throws PortalException
     */
    void openPanel(PortalControllerContext portalControllerContext, Panel panel, PanelPlayer player) throws PortalException;


    /**
     * Close panel.
     *
     * @param portalControllerContext portal controller context
     * @param panel panel
     * @throws PortalException
     */
    void closePanel(PortalControllerContext portalControllerContext, Panel panel) throws PortalException;


    /**
     * Show previously created panel.
     *
     * @param portalControllerContext portal controller context
     * @param panel panel
     * @throws PortalException
     */
    void showPanel(PortalControllerContext portalControllerContext, Panel panel) throws PortalException;


    /**
     * Hide previously created panel.
     *
     * @param portalControllerContext portal controller context
     * @param panel panel
     * @throws PortalException
     */
    void hidePanel(PortalControllerContext portalControllerContext, Panel panel) throws PortalException;


    /**
     * Check if panel is hidden.
     *
     * @param portalControllerContext portal controller context
     * @param panel panel
     * @return hidden panel indicator
     * @throws PortalException
     */
    Boolean isHidden(PortalControllerContext portalControllerContext, Panel panel) throws PortalException;


    void resetTaskDependentPanels(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Get navigation panel player.
     *
     * @param portalControllerContext portal controller context
     * @return panel player
     * @throws PortalException
     */
    PanelPlayer getNavigationPlayer(PortalControllerContext portalControllerContext, String instance) throws PortalException;

}
