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

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class ForEachActionInWindowTEI extends TagExtraInfo
{

   public static final String IMPLICIT_NAME = "name";
   public static final String IMPLICIT_URL = "url";
   public static final String IMPLICIT_ENABLED = "enabled";

   private static final VariableInfo INFO_1 = new VariableInfo(IMPLICIT_NAME, String.class.getName(), true, VariableInfo.NESTED);
   private static final VariableInfo INFO_2 = new VariableInfo(IMPLICIT_URL, String.class.getName(), true, VariableInfo.NESTED);
   private static final VariableInfo INFO_3 = new VariableInfo(IMPLICIT_ENABLED, Boolean.class.getName(), true, VariableInfo.NESTED);

   public VariableInfo[] getVariableInfo(TagData data)
   {
      VariableInfo[] infos = {INFO_1, INFO_2, INFO_3};
      return infos;
   }
}
