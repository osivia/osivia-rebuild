package org.osivia.portal.api.blacklist;

import java.util.List;


import org.osivia.portal.api.context.PortalControllerContext;

public interface IBlackListService {

    String MBEAN_NAME = "osivia:service=BlackListService";
    
    public <T> List<T> filterByBlacklist(PortalControllerContext portalCtx, String blackListPrefix, List<T> filteredApps, IBlackListableElement<T> idRetrieve) ;

}
