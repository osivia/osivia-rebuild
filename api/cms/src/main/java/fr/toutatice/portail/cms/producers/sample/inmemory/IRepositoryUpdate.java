package fr.toutatice.portail.cms.producers.sample.inmemory;

import org.osivia.portal.api.cms.exception.CMSException;

public interface IRepositoryUpdate {
    
    void addEmptyPage(String id, String name, String parentId) throws CMSException;

    void addWindow(String id, String name, String portletName, String region, String pageId) throws CMSException;
    
    boolean supportPreview();
    
    void addFolder(String id, String name, String parentId) throws CMSException;
    
    void addDocument(String id, String name, String parentId) throws CMSException;    
    
    void publish(String id) throws CMSException;
    
    boolean supportPageEdition();
}
