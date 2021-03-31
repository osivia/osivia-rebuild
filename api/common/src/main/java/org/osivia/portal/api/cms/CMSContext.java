package org.osivia.portal.api.cms;

import java.util.Locale;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * The Class CMSContext.
 */
public class CMSContext {


    /** The portal ctx. */
    private final PortalControllerContext portalCtx;
    
    /** The language. */
    private Locale locale;
    

    /** The preview. */
    private boolean preview;
    
    /** The preview. */
    private boolean superUserMode;


    



    /**
     * Instantiates a new CMS context.
     *
     * @param portalCtx the portal ctx
     */
    public CMSContext(PortalControllerContext portalCtx) {
        super();
        this.portalCtx = portalCtx;
   }
    


    /**
     * Gets the portal controller context.
     *
     * @return the portal controller context
     */
    public PortalControllerContext getPortalControllerContext() {
        return portalCtx;
    }

    
    /**
     * Gets the locale.
     *
     * @return the locale
     */
    public Locale getlocale() {
        return locale;
    }


    
    /**
     * Sets the locale.
     *
     * @param locale the new locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    
    public boolean isSuperUserMode() {
        return superUserMode;
    }


    
    public void setSuperUserMode(boolean superUserMode) {
        this.superUserMode = superUserMode;
    }


    public boolean isPreview() {
        return preview;
    }


    public void setPreview(boolean preview) {
        this.preview = preview;
    }
    
}
