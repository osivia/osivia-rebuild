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
package org.jboss.portal.server.servlet;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class PathParser
{

   public PathMappingResult map(PathMapping mapping, String path) throws IllegalArgumentException
   {
      if (mapping == null)
      {
         throw new IllegalArgumentException("No null mapping allowed");
      }
      if (path == null)
      {
         throw new IllegalArgumentException("No null request path allowed");
      }
      if (path.length() == 0)
      {
         throw new IllegalArgumentException("No empty request path allowed");
      }
      Object target = mapping.getRoot();
      if (target == null)
      {
         return new PathMappingResult(target, null, path);
      }
      if ("/".equals(path))
      {
         return new PathMappingResult(target, "", "/");
      }
      else
      {
         int previousSlashPos = 0;
         StringBuffer matchedPath = new StringBuffer();
         while (true)
         {
            int nextSlashPos = path.indexOf('/', previousSlashPos + 1);
            if (nextSlashPos == -1)
            {
               String atom = path.substring(previousSlashPos + 1);
               Object o = mapping.getChild(target, atom);
               if (o != null)
               {
                  target = o;
                  matchedPath.append('/').append(atom);
                  String remainingPath = "";
                  return new PathMappingResult(target, matchedPath.toString(), remainingPath);
               }
               else
               {
                  String remainingPath = "/" + atom;
                  return new PathMappingResult(target, matchedPath.toString(), remainingPath);
               }
            }
            else if (nextSlashPos == previousSlashPos + 1)
            {
               previousSlashPos = nextSlashPos;
            }
            else
            {
               String atom = path.substring(previousSlashPos + 1, nextSlashPos);
               Object o = mapping.getChild(target, atom);
               if (o != null)
               {
                  target = o;
                  matchedPath.append('/').append(atom);
                  previousSlashPos = nextSlashPos;
               }
               else
               {

                  String remainingPath = path.substring(previousSlashPos);
                  return new PathMappingResult(target, matchedPath.toString(), remainingPath);
               }
            }
         }
      }
   }
}
