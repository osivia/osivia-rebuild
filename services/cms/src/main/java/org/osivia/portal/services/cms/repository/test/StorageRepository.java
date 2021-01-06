package org.osivia.portal.services.cms.repository.test;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.services.cms.model.test.DocumentImpl;
import org.osivia.portal.services.cms.repository.cache.SharedRepository;


public interface StorageRepository {

    void setSharedRepository(SharedRepository sharedRepository);
    
    void addDocument(String internalID, DocumentImpl document, boolean batchMode);

    void updateDocument(String internalID, DocumentImpl document, boolean batchMode);

    DocumentImpl getDocument(String internalID) throws CMSException;

    void endBatch();

}