

package org.osivia.portal.core.renderers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.ActionRendererContext;
import org.jboss.portal.theme.render.renderer.DecorationRenderer;
import org.jboss.portal.theme.render.renderer.DecorationRendererContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.page.PageProperties;



public class DivDecorationRenderer extends AbstractObjectRenderer implements DecorationRenderer {

   
    /**
     * Default constructor.
     */
    public DivDecorationRenderer() {
        super();

    }


    /**
     * {@inheritDoc}
     */
    public void render(RendererContext rendererContext, DecorationRendererContext drc) throws RenderException {
        PrintWriter markup = rendererContext.getWriter();

        PageProperties properties = PageProperties.getProperties();

        this.renderTitle(properties, markup, drc);
    }


    /**
     * Render portlet title.
     *
     * @param properties page properties
     * @param markup markup
     * @param drc decoration renderer context
     */
    private void renderTitle(PageProperties properties, PrintWriter markup, DecorationRendererContext drc) {
        // Current window identifier
        String currentWindowId = properties.getCurrentWindowId();

        // Title value
        String title = properties.getWindowProperty(currentWindowId, "osivia.title");
        if (title == null) {
            title = drc.getTitle();
        }

        
        // Bootstrap panel style indicator
        boolean bootstrapPanelStyle = BooleanUtils.toBoolean(properties.getWindowProperty(currentWindowId, "osivia.bootstrapPanelStyle"));

     // Display title indicator
        boolean displayTitle = ! "1".equals(drc.getProperty("osivia.hideTitle"));
        
        if( displayTitle)   {
  
            // Title container
            Element titleContainer = DOM4JUtils.generateElement(HTMLConstants.H2, null, StringUtils.EMPTY);
            StringBuilder builder = new StringBuilder();
            builder.append("portlet-titlebar-title h5");
    
            
            if (bootstrapPanelStyle) {
                builder.append(" card-title");
            }
    
            DOM4JUtils.addAttribute(titleContainer, HTMLConstants.CLASS, builder.toString());
    
            Element titleText = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, StringEscapeUtils.escapeHtml4(title));
            titleContainer.add(titleText);
    
         
    
            // Write HTML
            HTMLWriter htmlWriter = new HTMLWriter(markup);
            htmlWriter.setEscapeText(false);
            try {
                htmlWriter.write(titleContainer);
            } catch (IOException e) {
                // Do nothing
            }
        }
    }


   
}
