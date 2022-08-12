package org.osivia.portal.cms.portlets.edition.repository.controller;


public class RepositoryBean {
    
    String name;
    boolean streamable;

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
