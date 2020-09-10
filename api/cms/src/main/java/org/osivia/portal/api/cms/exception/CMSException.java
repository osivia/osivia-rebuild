package org.osivia.portal.api.cms.exception;

import org.osivia.portal.api.PortalException;

/**
 * CMS exception.
 *
 * @author Cédric Krommenhoek
 * @see PortalException
 */
public class CMSException extends PortalException {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    
    private Throwable e;

    /**
     * Constructor.
     */
    public CMSException() {
        super();
    }

    
    public CMSException( Throwable e) {
        this.e = e;
        
    }
    
    public Throwable getCause() {
        return e;
        
    }
    
}
