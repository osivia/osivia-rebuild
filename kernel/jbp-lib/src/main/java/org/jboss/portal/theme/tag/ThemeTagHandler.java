/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.theme.tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeElement;
import org.jboss.portal.theme.impl.render.dynamic.DynaConstants;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.ThemeContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Iterator;

/**
 * Tag handler for the theme tag. <p>The theme tag injects theme resource links into the page. Themes are CSS files, and
 * their resources (images). The theme tag can inject only the resources of one theme at a time. The href and src
 * attributes of the link and script tags that are being injected are automatically adjusted to the war context they
 * reside in. A theme is defined in the /WEB-INF/portal-themes.xml</p>
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 12913 $, $LastChangedDate: 2009-02-28 12:02:15 -0500 (Sat, 28 Feb 2009) $
 * @see org.jboss.portal.theme.PortalTheme
 * @see org.jboss.portal.theme.ThemeLink
 * @see org.jboss.portal.theme.ThemeScript
 */
public class ThemeTagHandler
   extends SimpleTagSupport
{

   /** . */
	private static final Log log  = LogFactory.getLog(ThemeTagHandler.class);

   /** . */
   private String themeName;

   /**
    * Render the markup for theme injection into the HEAD tag. <p>This tag expects that the theme name is provided via a
    * http request attribute, keyed by <code>LayoutConstants.ATTR_PAGE</code>. The request attribute is expected to
    * contain a PageResult, which in turn exposes an accessor method for the active theme's name. The theme server
    * (registry of all available themes) also has to be available via a request attribute
    * (ThemeConstants.ATTR_THEMESERVER).</p>
    *
    * @throws JspException
    * @throws IOException
    * @see org.jboss.portal.theme.PortalTheme
    */
   public void doTag() throws JspException, IOException
   {
      JspWriter out = this.getJspContext().getOut();

      // get page and region
      PageContext app = (PageContext)getJspContext();
      HttpServletRequest request = (HttpServletRequest)app.getRequest();

      // Get the theme provided as a render context attribute
      RendererContext rendererContext = (RendererContext)request.getAttribute(LayoutConstants.ATTR_RENDERCONTEXT);
      ThemeContext themeContext = rendererContext.getThemeContext();

      PortalTheme theme = themeContext.getTheme();

      // Hard code here for now

      //JBPORTAL-2285 - move to render with other dyna scripts in DynaRegionRenderer
      /*String jsBase = rendererContext.getProperty(DynaConstants.RESOURCE_BASE_URL);
      if (jsBase != null)
      {
         out.print("<link rel=\"stylesheet\" id=\"dyna_css\" href=\"" + jsBase + "/style.css\" type=\"text/css\"/>\n");
      }
      */
      // If no theme provided we use what may be on the tag
      if (theme == null && themeName != null && themeName.length() > 0)
      {
         theme = themeContext.getTheme(getThemeName());
      }

      //
      if (theme != null)
      {
         for (Iterator i = theme.getElements().iterator(); i.hasNext();)
         {
            ThemeElement el = (ThemeElement)i.next();
            out.println(el.getElement());
         }
      }
   }

   // ----- attribute handlers

   public String getThemeName()
   {
      return themeName;
   }

   public void setThemeName(String name)
   {
      themeName = name;
   }
}
