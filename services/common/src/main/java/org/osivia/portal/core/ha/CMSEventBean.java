package org.osivia.portal.core.ha;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.UpdateInformations;
import org.osivia.portal.api.cms.UpdateScope;

/**
 * 
 * JSON serializable bean
 * 
 * @author jsste
 */
public class CMSEventBean { 
    private String repository = null;
     private String documentId = null;
    private String spaceId = null;
    private String scope = null;
    private boolean async;
    
    public CMSEventBean() {
        super();
    }

    public String getDocumentId() {
        return documentId;
    }
    
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    
    public String getSpaceId() {
        return spaceId;
    }
    
    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }
    
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public boolean getAsync() {
        return async;
    }
    
    public void setAsync(boolean async) {
        this.async = async;
    }
   
    
    public void setRepository(String repository) {
        this.repository = repository;
    }
    
    public String getRepository() {
        return repository;
    }


    /**
     * build the bean from update informations
     * 
     * @param update
     * @return
     */
    public static CMSEventBean fromUpdate( UpdateInformations update)  {
        CMSEventBean cmsEvent = new CMSEventBean();
        if( update.getRepository() != null)
            cmsEvent.setRepository(update.getRepository());
        if( update.getSpaceID() != null)
            cmsEvent.setSpaceId( update.getSpaceID().toString());
        if( update.getDocumentID() != null)
            cmsEvent.setDocumentId(update.getDocumentID().toString());        
        if( update.getScope() != null)
            cmsEvent.setScope(update.getScope().toString());        
        cmsEvent.setAsync(update.isAsync());        
        
        return cmsEvent;
    }
    
    
    /**
     * build update informations
     * 
     * @return
     */
    public UpdateInformations toUpdate( )  {
        return new UpdateInformations(getDocumentId()!=null? new UniversalID(getDocumentId()) : null, getSpaceId()!=null? new UniversalID(getSpaceId()) : null, getScope() != null ? UpdateScope.valueOf(getScope()) : null, getAsync(), getRepository());
    
    }
            
    
    
}
