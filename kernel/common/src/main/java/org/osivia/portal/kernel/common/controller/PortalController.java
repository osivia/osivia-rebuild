package org.osivia.portal.kernel.common.controller;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.easymock.EasyMock;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.PageContainer;
import org.osivia.portal.api.cms.model.Template;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.common.exception.PortalException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;

/**
 * Portal controller.
 *
 * @author Cédric Krommenhoek
 * @see ServletContextAware
 */
@Controller
public class PortalController implements ServletContextAware {

    /** Servlet context. */
    private ServletContext servletContext;

    /** CMS service. */
    private CMSService cmsService;


    /**
     * Constructor.
     *
     * @throws CMSException
     */
    public PortalController() throws CMSException {
        super();

        Template page = EasyMock.createMock("Page", Template.class);
        EasyMock.expect(page.getPageContainer()).andStubReturn(page);
        EasyMock.expect(page.getTemplate()).andStubReturn(page);
        EasyMock.expect(page.getLayout()).andReturn("2-cols").anyTimes();

        this.cmsService = EasyMock.createMock("CMSService", CMSService.class);
        EasyMock.expect(this.cmsService.getDocument(EasyMock.anyObject(ServletRequest.class))).andStubReturn(page);

        EasyMock.replay(page);
        EasyMock.replay(this.cmsService);
    }


    /**
     * Request mapping.
     *
     * @param request servlet request
     * @param response servlet response
     * @return dispatcher path
     * @throws ServletException
     * @throws PortalException
     */
    @RequestMapping
    public String view(ServletRequest request, ServletResponse response) throws ServletException, PortalException {
        Document document = this.cmsService.getDocument(request);
        PageContainer pageContainer = document.getPageContainer();
        Template template = pageContainer.getTemplate();
        return template.getLayout();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
