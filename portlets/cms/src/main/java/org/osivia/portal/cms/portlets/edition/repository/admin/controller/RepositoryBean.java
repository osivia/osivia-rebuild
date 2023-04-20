package org.osivia.portal.cms.portlets.edition.repository.admin.controller;


public class RepositoryBean {
    
    String name;
    boolean streamable;
    private String mergeUrl;
    private String version;
    
  
    
    
    public String getVersion() {
        return version;
    }



    
    public void setVersion(String version) {
        this.version = version;
    }



    public String getMergeUrl() {
        return mergeUrl;
    }


    
    public void setMergeUrl(String mergeUrl) {
        this.mergeUrl = mergeUrl;
    }

    

     public boolean isStreamable() {
        return streamable;
    }


    
    public void setStreamable(boolean streamable) {
        this.streamable = streamable;
    }


    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }
    

}
