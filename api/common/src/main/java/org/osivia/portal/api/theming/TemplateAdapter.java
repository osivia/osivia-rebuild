package org.osivia.portal.api.theming;

import org.osivia.portal.api.cms.UniversalID;

/**
 * Template adapter.
 *
 * @author Cédric Krommenhoek
 */
public interface TemplateAdapter {

    /**
     * Adapt template.
     * 
     * @return template
     */
    public UniversalID adapt(UniversalID templateId) ;

}
