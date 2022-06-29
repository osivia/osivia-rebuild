package org.osivia.portal.api.cms.model;


/**
 * User applicative profile
 * 
 * @author jsste
 */
public class Profile {
    

    /** Profile name. */
    private String name;
    /** Profile role. */
    private String role;
    /** Profile URL. */
    private String url;
    /** Profile  virtual user. */
    private String virtualUser;
    

    public Profile() {
        super();
    }
    
    public Profile(String name, String role, String url, String virtualUser) {
        super();
        this.name = name;
        this.role = role;
        this.url = url;
        this.virtualUser = virtualUser;
    }


    public String getName() {
        return name;
    }
    
    public String getRole() {
        return role;
    }
    
    
    public void setName(String name) {
        this.name = name;
    }

    
    public void setRole(String role) {
        this.role = role;
    }

    
    public void setUrl(String url) {
        this.url = url;
    }

    
    public void setVirtualUser(String virtualUser) {
        this.virtualUser = virtualUser;
    }

    public String getUrl() {
        return url;
    }
    
    public String getVirtualUser() {
        return virtualUser;
    }

}
