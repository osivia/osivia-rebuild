package org.osivia.portal.api.cms.model;


/**
 * The Class ModuleRef.
 */
public class ModuleRef {

    private final String windowName;

    private final String region;
    private final String order;
    private final String moduleId;

    public ModuleRef(String windowName, String region, String order, String moduleId) {
        super();
        this.windowName = windowName;
        this.region = region;
        this.order = order;
        this.moduleId = moduleId;
    }

    
    public String getWindowName() {
        return windowName;
    }

    
    public String getRegion() {
        return region;
    }

    public String getOrder() {
        return order;
    }

    public String getModuleId() {
        return moduleId;
    }
    
    
}
