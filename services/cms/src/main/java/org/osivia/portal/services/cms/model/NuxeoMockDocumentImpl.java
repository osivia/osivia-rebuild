package org.osivia.portal.services.cms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.services.cms.repository.user.InMemoryUserRepository;

/**
 * The Class DocumentImpl.
 */
public class NuxeoMockDocumentImpl implements org.osivia.portal.api.cms.model.Document {
    

    /** The id. */
    private final String internalID;


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
    

    private Document nativeItem;
    

    /**
     * Instantiates a new document impl.
     *
     * @param id the id
     * @param properties the properties
     */
    public NuxeoMockDocumentImpl( InMemoryUserRepository userRepository,  String internalID, String name, String parentId, String spaceId, List<String> childrenId, Map<String, Object> properties) {
        super();
        this.userRepository = userRepository;
        this.internalID = internalID;
        this.nativeItem = new Document();
        this.nativeItem.setName(name);
        this.nativeItem.setTitle(properties.get("dc:title").toString());
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
        return nativeItem.getTitle();

    }


    public String getSpaceInternalId() {
        return spaceInternalId;
    }
    

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
        return this.nativeItem.getName();
    }


    public NuxeoMockDocumentImpl getNavigationParent()  throws CMSException{
        return userRepository.getParent(this);
    }


    public List<NuxeoMockDocumentImpl> getNavigationChildren()  throws CMSException {
       return userRepository.getChildren(this);
    }
    
    public boolean isNavigable()    {
        return false;
    }


    

    public void setPath(String path) {
        this.nativeItem.setPath( path);
    }


    @Override
    public EcmDocument getNativeItem() {
        return nativeItem;
    }


}
