package org.osivia.portal.services.cms.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.services.cms.service.CMSServiceImpl;

/**
 * The Class DocumentImpl.
 */
public class DocumentImpl implements Document {
    

    /** The id. */
    private final String id;
    

    /** The name. */
    private final String name;

    /** The properties. */
    private final Map<String, Object> properties;
    

    /** The space id. */
    private String spaceId;
    
    /** The parent id. */
    private String parentId;
    
    /** The id. */
    private List<String> childrenId;
    
    
    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    /**
     * Instantiates a new document impl.
     *
     * @param id the id
     * @param properties the properties
     */
    public DocumentImpl( String id, String name, String parentId, List<String> childrenId, Map<String, Object> properties) {
        super();
        
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.childrenId = childrenId;
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
        return spaceId;
    }

    @Override
    public String getParentId() {
         return parentId;
    }

    @Override
    public List<String> getChildrenId() {
        return childrenId;
    }

    
     
    @Override    
    public String getName() {
        return name;
    }


}
