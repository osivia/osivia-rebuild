package org.osivia.portal.services.cms.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.model.Document;

import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.services.cms.service.CMSServiceImpl;

/**
 * The Class DocumentImpl.
 */
public class DocumentImpl implements Document {
    


    /** The id. */
    private final String id;
    
    /** The properties. */
    private final Map<String, Object> properties;
    

    /**
     * Instantiates a new document impl.
     *
     * @param id the id
     * @param properties the properties
     */
    public DocumentImpl( String id, Map<String, Object> properties) {
        super();
        this.id = id;
        this.properties = properties;
    }

    /* (non-Javadoc)
     * @see org.osivia.portal.api.cms.model.Document#getId()
     */
    @Override
    public String getId() {
        return id;
    }
    
    /* (non-Javadoc)
     * @see org.osivia.portal.api.cms.model.Document#getProperties()
     */
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public String getTitle() {
        return properties.get("dc:title").toString();

    }

    @Override
    public String getSpaceId() {
        int index = getId().indexOf(CMSServiceImpl.SPACE_SEPARATOR);
        if( index != -1)  {
            String spaceId = id.substring(0, index);
            return spaceId;
        }
        return id;
    }

}
