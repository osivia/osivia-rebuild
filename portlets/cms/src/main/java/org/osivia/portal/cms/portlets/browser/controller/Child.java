package org.osivia.portal.cms.portlets.browser.controller;


public class Child {
    private String url;
    private String title;
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public Child(String url, String title) {
        super();
        this.url = url;
        this.title = title;
    }

}
