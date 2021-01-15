package org.osivia.portal.services.cms.model.user;

import java.util.List;

import org.osivia.portal.services.cms.repository.spi.UserData;

public class UserDatasImpl implements UserData{

    private final List<String> subTypes;
    private final boolean manageable;
    private final boolean modifiable;
    
    

    public UserDatasImpl(List<String> subTypes, boolean manageable, boolean modifiable) {
        super();
        this.subTypes = subTypes;
        this.manageable = manageable;
        this.modifiable = modifiable;
    }

    @Override
    public List<String> getSubTypes() {
        return subTypes;
    }

    @Override
    public boolean isManageable() {
        return manageable;
    }
    @Override
    public boolean isModifiable() {
        return modifiable;
    }

}
