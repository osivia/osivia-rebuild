package org.osivia.portal.kernel.common.tag;

import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.osivia.portal.kernel.common.response.IntrospectionResponse;
import org.osivia.portal.kernel.common.response.PortalResponse;
import org.osivia.portal.kernel.common.response.RenderResponse;

/**
 * Portal body tag support abstract super-class.
 *
 * @author Cédric Krommenhoek
 * @see BodyTagSupport
 */
public abstract class PortalBodyTagSupport extends BodyTagSupport {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;


    /**
     * Constructor.
     */
    public PortalBodyTagSupport() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        ServletResponse response = this.pageContext.getResponse();
        if (response instanceof PortalResponse) {
            PortalResponse portalResponse = (PortalResponse) response;
            return this.doStartTag(portalResponse);
        } else {
            return super.doStartTag();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int doEndTag() throws JspException {
        ServletResponse response = this.pageContext.getResponse();
        if (response instanceof PortalResponse) {
            PortalResponse portalResponse = (PortalResponse) response;
            return this.doEndTag(portalResponse);
        } else {
            return super.doEndTag();
        }
    }


    /**
     * Do start tag.
     *
     * @param response portal response
     * @return evaluation
     * @throws JspException
     */
    private int doStartTag(PortalResponse response) throws JspException {
        if (response instanceof IntrospectionResponse) {
            IntrospectionResponse introspectionResponse = (IntrospectionResponse) response;
            return this.doStartTag(introspectionResponse);
        } else if (response instanceof RenderResponse) {
            RenderResponse renderResponse = (RenderResponse) response;
            return this.doStartTag(renderResponse);
        } else {
            throw new UnsupportedOperationException();
        }
    }


    /**
     * Do end tag.
     *
     * @param response portal response
     * @return evaluation
     * @throws JspException
     */
    private int doEndTag(PortalResponse response) throws JspException {
        if (response instanceof IntrospectionResponse) {
            IntrospectionResponse introspectionResponse = (IntrospectionResponse) response;
            return this.doEndTag(introspectionResponse);
        } else if (response instanceof RenderResponse) {
            RenderResponse renderResponse = (RenderResponse) response;
            return this.doEndTag(renderResponse);
        } else {
            throw new UnsupportedOperationException();
        }
    }


    /**
     * Do start tag (introspection phase).
     *
     * @param response introspection response
     * @return evaluation
     * @throws JspException
     */
    protected abstract int doStartTag(IntrospectionResponse response) throws JspException;


    /**
     * Do start tag (render phase).
     *
     * @param response render response
     * @return evaluation
     * @throws JspException
     */
    protected abstract int doStartTag(RenderResponse response) throws JspException;


    /**
     * Do end tag (introspection phase).
     *
     * @param response introspection response
     * @return evaluation
     * @throws JspException
     */
    protected abstract int doEndTag(IntrospectionResponse response) throws JspException;


    /**
     * Do end tag (render phase).
     *
     * @param response render response
     * @return evaluation
     * @throws JspException
     */
    protected abstract int doEndTag(RenderResponse response) throws JspException;

}
