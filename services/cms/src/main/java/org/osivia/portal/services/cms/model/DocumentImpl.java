package org.osivia.portal.services.cms.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.model.UniversalID;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.services.cms.repository.InMemoryUserRepository;
import org.osivia.portal.services.cms.service.CMSServiceImpl;

/**
 * The Class DocumentImpl.
 */
public class DocumentImpl implements Document {
    

    /** The id. */
    private final String internalID;


    /** The name. */
    private final String name;

    /** The properties. */
    private final Map<String, Object> properties;
    

    /** The space id. */
    private final String spaceInternalId;
    
    /** The parent id. */
    private final String parentInternalId;
    
    /** The id. */
    private final List<String> childrenId;
    
    /** The user repository. */
    private final InMemoryUserRepository userRepository;
    
    

    /**
     * Instantiates a new document impl.
     *
     * @param id the id
     * @param properties the properties
     */
    public DocumentImpl( InMemoryUserRepository userRepository, String internalID, String name, String parentId, String spaceId, List<String> childrenId, Map<String, Object> properties) {
        super();
        this.userRepository = userRepository;
        this.internalID = internalID;
        this.name = name;
        this.parentInternalId = parentId;
        this.spaceInternalId = spaceId;
        this.childrenId = childrenId;
        this.properties = properties;
    }

     
    public String getInternalID() {
        return internalID;
    }
    
    /* (non-Javadoc)
     * @see org.osivia.portal.api.cms.model.Document#getId()
     */
    @Override
    public UniversalID getId() {
        return new UniversalID(userRepository.getRepositoryName(), getInternalID());
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


    public String getSpaceInternalId() {
        return spaceInternalId;
    }
    
    @Override
    public UniversalID getSpaceId() {
        return new UniversalID(userRepository.getRepositoryName(), getSpaceInternalId());
    }


    public String getParentInternalId() {
         return parentInternalId;
    }


    public List<String> getChildrenId() {
        return childrenId;
    }


    public String getName() {
        return name;
    }


    public DocumentImpl getNavigationParent()  throws CMSException{
        return userRepository.getParent(this);
    }


    public List<DocumentImpl> getNavigationChildren()  throws CMSException {
       return userRepository.getChildren(this);
    }


}
