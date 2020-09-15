package fr.toutatice.portail.cms.producers.sample.inmemory;

import java.util.Map;

import org.osivia.portal.api.cms.exception.CMSException;

public interface IRepositoryUpdate {
    
    public final static int POSITION_BEGIN = 0;
    public final static int POSITION_END = -1;
    
    
    void addEmptyPage(String id, String name, String parentId) throws CMSException;

    void addWindow(String id, String name, String portletName, String region, int position, String pageId, Map<String, String> properties) throws CMSException;
    
    boolean supportPreview();
    
    void addFolder(String id, String name, String parentId) throws CMSException;
    
    void addDocument(String id, String name, String parentId) throws CMSException;    
    
    void publish(String id) throws CMSException;
    
    boolean supportPageEdition();
}
