package org.osivia.portal.services.common.context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osivia.portal.api.common.context.PortalControllerContext;
import org.osivia.portal.api.common.context.PortalControllerContextFactory;
import org.springframework.stereotype.Service;

/**
 * Portal controller context factory implementation.
 *
 * @author Cédric Krommenhoek
 * @see PortalControllerContextFactory
 */
@Service
public class PortalControllerContextFactoryImpl implements PortalControllerContextFactory {

    /**
     * Constructor.
     */
    public PortalControllerContextFactoryImpl() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PortalControllerContext getContext(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ServletContext servletContext) {
        PortalControllerContextImpl portalControllerContext = new PortalControllerContextImpl();

        return portalControllerContext;
    }

}
