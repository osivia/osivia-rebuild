package org.osivia.portal.cms.portlets.edition.page.apps.modify.controller;


public class BreadcrumbItem {
    public BreadcrumbItem( String id, String name) {
        super();
        this.name = name;
        this.id = id;
    }
    private String name;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    private String id;
}
