package org.osivia.portal.kernel.common.tag;

import java.io.IOException;

import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.osivia.portal.kernel.common.response.IntrospectionResponse;
import org.osivia.portal.kernel.common.response.PortalResponse;
import org.osivia.portal.kernel.common.response.RenderResponse;

/**
 * Portal simple tag support abstract super-class.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public abstract class PortalSimpleTagSupport extends SimpleTagSupport {

    /**
     * Constructor.
     */
    public PortalSimpleTagSupport() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        JspContext jspContext = this.getJspContext();
        if (jspContext instanceof PageContext) {
            PageContext pageContext = (PageContext) jspContext;
            ServletResponse response = pageContext.getResponse();
            if (response instanceof PortalResponse) {
                PortalResponse portalResponse = (PortalResponse) response;
                this.doTag(portalResponse);
            }
        }
    }


    /**
     * Do tag.
     *
     * @param response portal response
     * @throws JspException
     * @throws IOException
     */
    private void doTag(PortalResponse response) throws JspException, IOException {
        if (response instanceof IntrospectionResponse) {
            IntrospectionResponse introspectionResponse = (IntrospectionResponse) response;
            this.doTag(introspectionResponse);
        } else if (response instanceof RenderResponse) {
            RenderResponse renderResponse = (RenderResponse) response;
            this.doTag(renderResponse);
        }
    }


    /**
     * Do tag (introspection phase).
     *
     * @param response introspection response
     * @throws JspException
     * @throws IOException
     */
    protected abstract void doTag(IntrospectionResponse response) throws JspException, IOException;


    /**
     * Do tag (render phase).
     * 
     * @param response render response
     * @throws JspException
     * @throws IOException
     */
    protected abstract void doTag(RenderResponse response) throws JspException, IOException;

}
