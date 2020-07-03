package org.osivia.portal.services.cms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.services.cms.repository.InMemoryUserRepository;
import org.osivia.portal.services.cms.service.CMSServiceImpl;

/**
 * The Class DocumentImpl.
 */
public class NavigationItemImpl implements NavigationItem {


    private DocumentImpl document;


    /**
     * Instantiates a new document impl.
     *
     * @param id the id
     * @param properties the properties
     */
    public NavigationItemImpl(DocumentImpl document) {
        super();
        this.document = document;
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
        DocumentImpl parent = document.getNavigationParent();
        return new NavigationItemImpl(parent);
    }

    @Override
    public List<NavigationItem> getChildren() throws CMSException {
        List<NavigationItem> children = new ArrayList<>();
        for (DocumentImpl doc : document.getNavigationChildren()) {
            children.add(new NavigationItemImpl(doc));
        }
        return children;
    }

    @Override
    public boolean isRoot() {
        if (document instanceof SpaceImpl)
            return true;
        else
            return false;
    }


}
