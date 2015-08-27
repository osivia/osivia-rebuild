package org.osivia.portal.kernel.common.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletResponse;

import org.jboss.portal.portlet.controller.state.PortletPageNavigationalState;
import org.osivia.portal.kernel.common.model.WindowResult;

/**
 * Render response.
 *
 * @author Cédric Krommenhoek
 * @see PortalResponse
 */
public class RenderResponse extends PortalResponse {

    /** Portlet page navigational state. */
    private final PortletPageNavigationalState pageNavigationalState;
    /** Regions. */
    private final Map<String, SortedSet<WindowResult>> regions;


    /**
     * Constructor.
     *
     * @param response HTTP servlet response
     * @param pageNavigationalState portlet page navigational state
     * @param windowResults window results
     */
    public RenderResponse(HttpServletResponse response, PortletPageNavigationalState pageNavigationalState, List<WindowResult> windowResults) {
        super(response);
        this.pageNavigationalState = pageNavigationalState;

        this.regions = new HashMap<String, SortedSet<WindowResult>>();
        for (WindowResult windowResult : windowResults) {
            String region = windowResult.getWindow().getRegion();

            SortedSet<WindowResult> regionWindowResults = this.regions.get(region);
            if (regionWindowResults == null) {
                regionWindowResults = new TreeSet<WindowResult>();
                this.regions.put(region, regionWindowResults);
            }
            regionWindowResults.add(windowResult);
        }
    }


    /**
     * Get window results.
     *
     * @param region region name
     */
    public SortedSet<WindowResult> getWindowResults(String region) {
        return this.regions.get(region);
    }


    /**
     * Getter for pageNavigationalState.
     * 
     * @return the pageNavigationalState
     */
    public PortletPageNavigationalState getPageNavigationalState() {
        return this.pageNavigationalState;
    }

}
