package org.jboss.portal.theme.impl.render.dynamic.response;


/**
 * The resource produced by the portlet
 */
public class PageResource {

    private String tag;
    

    private String rel ;
    private String type ;
    private String href ;
    private String media ;
    private String src ;
    
    
    public String getTag() {
        return tag;
    }

    
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRel() {
        return rel;
    }
    
    public void setRel(String rel) {
        this.rel = rel;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getHref() {
        return href;
    }
    
    public void setHref(String href) {
        this.href = href;
    }
    
    public String getMedia() {
        return media;
    }
    
    public void setMedia(String media) {
        this.media = media;
    }
    
    public String getSrc() {
        return src;
    }
    
    public void setSrc(String src) {
        this.src = src;
    }

}
