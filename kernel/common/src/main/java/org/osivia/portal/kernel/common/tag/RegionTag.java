package org.osivia.portal.kernel.common.tag;

import java.io.IOException;
import java.util.SortedSet;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.jboss.portal.WindowState;
import org.jboss.portal.portlet.controller.state.PortletWindowNavigationalState;
import org.jboss.portal.portlet.invocation.response.ContentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.osivia.portal.api.cms.model.Template;
import org.osivia.portal.api.common.model.Window;
import org.osivia.portal.kernel.common.model.WindowResult;
import org.osivia.portal.kernel.common.response.IntrospectionResponse;
import org.osivia.portal.kernel.common.response.RenderResponse;

/**
 * Region tag.
 *
 * @author Cédric Krommenhoek
 * @see PortalSimpleTagSupport
 */
public class RegionTag extends PortalSimpleTagSupport {

    /** Region name. */
    private String name;


    /**
     * Constructor.
     */
    public RegionTag() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void doTag(IntrospectionResponse response) throws JspException, IOException {
        Template template = response.getTemplate();
        SortedSet<Window> windows = template.getWindows(this.name);
        for (Window window : windows) {
            response.addWindow(window);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void doTag(RenderResponse response) throws JspException, IOException {
        SortedSet<WindowResult> windowResults = response.getWindowResults(this.name);

        if (windowResults != null) {
            JspWriter out = this.getJspContext().getOut();

            for (WindowResult windowResult : windowResults) {
                Window window = windowResult.getWindow();
                PortletInvocationResponse portletResponse = windowResult.getResponse();

                out.print("<div class='panel panel-info'>");
                out.print("<div class='panel-body'>");

                if (portletResponse != null) {
                    if (portletResponse instanceof ContentResponse) {
                        ContentResponse contentResponse = (ContentResponse) portletResponse;
                        PortletWindowNavigationalState windowNS = null;

                        if (response.getPageNavigationalState() != null) {
                            windowNS = response.getPageNavigationalState().getPortletWindowNavigationalState(window.getId());
                        }

                        if ((windowNS == null || !windowNS.getWindowState().equals(WindowState.MINIMIZED))
                                && (contentResponse.getType() != ContentResponse.TYPE_EMPTY)) {
                            String markup;
                            if (contentResponse.getType() == ContentResponse.TYPE_BYTES) {
                                markup = contentResponse.getBytes().toString();
                            } else {
                                markup = contentResponse.getChars();
                            }
                            out.print(markup);
                        }
                    } else {
                        out.print("<p class='text-danger'>");
                        out.print(portletResponse.getClass().getSimpleName());
                        out.print(" [");
                        out.print(window.getApplicationName());
                        out.print(", ");
                        out.print(window.getPortletName());
                        out.print("]</p>");
                    }
                } else {
                    out.print("<p class='text-danger'>Empty [");
                    out.print(window.getApplicationName());
                    out.print(", ");
                    out.print(window.getPortletName());
                    out.print("]</p>");
                }

                out.print("</div>");
                out.print("</div>");
                out.flush();
            }
        }
    }


    /**
     * Setter for name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
