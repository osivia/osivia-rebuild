package org.osivia.portal.api.cms.model;

import java.util.List;


/**
 * Page container interface.
 *
 * @author Cédric Krommenhoek
 * @see DocumentContainer
 */
public interface PageContainer extends DocumentContainer {

    /**
     * Get pages.
     *
     * @return pages
     */
    List<Page> getPages();


    /**
     * Get template.
     *
     * @return template
     */
    Template getTemplate();

}
