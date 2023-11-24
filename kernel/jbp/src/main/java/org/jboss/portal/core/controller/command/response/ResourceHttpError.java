package org.jboss.portal.core.controller.command.response;

import org.jboss.portal.core.controller.ControllerResponse;


public class ResourceHttpError extends ControllerResponse {
    private int getHttpCode;

    
    public ResourceHttpError(int getHttpCode) {
        super();
        this.getHttpCode = getHttpCode;
    }


    public int getGetHttpCode() {
        return getHttpCode;
    }

    
    public void setGetHttpCode(int getHttpCode) {
        this.getHttpCode = getHttpCode;
    }

}
