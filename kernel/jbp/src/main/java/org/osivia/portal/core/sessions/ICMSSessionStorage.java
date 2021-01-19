package org.osivia.portal.core.sessions;

import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.service.CMSSession;

/**
 * The Interface ICMSSessionStorage.
 */
public interface ICMSSessionStorage {
    
    /**
     * Store session.
     *
     * @param cmsSession the cms session
     */
    void storeSession(CMSSessionRecycle cmsSession);
    
    /**
     * Gets the session.
     *
     * @return the session
     */
    CMSSession getSession( CMSContext cmsContext);



}
