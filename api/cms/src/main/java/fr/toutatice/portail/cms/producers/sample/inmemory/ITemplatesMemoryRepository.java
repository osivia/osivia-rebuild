package fr.toutatice.portail.cms.producers.sample.inmemory;

import org.osivia.portal.api.cms.exception.CMSException;

public interface ITemplatesMemoryRepository {
    void addEmptyPage(String id, String name, String parentId) throws CMSException;



    void addWindow(String id, String name, String portletName, String region, String pageId) throws CMSException;

}
