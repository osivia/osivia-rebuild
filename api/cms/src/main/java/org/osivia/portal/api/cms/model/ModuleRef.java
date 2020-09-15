package org.osivia.portal.api.cms.model;

import java.io.Serializable;
import java.util.Map;

/**
 * The Class ModuleRef.
 */
public class ModuleRef implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4305768690906614507L;

    private final String windowName;

    private final String region;

    private final String moduleId;
    private final Map<String,String> properties;

    


    public ModuleRef(String windowName, String region,String moduleId, Map<String,String> properties) {
        super();
        this.windowName = windowName;
        this.region = region;

        this.moduleId = moduleId;
        this.properties = properties;
    }

    
    public String getWindowName() {
        return windowName;
    }

    
    public String getRegion() {
        return region;
    }



    public String getModuleId() {
        return moduleId;
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }

}
