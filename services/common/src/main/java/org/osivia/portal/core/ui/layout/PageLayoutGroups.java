package org.osivia.portal.core.ui.layout;

import java.util.List;

/**
 * Cache for layoutgroups in a page
 * 
 * @author jsste
 */
public class PageLayoutGroups {
    /**
     * Constuctor
     * @param timestamp
     * @param groups
     */
    public PageLayoutGroups(long timestamp, List<LayoutGroupImpl> groups) {
        super();
        this.timestamp = timestamp;
        this.groups = groups;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public List<LayoutGroupImpl> getGroups() {
        return groups;
    }
    final long timestamp;
    final List<LayoutGroupImpl> groups;
}
