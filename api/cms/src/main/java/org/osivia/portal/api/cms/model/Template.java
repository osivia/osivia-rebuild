package org.osivia.portal.api.cms.model;

import java.util.List;
import java.util.SortedSet;

import org.osivia.portal.api.common.model.Window;

/**
 * Template interface.
 *
 * @author Cédric Krommenhoek
 * @see Page
 */
public interface Template extends Page {

    /**
     * Get page layout.
     *
     * @return page layout
     */
    String getLayout();


    /**
     * Get windows.
     *
     * @return windows
     */
    List<Window> getWindows();


    /**
     * Get region windows.
     *
     * @param region region name
     * @return windows
     */
    SortedSet<Window> getWindows(String region);

}
