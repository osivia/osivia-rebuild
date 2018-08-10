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
package org.jboss.portal.theme.tag.basic;

import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.render.renderer.PageRendererContext;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class ForEachWindowInRegionTag extends BodyTagSupport
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -6290334694176657623L;

   /** . */
   private String region;

   /** . */
   private Iterator iterator;

   /** . */
   private PageRendererContext pageRenderContext;

   /** . */
   private WindowRendererContext windowRenderContext;

   public String getRegion()
   {
      return region;
   }

   public void setRegion(String region)
   {
      this.region = region;
   }

   public WindowRendererContext getWindowRenderContext()
   {
      return windowRenderContext;
   }

   // BodyTag implementation *******************************************************************************************

   private boolean next()
   {
      if (iterator.hasNext())
      {
         // Get the next window context
         windowRenderContext = (WindowRendererContext)iterator.next();

         // Set the implicit content
         pageContext.setAttribute(ForEachWindowInRegionTEI.IMPLICIT_CONTENT, this.windowRenderContext.getPortlet().getMarkup());

         // Set the implicit title
         pageContext.setAttribute(ForEachWindowInRegionTEI.IMPLICIT_TITLE, String.valueOf(windowRenderContext.getDecoration().getTitle()));

         //
         return true;
      }
      else
      {
         return false;
      }
   }

   public int doStartTag() throws JspException
   {
      // Get the aggrehated page result from the request
      pageRenderContext = (PageRendererContext)pageContext.getRequest().getAttribute(LayoutConstants.ATTR_PAGE);

      // Aggregate the regions parameterized in the region attribute
      List windows = new ArrayList(10);
      for (StringTokenizer tokenizer = new StringTokenizer(this.region, ","); tokenizer.hasMoreTokens();)
      {
         String token = tokenizer.nextToken().trim();
         RegionRendererContext region = pageRenderContext.getRegion(token);
         if (region != null)
         {
            for (Iterator i = region.getWindows().iterator(); i.hasNext();)
            {
               WindowRendererContext window = (WindowRendererContext)i.next();
               windows.add(window);
            }
         }
         else
         {
            // log.warn("region not found in result:" + region);
         }
      }

      //
      iterator = windows.iterator();
      if (next())
      {
         return Tag.EVAL_BODY_INCLUDE;
      }
      else
      {
         return Tag.SKIP_BODY;
      }
   }

   public int doAfterBody() throws JspException
   {
      if (next())
      {
         return IterationTag.EVAL_BODY_AGAIN;
      }
      else
      {
         return IterationTag.SKIP_BODY;
      }
   }

   public int doEndTag() throws JspException
   {
      iterator = null;
      pageRenderContext = null;
      windowRenderContext = null;
      return Tag.EVAL_PAGE;
   }
}
