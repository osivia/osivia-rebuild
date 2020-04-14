package org.osivia.portal.services.cms.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.services.cms.repository.CMSUserRepository;
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
    
    /** The user repository. */
    private CMSUserRepository userRepository;
    
    

    /**
     * Instantiates a new document impl.
     *
     * @param id the id
     * @param properties the properties
     */
    public DocumentImpl( CMSUserRepository userRepository, String id, String name, String parentId, List<String> childrenId, Map<String, Object> properties) {
        super();
        this.userRepository = userRepository;
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


    public String getParentId() {
         return parentId;
    }


    public List<String> getChildrenId() {
        return childrenId;
    }


    public String getName() {
        return name;
    }
    
    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }


    @Override
    public Document getParent()  throws CMSException{
        return userRepository.getParent(this);
    }

    @Override
    public List<Document> getChildren()  throws CMSException {

        return userRepository.getChildren(this);
    }


}
