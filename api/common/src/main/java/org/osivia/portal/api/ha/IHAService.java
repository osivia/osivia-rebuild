package org.osivia.portal.api.ha;


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
}
