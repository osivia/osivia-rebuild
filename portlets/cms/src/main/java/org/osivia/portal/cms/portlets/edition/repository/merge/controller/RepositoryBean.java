package org.osivia.portal.cms.portlets.edition.repository.merge.controller;


public class RepositoryBean {
    
    String name;
    boolean streamable;
    String version;

     
    public String getVersion() {
        return version;
    }



    
    public void setVersion(String version) {
        this.version = version;
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
