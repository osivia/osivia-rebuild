package fr.toutatice.portail.cms.producers.test;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.NativeRepository;

/**
 * 
 * This API offers advanced repository fonctionnalities
 * 
 * @author jsste
 */
public interface AdvancedRepository extends NativeRepository {
    
    public final static int POSITION_BEGIN = 0;
    public final static int POSITION_END = -1;
    
    

    void addWindow(String id, String name, String portletName, String region, int position, String pageId, Map<String, String> properties) throws CMSException;
    
    boolean supportPreview();
    
    void addDocument(String id, String type, String name, String parentId) throws CMSException;    
    
    void publish(String id) throws CMSException;
    
    void unpublish(String id) throws CMSException;
    
    boolean supportPageEdition();
    
    List<Document> getChildren(String id) throws CMSException;
    
    void setACL(String id, List<String> acls) throws CMSException ;
    
    List<String> getACL(String id) throws CMSException ;
     
    void deleteDocument(String id) throws CMSException;
    
    void renameDocument(String id, String title) throws CMSException;
    
    void moveDocument(String srcId, String beforedestId, boolean endOfList) throws CMSException;
   
    void reloadDatas();
    
    public void addDocument(String internalID, RepositoryDocument document) throws CMSException  ;
    
    public void updateDocument(String internalID, RepositoryDocument document) throws CMSException ;
    
    public void setNewId(String internalID, String newId) throws CMSException ;

    void setProfiles(String id, List<Profile> profiles) throws CMSException;
    
    void setStyles(String id, List<String> styles) throws CMSException;
    
    void setTemplateId( String id, UniversalID templateID) throws CMSException;
}
