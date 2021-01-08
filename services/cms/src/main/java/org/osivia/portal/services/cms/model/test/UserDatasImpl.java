package org.osivia.portal.services.cms.model.test;

import java.util.List;

import org.osivia.portal.services.cms.repository.spi.UserData;

public class UserDatasImpl implements UserData{

    private List<String> subTypes;
    
    public UserDatasImpl(List<String> subTypes) {
        super();
        this.subTypes = subTypes;
    }

    @Override
    public List<String> getSubTypes() {
        return subTypes;
    }

}
