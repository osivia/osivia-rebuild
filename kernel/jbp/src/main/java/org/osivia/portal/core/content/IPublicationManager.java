package org.osivia.portal.core.content;

import java.util.Map;

import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * The Interface PublicationManager.
 * 
 * Handle publication logic
 */
public interface IPublicationManager {
    
    
    /**
     * Gets the page id.
     *
     * @param portalCtx the portal ctx
     * @param docId the doc id
     * @return the page id
     * @throws ControllerException the controller exception
     */
    PortalObjectId getPageId(PortalControllerContext portalCtx, UniversalID parentID, UniversalID docId, Map<String, String> pageProps, Map<String,String> pageParams, String restorableName)
            throws ControllerException;    
    
    /** The Constant PAGEID_PREFIX. */
    public static final String PAGEID_VALUE_SEPARATOR = "_";
    
    public static final String PAGEID_ITEM_SEPARATOR = "__";
   
    /** The Constant PAGEID_CTX. */
    public static final String PAGEID_CTX = PAGEID_ITEM_SEPARATOR +"ctx";
    
    /** The Constant PAGEID_PREVIEW. */
    public static final String PAGEID_PREVIEW = "preview";
    
    /** The Constant PAGEID_LOCALE. */
    public static final String PAGEID_LOCALE =  "locale";



    
    

}
