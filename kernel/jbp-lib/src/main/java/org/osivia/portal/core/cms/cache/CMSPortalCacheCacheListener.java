package org.osivia.portal.core.cms.cache;

import java.util.LinkedList;
import java.util.Queue;

import org.osivia.portal.api.cms.service.CMSEvent;
import org.osivia.portal.api.cms.service.RepositoryListener;


/**
 * The listener interface for receiving CMSPortalCacheCache events.
 * The class that is interested in processing a CMSPortalCacheCache
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addCMSPortalCacheCacheListener<code> method. When
 * the CMSPortalCacheCache event occurs, that object's appropriate
 * method is invoked.
 *
 * @see CMSPortalCacheCacheEvent
 */
public class CMSPortalCacheCacheListener implements RepositoryListener {
    
    /** The events. */
    Queue<CMSPortalCacheEvent> events;



    /**
     * Instantiates a new CMS portal cache cache listener.
     */
    public CMSPortalCacheCacheListener() {
        super();
        events = new LinkedList<>();
    }

    /* (non-Javadoc)
     * @see org.osivia.portal.api.cms.service.RepositoryListener#contentModified(org.osivia.portal.api.cms.service.CMSEvent)
     */
    @Override
    public void contentModified(CMSEvent event) {
        events.add(new CMSPortalCacheEvent( event));
    }
    

    
    /**
     * Gets the events.
     *
     * @return the events
     */
    public Queue<CMSPortalCacheEvent> getEvents() {
        return events;
    }

}
