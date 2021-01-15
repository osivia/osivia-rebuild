package org.osivia.portal.services.cms.model.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Templateable;
import org.osivia.portal.services.cms.model.share.DocumentImpl;
import org.osivia.portal.services.cms.repository.BaseUserRepository;
import org.osivia.portal.services.cms.repository.spi.UserData;

public class UserDocumentImpl implements Document {
    
    DocumentImpl shareDocument;
    UserData userDatas;

    
    public DocumentImpl getSharedDocument() {
        return shareDocument;
    }

    public UserDocumentImpl(DocumentImpl shareDocument, UserData userDatas) {
        this.shareDocument = shareDocument;
        this.userDatas = userDatas;
    }

    @Override
    public UniversalID getId() {
        
        return shareDocument.getId();
    }

    @Override
    public UniversalID getSpaceId() {
        return shareDocument.getSpaceId();
    }

    @Override
    public String getTitle() {
        return shareDocument.getTitle();
    }

    @Override
    public Map<String, Object> getProperties() {
        return shareDocument.getProperties();
    }

    @Override
    public EcmDocument getNativeItem() {
        return shareDocument.getNativeItem();
    }

    @Override
    public List<String> getSubTypes() {
        if(userDatas != null)
            return userDatas.getSubTypes();
        else 
            return new ArrayList<>();
    }
    
    @Override
    public boolean isModifiable() {
        if(userDatas != null)
            return userDatas.isModifiable();
        else 
            return false;
    }

    
    @Override
    public boolean isManageable() {
        if(userDatas != null)
            return userDatas.isManageable();
        else 
            return false;
    }

    @Override
    public boolean isPreview() {
        return shareDocument.isPreview();
    }

    @Override
    public String getType() {
        return shareDocument.getType();
    }

    @Override
    public boolean isTemplateable() {
        return getSharedDocument() instanceof Templateable;
    }


}
