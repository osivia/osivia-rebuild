package org.osivia.portal.api.cms.model;

import java.util.List;

import org.osivia.portal.api.cms.exception.CMSException;

/**
 * The Interface HierarchicalDocument.
 */
public interface HierarchicalDocument extends Document {
    
    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath();
    
    /**
     * Gets the name.
     *
     * @return the path
     */
    public String getName();

}
