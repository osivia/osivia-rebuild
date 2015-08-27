package org.osivia.portal.api.cms.model;

/**
 * CMS document interface.
 *
 * @author Cédric Krommenhoek
 */
public interface Document {

    /**
     * Get document identifier.
     *
     * @return identifier
     */
    String getId();


    /**
     * Get document container.
     *
     * @return document container
     */
    DocumentContainer getContainer();


    /**
     * Get page container.
     *
     * @return page container
     */
    PageContainer getPageContainer();

}
