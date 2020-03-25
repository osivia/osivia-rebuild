package org.osivia.portal.services.cms.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.common.url.service.PortalURLFactory;
import org.osivia.portal.services.cms.model.DocumentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CMS service implementation.
 *
 * @author Cédric Krommenhoek
 * @see CMSService
 */
@Service
public class CMSServiceImpl implements CMSService {
    
    public static String SPACE_SEPARATOR = "_";

    @Autowired
    private PortalURLFactory urlFactory;


    /**
     * Constructor.
     */
    public CMSServiceImpl() {
        super();
    }


    /**
     * {@inheritDoc}
     */


    @Override
    public Document getDocument(CMSContext cmsContext, String id) throws CMSException {

        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        if( id.contains(SPACE_SEPARATOR))   {
            properties.put("dc:title", "space" + id); 
            properties.put("osivia.template", "/portalA/pageA"); 
        }
        else
            properties.put("dc:title", "doc." + id);         
        return new DocumentImpl(id, properties);

    }

}
