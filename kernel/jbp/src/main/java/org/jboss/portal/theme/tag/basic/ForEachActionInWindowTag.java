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

import org.jboss.portal.theme.render.renderer.ActionRendererContext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class ForEachActionInWindowTag extends BodyTagSupport
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 2573571506276549441L;

   /** The action familly name parameter of the tag. */
   private String family;

   /** Iterator over actions of the family. */
   private Iterator iterator;

   public String getFamily()
   {
      return family;
   }

   public void setFamily(String family)
   {
      this.family = family;
   }

   //

   private boolean next()
   {
      if (iterator.hasNext())
      {
         ActionRendererContext action = (ActionRendererContext)iterator.next();
         pageContext.setAttribute(ForEachActionInWindowTEI.IMPLICIT_NAME, action.getName());
         pageContext.setAttribute(ForEachActionInWindowTEI.IMPLICIT_URL, action.getURL());
         pageContext.setAttribute(ForEachActionInWindowTEI.IMPLICIT_ENABLED, Boolean.valueOf(action.isEnabled()));
         return true;
      }
      else
      {
         return false;
      }
   }

   public int doStartTag() throws JspException
   {
      //
      ForEachWindowInRegionTag other = (ForEachWindowInRegionTag)findAncestorWithClass(this, ForEachWindowInRegionTag.class);
      Collection actions = other.getWindowRenderContext().getDecoration().getTriggerableActions(family);
      if (actions != null)
      {
         iterator = actions.iterator();
      }

      //
      if (next())
      {
         return IterationTag.EVAL_BODY_INCLUDE;
      }
      else
      {
         return IterationTag.SKIP_BODY;
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
      return Tag.EVAL_PAGE;
   }


}
