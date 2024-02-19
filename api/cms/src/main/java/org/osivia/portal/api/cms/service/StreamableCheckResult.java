package org.osivia.portal.api.cms.service;


public class StreamableCheckResult {
    String   checkCode;
    boolean  ok;
    String   errorComplement;
    
    public StreamableCheckResult(String checkCode, boolean ok, String errorComplement) {
        super();
        this.checkCode = checkCode;
        this.ok = ok;
        this.errorComplement = errorComplement;
    }

    public String getCheckCode() {
        return checkCode;
    }
    
    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
    
    public boolean isOk() {
        return ok;
    }
    
    public void setOk(boolean ok) {
        this.ok = ok;
    }
    
    public String getErrorComplement() {
        return errorComplement;
    }
    
    public void setErrorComplement(String errorComplement) {
        this.errorComplement = errorComplement;
    }
 
}
