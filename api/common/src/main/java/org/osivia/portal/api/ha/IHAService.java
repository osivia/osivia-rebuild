package org.osivia.portal.api.ha;

import java.io.Serializable;
import java.util.Map;

/**
 * The Interface IHAService.
 */

public interface IHAService {
    
    /** MBean name. */
    static final String MBEAN_NAME = "osivia:service=HAService";
    
    public boolean isMaster();
    
    /**
     * Inits the portal parameters.
     */
    void initPortalParameters();


    /**
     * Check if portal parameters reloaded.
     *
     * @param savedTS the saved TS
     * @return true, if successful
     */
    boolean checkIfPortalParametersReloaded(long savedTS);
    
    /**
     * Adds a shared map.
     *
     * @param mapID the map ID
     * @param map the map
     */
    void shareMap(String mapId, Map<String,String> object);
    

    /**
     * Removes the object.
     *
     * @param objectId the object id
     */
    void unshareMap(String mapId);
    
    
 
    /**
     * Gets the object.
     *
     * @param objectId the object id
     * @return the object
     */
    ClusterMap getSharedMap(String objectId);
}
