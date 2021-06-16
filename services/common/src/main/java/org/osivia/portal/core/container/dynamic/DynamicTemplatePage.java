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
package org.osivia.portal.core.container.dynamic;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.jboss.portal.common.i18n.LocalizedString;

import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.container.persistent.PageImplBase;
import org.osivia.portal.core.container.persistent.StaticPortalObjectContainer;
import org.osivia.portal.core.dynamic.DynamicPageBean;



/**
 * Dynamic template page.
 *
 * @see TemplatePage
 * @see ITemplatePortalObject
 */
@SuppressWarnings("unchecked")
public final class DynamicTemplatePage extends TemplatePage {

    /** Page bean. */
    private final DynamicPageBean pageBean;
    /** Local display name. */
    private final Map<Locale, String> localDisplayName;
    
    private final PortalObjectId templateID;



    /**
     * Create dynamic template page.
     *
     * @param container portal object container
     * @param parentId parent portal object identifier
     * @param name name
     * @param displayNames display names
     * @param template template portal object
     * @param dynamicContainer dynamic portal object container
     * @param pageBean dynamic page bean
     * @param poid portal object identifier
     * @return dynamic template page
     */
    public static DynamicTemplatePage createPage(StaticPortalObjectContainer container, PortalObjectId parentId, String name, Map<Locale, String> displayNames,
            PageImplBase template, String theme, DynamicPortalObjectContainer dynamicContainer, DynamicPageBean pageBean, PortalObjectId poid) {
        DynamicTemplatePage page = null;
        try {
            page = new DynamicTemplatePage(container, parentId, name, displayNames, template, theme, dynamicContainer, pageBean);
            
            
              
        } catch (Exception e) {
            PortalObjectId pageId = new PortalObjectId("", new PortalObjectPath(parentId.getPath().toString().concat("/").concat(name),
                    PortalObjectPath.CANONICAL_FORMAT));

            // Page non accessible, le template peut avoir été supprimé (auquel cas le template est null)
            // On supprime la page dynamique pour ne plus rencontrer d'erreurs
            dynamicContainer.removeDynamicPage(pageId.toString(PortalObjectPath.SAFEST_FORMAT));

            

            throw new RuntimeException("Page " + pageId + " has not be created. Exception = " + e.getMessage() + ". Check the template " + poid.toString(), e);

        }

        return page;

    }


    /**
     * Constructor.
     *
     * @param container portal object container
     * @param parentId parent portal object identifier
     * @param name name
     * @param displayNames display names
     * @param template template portal object
     * @param dynamicContainer dynamic portal object container
     * @param pageBean dynamic page bean
     */
    private DynamicTemplatePage(StaticPortalObjectContainer container, PortalObjectId parentId, String name, Map<Locale, String> displayNames,
    		PageImplBase template, String theme, DynamicPortalObjectContainer dynamicContainer, DynamicPageBean pageBean) {
        super(container, parentId, name, template, theme, dynamicContainer);

        this.pageBean = pageBean;

        this.getLocalProperties().putAll(pageBean.getPageProperties());

        this.getLocalProperties().put(InternalConstants.TABS_ORDER_PROPERTY, "" + pageBean.getOrder());
        this.getLocalProperties().put(InternalConstants.PAGE_PROP_NAME_DYNAMIC, InternalConstants.PROP_VALUE_ON);


        this.localDisplayName = displayNames;
        
        this.templateID  = template.getId();
        // TODO : analyser si on peut faire du lazy fetching sur les propriétés
        
        setUpdateTs(template.getUpdateTs());

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeclaredProperty(String name) {
        String value = this.getLocalProperties().get(name);
        if (value == null) {
            value = super.getDeclaredProperty(name);
        }
        return value;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public LocalizedString getDisplayName() {
        if (this.localDisplayName != null) {
            return new LocalizedString(this.localDisplayName, Locale.ENGLISH);
        } else {
            return super.getDisplayName();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosable() {
        return this.pageBean.isClosable();
    }



    /**
     * Getter for pageBean.
     *
     * @return the pageBean
     */
    public DynamicPageBean getPageBean() {
        return this.pageBean;
    }


    
    /**
     * Gets the template ID.
     *
     * @return the template ID
     */
    public PortalObjectId getTemplateId() {
        return templateID;
    }

}
