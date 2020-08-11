package fr.toutatice.portail.cms.producers.api;

import org.osivia.portal.api.cms.exception.CMSException;

public interface ITemplatesMemoryRepository {
    void addEmptyPage(String id, String name, String parentId) throws CMSException;

}
