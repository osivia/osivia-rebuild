package org.osivia.portal.api.cms;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.windows.WindowFactory;

/**
 * The Class CMSController.
 */
public class CMSController {

    /** The portal ctx. */
    private final PortalControllerContext portalCtx;

    /** The locale service. */
    private ILocaleService localeService;
    
    public CMSController(PortalControllerContext portalCtx) {
        super();
        this.portalCtx = portalCtx;
        
        if( portalCtx.getRequest() != null) {
            
            
            String sPreview = WindowFactory.getWindow(portalCtx.getRequest()).getProperty("osivia.content.preview");
            if( sPreview == null)
                sPreview = WindowFactory.getWindow(portalCtx.getRequest()).getPageProperty("osivia.content.preview");                
            String sLocale = WindowFactory.getWindow(portalCtx.getRequest()).getProperty("osivia.content.locale");
            if( sLocale == null)
                sLocale = WindowFactory.getWindow(portalCtx.getRequest()).getPageProperty("osivia.content.locale");
            
            
            
            preview = BooleanUtils.toBoolean(sPreview);
            if( sLocale != null) {
                locale = new Locale(sLocale);
            }
        }
        
    }
  
    
    public void addContentToProperties( Map<String, String> properties, String propertyName, UniversalID id) {
        properties.put(propertyName, id.toString());
        writeContextToProperty(  properties) ;
     }
    
    public void writeContextToProperty( Map<String, String> properties) {
        properties.put("osivia.content.preview", BooleanUtils.toStringTrueFalse( getCMSContext().isPreview()));
        if( getCMSContext().getlocale() != null) {
            properties.put("osivia.content.locale", getCMSContext().getlocale().toString()); 
        }
     }

    /**
     * Gets the locale service.
     *
     * @return the locale service
     */
    public ILocaleService getLocaleService() {
        if (localeService == null) {
            localeService =  Locator.getService(ILocaleService.class);
        }
        return localeService;

       
    }

    /** The preview. */
    private boolean preview;
    
    /** The preview. */
    private Locale locale;

    
    public boolean isPreview() {
        return preview;
    }

    public Locale getLocale()  {
        return locale;
    }
    
    
    public CMSContext getCMSContext()   {
        CMSContext cmsContext = new CMSContext(portalCtx);
        
        cmsContext.setPreview(this.preview);
        
        try {
        cmsContext.setLocale( this.locale);
        } catch( Exception e) {
            throw new RuntimeException(e);
        }
        return cmsContext;
    }
    
}
