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


    public boolean isPreview() {
        return preview;
    }


    public void setPreview(boolean preview) {
        this.preview = preview;
    }


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
     * Gets the language.
     *
     * @return the language
     */
    public Locale getlocale() {
        return locale;
    }


    
    /**
     * Sets the language.
     *
     * @param language the new language
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
