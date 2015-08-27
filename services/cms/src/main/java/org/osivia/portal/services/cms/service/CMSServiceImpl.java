package org.osivia.portal.services.cms.service;

import javax.servlet.ServletRequest;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSService;

/**
 * CMS service implementation.
 *
 * @author Cédric Krommenhoek
 * @see CMSService
 */
// @Service
public class CMSServiceImpl implements CMSService {

    /**
     * Constructor.
     */
    public CMSServiceImpl() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Document getDocument(ServletRequest servletRequest) throws CMSException {
        // TODO Auto-generated method stub
        return null;
    }

}
