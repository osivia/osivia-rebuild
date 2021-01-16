package org.osivia.portal.api.cms.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;

public interface TestRepository  {
    public final static int POSITION_BEGIN = 0;
    public final static int POSITION_END = -1;
    
    

    Document getDocument(String internalId) throws CMSException;

    public NavigationItem getNavigationItem(String internalId) throws CMSException;
    
    void publish(String id) throws CMSException;
    
    boolean supportPageEdition();
    
    List<Document> getChildren(String id) throws CMSException;
    
    void setACL(String id, List<String> acls) throws CMSException ;
    
    List<String> getACL(String id) throws CMSException ;
     
    public List<Locale> getLocales() ;
    
    void addEmptyPage(String id, String name, String parentId) throws CMSException;

    void addWindow(String id, String name, String portletName, String region, int position, String pageId, Map<String, String> properties) throws CMSException;
    
    
    void addFolder(String id, String name, String parentId) throws CMSException;
    
    void addDocument(String id, String name, String parentId) throws CMSException;    

}
