package org.osivia.portal.api.cms.repository;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.repository.cache.SharedRepository;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;


/**
 * The storage SPI
 */
public interface UserStorage {

    
    void setUserRepository(BaseUserRepository userRepository);
    
    void addDocument(String internalID, RepositoryDocument document, boolean batchMode);

    void updateDocument(String internalID, RepositoryDocument document, boolean batchMode);

    UserData getUserData(String internalID) throws CMSException;
    
    RepositoryDocument reloadDocument(String internalID) throws CMSException;

    void endBatch();

}