package org.osivia.portal.api.cms.service;

import java.util.List;
import java.util.Locale;

/**
 * The Interface NativeRepository.
 */
public interface NativeRepository {
    
    /**
     * Support preview.
     *
     * @return true, if successful
     */
    public boolean supportPreview();
    
    /**
     * Gets the locales.
     *
     * @return the locales
     */
    public List<Locale> getLocales();        

}
