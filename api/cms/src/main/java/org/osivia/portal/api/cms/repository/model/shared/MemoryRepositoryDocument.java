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
public class MemoryRepositoryDocument extends RepositoryDocument implements org.osivia.portal.api.cms.model.Document, Serializable {

    private List<String> acls = new ArrayList<>();
    private List<String> computedAcls = new ArrayList<>();




    /**
     * Instantiates a new document impl.
     *
     * @param id the id
     * @param properties the properties
     */
    public MemoryRepositoryDocument(BaseUserRepository userRepository, String internalID, String name, String parentId, String spaceId, List<String> childrenId,
            Map<String, Object> properties) {


        super(userRepository, buildNativeItem(name, properties), internalID, name, parentId, spaceId, childrenId, properties);

    }


    private static NxDocumentMock buildNativeItem(String name, Map<String, Object> properties) {
        NxDocumentMock nativeItem = new NxDocumentMock();
        nativeItem.setName(name);
        nativeItem.setTitle(properties.get("dc:title").toString());
        return nativeItem;
    }

   
   

    public MemoryRepositoryDocument duplicateForPublication(String parentInternalId, List<String> childrenId, BaseUserRepository userRepository)
            throws CloneNotSupportedException {
        MemoryRepositoryDocument newDoc = SerializationUtils.clone(this);
        newDoc.parentInternalId = parentInternalId;
        newDoc.childrenId = childrenId;
        newDoc.userRepository = userRepository;
        return newDoc;
    }

    public MemoryRepositoryDocument duplicate() throws CloneNotSupportedException {
        MemoryRepositoryDocument newDoc = SerializationUtils.clone(this);
        newDoc.parentInternalId = getParentInternalId();
        newDoc.childrenId = getChildrenId();
        newDoc.userRepository = userRepository;
        newDoc.preview = isPreview();
        return newDoc;
    }


    public void setACL(List<String> acls) {
        this.acls = acls;
    }

    public List<String> getACL() {
        return this.acls;
    }


    
    public List<String> getComputedAcls() {
        return computedAcls;
    }


    
    public void setComputedAcls(List<String> computedAcls) {
        this.computedAcls = computedAcls;
    }

}
