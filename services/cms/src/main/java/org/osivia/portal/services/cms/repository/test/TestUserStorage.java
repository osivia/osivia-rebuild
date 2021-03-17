package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.List;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.exception.DocumentForbiddenException;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.repository.BaseUserStorage;
import org.osivia.portal.api.cms.repository.UserData;

import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.user.UserDatasImpl;

public class TestUserStorage extends BaseUserStorage {


    @Override
    public UserData getUserData(String internalID) throws CMSException {
        try {
            RepositoryDocument doc = getDocuments().get(internalID);
            if (doc == null)
                throw new CMSException();


            BaseUserRepository userRepository = getUserRepository();

            // ACL
            
            boolean aclControl = false;

            if (doc instanceof MemoryRepositoryDocument) {

                MemoryRepositoryDocument mDoc = (MemoryRepositoryDocument) doc;
                List<String> acls = mDoc.getACL();
                if (acls.size() > 0) {

                    if (userRepository.getUserName() != null && acls.contains("group:members"))
                        aclControl = true;
                } else
                    aclControl = true;
            }
            
            if (aclControl == false)
                throw new DocumentForbiddenException();


            List<String> subtypes = new ArrayList<String>(doc.getSupportedSubTypes());

            if (!getUserRepository().isAdministrator())
                subtypes.clear();

            return new UserDatasImpl(subtypes, userRepository.isAdministrator(), userRepository.isAdministrator());
        } catch (Exception e) {
            throw new CMSException(e);
        }

    }

  


}
