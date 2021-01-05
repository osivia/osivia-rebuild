package org.osivia.portal.api.cms.service;

import java.util.List;

import org.osivia.portal.api.cms.model.Document;

public class Documents extends Result {
    
    private final List<Document> documents;
    
    public Documents(List<Document> documents) {
        super();
        this.documents = documents;
    }

    /**
     * Gets the documents.
     *
     * @return the documents
     */
    List<Document> getDocuments()   {
        return documents;
    }

}
