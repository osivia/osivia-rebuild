package fr.toutatice.portail.cms.producers.test;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.NativeRepository;

public interface TestRepository extends NativeRepository {
    
    public final static int POSITION_BEGIN = 0;
    public final static int POSITION_END = -1;
    
    
    void addEmptyPage(String id, String name, String parentId) throws CMSException;

    void addWindow(String id, String name, String portletName, String region, int position, String pageId, Map<String, String> properties) throws CMSException;
    
    boolean supportPreview();
    
    void addFolder(String id, String name, String parentId) throws CMSException;
    
    void addDocument(String id, String name, String parentId) throws CMSException;    
    
    void publish(String id) throws CMSException;
    
    boolean supportPageEdition();
    
    List<Document> getChildren(String id) throws CMSException;
    
    void setACL(String id, List<String> acls) throws CMSException ;
    
    List<String> getACL(String id) throws CMSException ;
     
    public List<Locale> getLocales() ;
}
