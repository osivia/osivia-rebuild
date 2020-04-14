package org.osivia.portal.services.cms.service;

import javax.servlet.http.HttpSession;

import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.services.cms.repository.CMSUserRepository;
import org.springframework.stereotype.Service;

/**
 * CMS service implementation.
 *
 * @author Jean-Sébastien Steux
 * @see CMSService
 */
@Service
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
    public Document getDocument(CMSContext cmsContext, String id) throws CMSException {
        
        ControllerContext ctx = ControllerContextAdapter.getControllerContext(cmsContext.getPortalControllerContext());
        
        HttpSession session = ctx.getServerInvocation().getServerContext().getClientRequest().getSession(true);
        
        CMSUserRepository userRepository = (CMSUserRepository) session.getAttribute(CMSUserRepository.SESSION_ATTRIBUTE_NAME);
        if( userRepository == null) {
            userRepository = new CMSUserRepository();
        }
        return userRepository.getDocument(id);

    }

}
