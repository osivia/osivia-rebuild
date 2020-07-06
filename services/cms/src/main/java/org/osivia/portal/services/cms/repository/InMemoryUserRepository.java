package org.osivia.portal.services.cms.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.services.cms.model.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.model.NavigationItemImpl;
import org.osivia.portal.services.cms.model.PageImpl;
import org.osivia.portal.services.cms.model.SpaceImpl;

public abstract class InMemoryUserRepository {

    public static String SESSION_ATTRIBUTE_NAME = "osivia.CMSUserRepository";

    protected String repositoryName;


    public InMemoryUserRepository(String repositoryName) {
        super();
        this.repositoryName = repositoryName;
        init();
    }

    /**
     * {@inheritDoc}
     */


    private Map<String, NuxeoMockDocumentImpl> documents;

   

    private void init() {
        documents = new ConcurrentHashMap<>();
        initDocuments();

        updatePaths();
    }

    protected void updatePaths() {
        // Set paths
        for (NuxeoMockDocumentImpl doc : documents.values()) {
            try {
                String path = "";
                NuxeoMockDocumentImpl hDoc = doc;
                
                path = "/" + hDoc.getName() + path;
 
                while (!(hDoc instanceof SpaceImpl))    {
                        hDoc = getDocument(hDoc.getParentInternalId());
                        path = "/" + hDoc.getName() + path;
                } 
                
                path = "/" + getRepositoryName() + path;
                
                // add path to document
                doc.setPath(path);
                
                // add path entry
                documents.put(path, doc);
                
            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        }
    }

    protected abstract void initDocuments();

    public String getRepositoryName() {
        return repositoryName;
    }

    protected void addDocument(String internalID, NuxeoMockDocumentImpl document) {
        documents.put(internalID, document);
    }


    /**
     * {@inheritDoc}
     */


    public NuxeoMockDocumentImpl getDocument(String internalId) throws CMSException {
        return documents.get(internalId);
    }


    public NavigationItem getNavigationItem(String internalId) throws CMSException {
        NuxeoMockDocumentImpl document = getDocument(internalId);
        return new NavigationItemImpl(document);
    }


    public NavigationItem getContentPrimaryNavigationItem(String internalId) throws CMSException {
        NuxeoMockDocumentImpl document = getDocument(internalId);
        if (!document.isNavigable()) {
            document = document.getNavigationParent();
        }
        return new NavigationItemImpl(document);
    }

    public NuxeoMockDocumentImpl getParent(Document document) throws CMSException {
        NuxeoMockDocumentImpl docImpl = (NuxeoMockDocumentImpl) document;
        return getDocument(docImpl.getParentInternalId());

    }

    public List<NuxeoMockDocumentImpl> getChildren(NuxeoMockDocumentImpl document) throws CMSException {
        NuxeoMockDocumentImpl docImpl = (NuxeoMockDocumentImpl) document;
        List<String> childrenId = docImpl.getChildrenId();
        List<NuxeoMockDocumentImpl> children = new ArrayList<>();
        for (String id : childrenId) {
            children.add(getDocument(id));
        }

        return children;
    }


}
