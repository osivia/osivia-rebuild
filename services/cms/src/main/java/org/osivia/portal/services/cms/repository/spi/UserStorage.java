package org.osivia.portal.services.cms.repository.spi;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.services.cms.model.share.DocumentImpl;
import org.osivia.portal.services.cms.repository.BaseUserRepository;
import org.osivia.portal.services.cms.repository.cache.SharedRepository;


public interface UserStorage {

    
    void setUserRepository(BaseUserRepository userRepository);
    
    void addDocument(String internalID, DocumentImpl document, boolean batchMode);

    void updateDocument(String internalID, DocumentImpl document, boolean batchMode);

    UserData getUserData(String internalID) throws CMSException;
    
    DocumentImpl getSharedDocument(String internalID) throws CMSException;

    void endBatch();

}