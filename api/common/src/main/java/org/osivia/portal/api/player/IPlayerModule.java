/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.api.player;

import org.osivia.portal.api.cms.DocumentContext;


/**
 * Module defined by a portlet to display a document in a specific portlet.
 * 
 * @author Loïc Billon
 * @param <D> document context type
 */
public interface IPlayerModule<D extends DocumentContext> {
    
	/**
     * Make a player with the document.
     * 
     * @param documentContext document context
     * @return player
     */
    Player getCMSPlayer(D documentContext);

}
