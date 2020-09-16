package org.osivia.portal.services.cms.repository.spi;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.RepositoryListener;

public interface UserRepository {
    public void addListener(RepositoryListener listener) ;
    public Document getDocument(String id) throws CMSException;
    public NavigationItem getNavigationItem(String internalId) throws CMSException;
    public boolean supportPreview();
        

}
