package org.osivia.portal.api.common.url.model;

/**
 * Identifier URL.
 *
 * @author Cédric Krommenhoek
 * @see PortalURL
 */
public interface IdentifierURL extends PortalURL {

    /**
     * Get identifier.
     * 
     * @return identifier
     */
    String getId();

}
