package org.osivia.portal.services.cms.repository;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.services.cms.model.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.model.SpaceImpl;

public class SharedRepository {
    
    private final String repositoryName;
    
    private final List<RepositoryListener> listeners;
    
    private Map<String, NuxeoMockDocumentImpl> documents;
    
    public SharedRepository(String repositoryName) {
        super();
        this.repositoryName = repositoryName;
        this.listeners = new ArrayList<>();
        this.documents= new Hashtable<String, NuxeoMockDocumentImpl>();

    }


    /**
     * {@inheritDoc}
     */

    public void addListener(RepositoryListener listener) {
        listeners.add(listener);
    }

    
    
    public void addDocument(String internalID, NuxeoMockDocumentImpl document)  {
        documents.put(internalID, document);
    }
    
    public NuxeoMockDocumentImpl getDocument(String internalID)  {
        return documents.get(internalID);
    }

   
    public String getRepositoryName() {
        return repositoryName;
    }
    
    
    public void updatePaths() {
        // Set paths
        for (NuxeoMockDocumentImpl doc : new ArrayList<NuxeoMockDocumentImpl>(documents.values())) {
            try {
                String path = "";
                NuxeoMockDocumentImpl hDoc = doc;

                path = "/" + hDoc.getName() + path;

                while (!(hDoc instanceof SpaceImpl)) {
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
    

    public void notifyChanges() {
        for (RepositoryListener listener : listeners) {
            listener.contentModified();
        }
    }
    
    
}
