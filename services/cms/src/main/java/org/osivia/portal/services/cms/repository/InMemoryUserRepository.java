package org.osivia.portal.services.cms.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.UniversalID;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.services.cms.model.DocumentImpl;
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


    private Map<String, DocumentImpl> documents;


    private void init() {
        documents = new ConcurrentHashMap<>();
        initDocuments();
    }

    protected abstract void initDocuments();

    public String getRepositoryName() {
        return repositoryName;
    }

    protected void addDocument( String internalID, DocumentImpl document)  {
        documents.put(internalID, document);
    }

    /**
     * Adds the document.
     *
     * @param id the id
     * @param properties the properties
     */
    protected void addDocument(String id, String name, String parentId) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "doc." + name + "#" + id);
        DocumentImpl doc = new DocumentImpl(this, id, name, parentId, new ArrayList<String>(), properties);
        doc.setSpaceInternalId(parentId);
        documents.put(id, doc);
    }


    /**
     * {@inheritDoc}
     */


    public Document getDocument(String internalId) throws CMSException {
        return documents.get(internalId);
    }


    public Document getParent(Document document) throws CMSException {
        DocumentImpl docImpl = (DocumentImpl) document;
        return getDocument(docImpl.getParentInternalId());

    }

    public List<Document> getChildren(Document document) throws CMSException {
        DocumentImpl docImpl = (DocumentImpl) document;
        List<String> childrenId = docImpl.getChildrenId();
        List<Document> children = new ArrayList<>();
        for (String id : childrenId) {
            children.add(getDocument(id));
        }

        return children;
    }


}