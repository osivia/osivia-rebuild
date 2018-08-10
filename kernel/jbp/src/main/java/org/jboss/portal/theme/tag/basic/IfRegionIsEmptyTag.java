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
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.Region;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class IfRegionIsEmptyTag extends BodyTagSupport
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 7802315131862852954L;
   private String region;

   public String getRegion()
   {
      return region;
   }

   public void setRegion(String region)
   {
      this.region = region;
   }

   public int doStartTag() throws JspException
   {
      HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
      PageResult page = (PageResult)request.getAttribute(LayoutConstants.ATTR_PAGE);
      Region region = page.getRegion2(this.region);
      if (region != null && region.getWindows().iterator().hasNext())
      {
         return Tag.SKIP_BODY;
      }
      else
      {
         return Tag.EVAL_BODY_INCLUDE;
      }

   }
}
