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
package org.jboss.portal.theme.impl;

import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.ThemeService;

/**
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public final class PageServiceImpl extends AbstractJBossService implements PageService
{
   private LayoutService layoutService;
   private ThemeService themeService;
//   private InterceptorStack pageStack;

   /** Inject the layout service (must be defined as a dependent service in service descriptor). */
   public void setLayoutService(LayoutService layoutService)
   {
      this.layoutService = layoutService;
   }

   public LayoutService getLayoutService()
   {
      return layoutService;
   }

   /** Inject the theme service (must be defined as a dependent service in service descriptor). */
   public void setThemeService(ThemeService themeService)
   {
      this.themeService = themeService;
   }

   public ThemeService getThemeService()
   {
      return themeService;
   }
}
