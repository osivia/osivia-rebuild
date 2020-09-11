package org.osivia.portal.services.cms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.services.cms.repository.user.InMemoryUserRepository;

/**
 * The Class DocumentImpl.
 */
public class NuxeoMockDocumentImpl implements org.osivia.portal.api.cms.model.Document, Serializable {
    

    /** The id. */
    private final String internalID;


    /** The properties. */
    private final Map<String, Object> properties;
    

    /** The space id. */
    private final String spaceInternalId;
    
    /** The parent id. */
    private transient  String parentInternalId;
    
 
    /** The id. */
    private transient  List<String> childrenId;
    
    /** The user repository. */
    private transient InMemoryUserRepository userRepository;
    

    /** The native item. */
    private Document nativeItem;
    
    /** The sub types. */
    protected List<String> subTypes;
    
    private transient boolean preview = false;
    



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
        this.preview = userRepository.isPreviewRepository();
    }

     
    
    public boolean isPreview() {
        return preview;
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


    public void setParentInternalId(String parentInternalId) {
        this.parentInternalId = parentInternalId;
    }


    public void setPath(String path) {
        this.nativeItem.setPath( path);
    }


    @Override
    public EcmDocument getNativeItem() {
        return nativeItem;
    }


    public NuxeoMockDocumentImpl duplicate( String parentInternalId,List<String> childrenId,InMemoryUserRepository userRepository) throws CloneNotSupportedException {
        NuxeoMockDocumentImpl newDoc = SerializationUtils.clone(this);
        newDoc.parentInternalId = parentInternalId;
        newDoc.childrenId = childrenId;
        newDoc.userRepository = userRepository;
        return newDoc;
    }


    @Override
    public List<String> getSubTypes() {
        return subTypes;
    } 





}
