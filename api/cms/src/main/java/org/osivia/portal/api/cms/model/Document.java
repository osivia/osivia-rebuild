package org.osivia.portal.api.cms.model;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;

/**
 * CMS document interface.
 *
 * @author Jean-Sbastien Steux
 */
public interface Document {

    /**
     * Get document identifier.
     *
     * @return identifier
     */
    UniversalID getId();
    
    
    /**
     * Gets the space id.
     *
     * @return the space id
     */
    public UniversalID getSpaceId();
    
    
    /**
     * Gets the title.
     *
     * @return the title
     */
    String getTitle();
    
    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public Map<String,Object> getProperties();
    

    /**
     * Gets the native item.
     *
     * @return the native item
     */
    public EcmDocument getNativeItem();
    
     
    
    
    /**
     * Checks if is preview.
     *
     * @return true, if is preview
     */
    public boolean isPreview();
    
    
    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType();
    
    
    /**
     * Checks if is templateable.
     *
     * @return true, if is templateable
     */
    public boolean isTemplateable();
  
     
    /**
     * Gets the locale.
     *
     * @return the locale
     */
    public Locale getLocale();
}
