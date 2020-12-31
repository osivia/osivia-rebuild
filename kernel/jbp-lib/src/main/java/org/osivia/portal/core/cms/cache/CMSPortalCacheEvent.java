package org.osivia.portal.core.cms.cache;

import org.osivia.portal.api.cms.service.CMSEvent;

/**
 * The Class CMSPortalCacheEvent.
 */
public class CMSPortalCacheEvent {
    
    public CMSPortalCacheEvent(CMSEvent event) {
        super();
        this.event = event;
        this.timestamp = System.currentTimeMillis();
    }

    /** The timestamp. */
    private long timestamp;
    
    /** The event. */
    private CMSEvent event;
    
    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    
    /**
     * Gets the event.
     *
     * @return the event
     */
    public CMSEvent getEvent() {
        return event;
    }
    
    /**
     * Sets the event.
     *
     * @param event the new event
     */
    public void setEvent(CMSEvent event) {
        this.event = event;
    }


}
