package org.osivia.portal.api.locale;

import java.util.Locale;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * The Interface IPreviewModeService.
 */
public interface ILocaleService {
    

    /**
     * Sets the locale.
     *
     * @param portalCtx the portal ctx
     * @param locale the locale
     * @return true, if successful
     * @throws PortalException the portal exception
     */
    public void setLocale( PortalControllerContext portalCtx, Locale locale) throws PortalException;

    /**
     * Gets the locale.
     *
     * @param portalCtx the portal ctx
     * @return the locale
     * @throws PortalException the portal exception
     */
    public Locale getLocale( PortalControllerContext portalCtx) throws PortalException;
    
}
