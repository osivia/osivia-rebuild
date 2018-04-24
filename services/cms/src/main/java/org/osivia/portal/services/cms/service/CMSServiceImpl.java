package org.osivia.portal.services.cms.service;

import javax.servlet.ServletRequest;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.common.url.service.PortalURLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CMS service implementation.
 *
 * @author Cédric Krommenhoek
 * @see CMSService
 */
@Service
public class CMSServiceImpl implements CMSService {

    @Autowired
    private PortalURLFactory urlFactory;


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


    @Override
    public String foo() {
        return "toto" + this.urlFactory.foo();
    }

}
