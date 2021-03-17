package org.osivia.portal.api.cms.repository.model.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.repository.UserRepository;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.service.CMSService;


/**
 * The Class DocumentImpl.
 */
public class NavigationItemImpl implements NavigationItem {


    private RepositoryDocument document;
    private BaseUserRepository repository;


    /**
     * Instantiates a new document impl.
     *
     * @param id the id
     * @param properties the properties
     */
    public NavigationItemImpl(BaseUserRepository repository,RepositoryDocument document) {
        super();
        this.document = document;
        this.repository = repository;
    }

    @Override
    public UniversalID getDocumentId() {
        return document.getId();
    }

    @Override
    public String getTitle() {
        return document.getTitle();
    }

    @Override
    public NavigationItem getParent() throws CMSException {
        return new NavigationItemImpl(repository, repository.getNavigationParent(document));
    }

    @Override
    public List<NavigationItem> getChildren() throws CMSException {
        List<NavigationItem> children = new ArrayList<>();
        for (RepositoryDocument doc : repository.getNavigationChildren(document)) {
            children.add(new NavigationItemImpl(repository, doc));
        }
        return children;
    }

    @Override
    public boolean isRoot() {
        if (document instanceof MemoryRepositorySpace)
            return true;
        else
            return false;
    }

    @Override
    public UniversalID getSpaceId() {
        return document.getSpaceId();

    }


}
