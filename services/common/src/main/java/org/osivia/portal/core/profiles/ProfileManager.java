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
 */
package org.osivia.portal.core.profiles;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.core.model.portal.Portal;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("osivia:service=ProfileManager")
public class ProfileManager implements IProfileManager {

    public static final String ATTRIBUTE_PROFILE_NAME = "osivia.profil";
    public static final String DEFAULT_PROFIL_NAME = "osivia.default_profil";


    @Autowired
    IPreviewModeService previewModeService;
    
    @Autowired
    ILocaleService localeService;

    private CMSService cmsService;


    private CMSService getCMSService() {
        if (cmsService == null) {
            cmsService = Locator.getService(CMSService.class);
        }

        return cmsService;
    }

    
    private Profile createUserProfile(PortalControllerContext portalControllerContext, UniversalID spaceID) {

        /* Récupération des rôles */

        CMSContext cmsContext = new CMSContext(portalControllerContext);
        
        HttpSession session = portalControllerContext.getHttpServletRequest().getSession();



        Space space; 
        try {
            space = (Space) getCMSService().getCMSSession(cmsContext).getDocument(spaceID);
        } catch(Exception e)    {
            throw new RuntimeException(e);
        }

        if (portalControllerContext.getHttpServletRequest().getUserPrincipal() != null) {


            /* On parcourt les espaces pour voir celui qui correspond au profil */
            for (Profile profile : space.getProfiles()) {
                if( StringUtils.isNotEmpty(profile.getUrl()))
                   if( portalControllerContext.getHttpServletRequest().isUserInRole(profile.getRole())) {
                       
                           boolean keepPage = true;
                          // Is the page readable
                          try    {
                               getCMSService().getCMSSession(cmsContext).getDocument(new UniversalID(spaceID.getRepositoryName(), profile.getUrl()));
                          } catch( CMSException e)  {
                              keepPage = false;
                          }
                       
                          if( keepPage) {
                              session.setAttribute(getProfileKey(spaceID), profile);
                              return profile;
                          }
                    }
            }


            /* Aucun profil trouve, création d'un profil par défaut */

            String pageAccueilConnecte = (String) space.getProperties().get("portal.unprofiledPageId");
            if (pageAccueilConnecte == null) {
                pageAccueilConnecte = (String) space.getProperties().get("portal.unprofiledPageId");
            }

            Profile profilDefaut = new Profile(DEFAULT_PROFIL_NAME, "default", pageAccueilConnecte, "");
            session.setAttribute(getProfileKey(spaceID), profilDefaut);

            return profilDefaut;
        }

        return null;

    }

   
    public Profile getMainProfile( PortalControllerContext portalCtx, UniversalID spaceID) {
 
        HttpSession session = portalCtx.getHttpServletRequest().getSession();

        Profile profile = (Profile) session.getAttribute(getProfileKey(spaceID));
        if (profile == null) {
            profile = this.createUserProfile(portalCtx, spaceID);
        }
        return profile;

    }


	private String getProfileKey(UniversalID spaceID) {
		return ATTRIBUTE_PROFILE_NAME+"."+spaceID.toString();
	}


  


}
