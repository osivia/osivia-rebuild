package org.osivia.portal.api.cms.repository.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.nuxeo.ecm.automation.client.model.NxDocumentMock;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Templateable;
import org.osivia.portal.api.cms.repository.BaseUserRepository;

/**
 * The Class DocumentImpl.
 */
public class RepositoryDocument implements org.osivia.portal.api.cms.model.Document, Serializable {
    

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
    private transient BaseUserRepository userRepository;
    

    /** The native item. */
    private NxDocumentMock nativeItem;
    
    private Locale locale;
    
    


    private transient boolean preview = false;
    
    private List<String> acls = new ArrayList<>();
    
    public List<String> supportedSubTypes = new ArrayList<>();    
    
    public  String getType()    {
        return "document";
    }

    public List<String> getSupportedSubTypes()  {
        return supportedSubTypes;
    }

    /**
     * Instantiates a new document impl.
     *
     * @param id the id
     * @param properties the properties
     */
    public RepositoryDocument( BaseUserRepository userRepository,  String internalID, String name, String parentId, String spaceId, List<String> childrenId, Map<String, Object> properties) {
        super();
        this.userRepository = userRepository;
        this.internalID = internalID;
        this.nativeItem = new NxDocumentMock();
        this.nativeItem.setName(name);
        this.nativeItem.setTitle(properties.get("dc:title").toString());
        this.parentInternalId = parentId;
        this.spaceInternalId = spaceId;
        this.childrenId = childrenId;
        this.properties = properties;
        this.preview = userRepository.isPreviewRepository();
        this.locale = userRepository.getRepositoryKey().getLocale();

    }

    public Locale getLocale() {
        return locale;
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

    public UniversalID getId() {
        return new UniversalID(userRepository.getRepositoryName(), getInternalID());
    }
    
    /* (non-Javadoc)
     * @see org.osivia.portal.api.cms.model.Document#getProperties()
     */

    public Map<String, Object> getProperties() {
        return properties;
    }


    public String getTitle() {
        return nativeItem.getTitle();

    }

    public void setTitle( String title)  {
        this.nativeItem.setTitle(title);
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

  
    
    public boolean isNavigable()    {
        return false;
    }


    public void setParentInternalId(String parentInternalId) {
        this.parentInternalId = parentInternalId;
    }


    public void setPath(String path) {
        this.nativeItem.setPath( path);
    }



    public EcmDocument getNativeItem() {
        return nativeItem;
    }


    public RepositoryDocument duplicateForPublication( String parentInternalId,List<String> childrenId,BaseUserRepository userRepository) throws CloneNotSupportedException {
        RepositoryDocument newDoc = SerializationUtils.clone(this);
        newDoc.parentInternalId = parentInternalId;
        newDoc.childrenId = childrenId;
        newDoc.userRepository = userRepository;
        return newDoc;
    }

    public RepositoryDocument duplicate( ) throws CloneNotSupportedException {
        RepositoryDocument newDoc = SerializationUtils.clone(this);
        newDoc.parentInternalId = getParentInternalId();
        newDoc.childrenId = getChildrenId();
        newDoc.userRepository = userRepository;
        newDoc.preview = isPreview();
        return newDoc;
    }



    public void setACL( List<String> acls)   {
        this.acls = acls;
    }

    public List<String> getACL( )   {
       return this.acls;
    }
    

    @Override
    public boolean isTemplateable() {
        return this instanceof Templateable;
    }


}
