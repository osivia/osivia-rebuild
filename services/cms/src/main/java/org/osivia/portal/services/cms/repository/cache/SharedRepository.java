package org.osivia.portal.services.cms.repository.cache;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.GetChildrenRequest;
import org.osivia.portal.api.cms.service.RepositoryListener;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.services.cms.model.test.NuxeoMockDocumentImpl;
import org.osivia.portal.services.cms.model.test.SpaceImpl;
import org.osivia.portal.services.cms.service.CMSEventImpl;
import org.springframework.util.CollectionUtils;

/**
 * The shared cache
 * 
 */
public class SharedRepository {
    
    /** The repository name. */
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

    
    
    public void addDocument(String internalID, NuxeoMockDocumentImpl document, boolean batchMode)  {
        
        documents.put(internalID, document);
        
        // update parent
        String parentID = document.getParentInternalId();
        if( parentID != null)   {
            NuxeoMockDocumentImpl parent = documents.get(parentID);
            if( parent != null) {
                if( !parent.getChildrenId().contains(internalID))  {
                    parent.getChildrenId().add(internalID);
                }
            }
        }
        
        if(!batchMode)  {
        
            updatePaths();
            
            if(CollectionUtils.isEmpty(document.getSubTypes()))    {
                List<Request> dirtyRequests = new ArrayList<>();
                dirtyRequests.add(new GetChildrenRequest(new UniversalID(repositoryName,document.getParentInternalId())));
                notifyChanges( new CMSEventImpl( document, dirtyRequests));    
            }   else
                notifyChanges( new CMSEventImpl());
            
        }
    }
    
    public void updateDocument(String internalID, NuxeoMockDocumentImpl document, boolean batchMode)  {
        
        documents.put(internalID, document);
        
        if(!batchMode)  {
            updatePaths();
            notifyChanges( new CMSEventImpl());    
        }
    } 
    
    
    public NuxeoMockDocumentImpl getDocument(String internalID) throws CMSException {
        try {
        NuxeoMockDocumentImpl doc = documents.get(internalID);
        if( doc == null)
            throw new CMSException();
        return doc.duplicate();
        } catch(Exception e)    {
            throw new CMSException(e);
        }
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
    

    public void notifyChanges( CMSEvent e) {
        for (RepositoryListener listener : listeners) {
            listener.contentModified(e);
        }
    }
    
    
}
