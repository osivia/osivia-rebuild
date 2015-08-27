package org.osivia.portal.api.cms.model;

import java.util.List;

/**
 * Document container interface.
 *
 * @author Cédric Krommenhoek
 * @see Document
 */
public interface DocumentContainer extends Document {

    /**
     * Document container children.
     *
     * @return children
     */
    List<Document> getChildren();


    /**
     * Document container filtered children.
     *
     * @param expectedType expected children type
     * @return filtered children
     */
    <T extends Document> List<T> getChildren(Class<T> expectedType);

}
