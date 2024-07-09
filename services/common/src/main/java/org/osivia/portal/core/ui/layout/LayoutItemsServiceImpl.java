package org.osivia.portal.core.ui.layout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.toutatice.portail.cms.producers.test.AdvancedRepository;
import fr.toutatice.portail.cms.producers.test.TestRepositoryLocator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.ui.layout.LayoutGroup;
import org.osivia.portal.api.ui.layout.LayoutItem;
import org.osivia.portal.api.ui.layout.LayoutItemsService;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.core.container.dynamic.DynamicTemplatePage;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Layout items service implementation.
 *
 * @author Cédric Krommenhoek
 * @see LayoutItemsService
 */
@Service(LayoutItemsService.MBEAN_NAME)
public class LayoutItemsServiceImpl implements LayoutItemsService {

    /**
     * Layout groups page property.
     */
    private static final String LAYOUT_GROUPS_PROPERTY = "osivia.layout.groups";

    /**
     * Selected layout items session attribute suffix.
     */
    private static final String SELECTED_LAYOUT_ITEMS_ATTRIBUTE_SUFFIX = ".selected-layout-items";


    /**
     * layout items groups session attribute suffix.
     */
    private static final String LAYOUT_ITEMS_GROUPS_SUFFIX = ".layout-items-groups";
    
    /**
     * Log.
     */
    private final Log log;


    /**
     * CMS service.
     */
    @Autowired
    private CMSService cmsService;


    /**
     * Constructor.
     */
    public LayoutItemsServiceImpl() {
        super();

        // Log
        this.log = LogFactory.getLog(this.getClass());
    }


    @Override
    public List<LayoutGroup> getGroups(PortalControllerContext portalControllerContext) {
        // Layout groups implementation
        List<LayoutGroupImpl> groupsImpl = this.getGroupsImpl(portalControllerContext);

        // Layout groups
        List<LayoutGroup> groups;

        if (CollectionUtils.isEmpty(groupsImpl)) {
            groups = null;
        } else {
            groups = new ArrayList<>(groupsImpl.size());
            groups.addAll(groupsImpl);
        }

        return groups;
    }


    @Override
    public LayoutGroup getGroup(PortalControllerContext portalControllerContext, String groupId) {
        // Layout groups
        List<LayoutGroup> groups = this.getGroups(portalControllerContext);

        // Selected layout group
        LayoutGroup group = null;
        if (CollectionUtils.isNotEmpty(groups)) {
            Iterator<LayoutGroup> iterator = groups.iterator();
            while ((group == null) && iterator.hasNext()) {
                LayoutGroup next = iterator.next();
                if (StringUtils.equals(groupId, next.getId())) {
                    group = next;
                }
            }
        }

        if (group == null) {
            // New layout group
            LayoutGroupImpl groupImpl = new LayoutGroupImpl();
            groupImpl.setId(groupId);

            group = groupImpl;
        }

        return group;
    }

    
    @Override
    public void removeGroup(PortalControllerContext portalControllerContext, UniversalID pageId, String groupId) {


        if ((groupId != null) && (pageId != null)) {
            // Page property
            String property;

            // Layout groups
            List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);
            if (CollectionUtils.isEmpty(groups)) {
                groups = new ArrayList<>(1);
            } else {
                // Remove layout group
                boolean removed = false;
                Iterator<LayoutGroupImpl> iterator = groups.iterator();
                while (!removed && iterator.hasNext()) {
                    LayoutGroup next = iterator.next();
                    if (StringUtils.equals(groupId, next.getId())) {
                        iterator.remove();
                        removed = true;
                    }
                }
            }

            

            try {
                // Page document
                Document pageDocument = this.getPageDocument(portalControllerContext, pageId);
                // Repository
                AdvancedRepository repository = getRepository(portalControllerContext, pageDocument);

                if( groups.size() > 0)  {
                    // Container
                    LayoutGroupsContainer container = new LayoutGroupsContainer();
                    container.setGroups(groups);

                    // JSON object mapper
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        property = mapper.writeValueAsString(container);
                    } catch (JsonProcessingException e) {
                        property = null;
                        this.log.error(e.getLocalizedMessage());
                    }
                    // Update page
                    pageDocument.getProperties().put(LAYOUT_GROUPS_PROPERTY, property);
                
                }   else    {
                    pageDocument.getProperties().remove(LAYOUT_GROUPS_PROPERTY);
                }
                repository.updateDocument(pageDocument.getId().getInternalID(), (RepositoryDocument) pageDocument);
            } catch (CMSException e) {
                this.log.error(e);
            }
        }
    }

    

    @Override
    public void setGroup(PortalControllerContext portalControllerContext, LayoutGroup group) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);

        if ((group != null) && (page != null)) {
            // Page property
            String property;

            // Layout groups
            List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);
            if (CollectionUtils.isEmpty(groups)) {
                groups = new ArrayList<>(1);
            } else {
                // Remove layout group
                boolean removed = false;
                Iterator<LayoutGroupImpl> iterator = groups.iterator();
                while (!removed && iterator.hasNext()) {
                    LayoutGroup next = iterator.next();
                    if (StringUtils.equals(group.getId(), next.getId())) {
                        iterator.remove();
                        removed = true;
                    }
                }
            }

            // Layout items
            List<LayoutItemImpl> items;
            if (CollectionUtils.isEmpty(group.getItems())) {
                items = new ArrayList<>(0);
            } else {
                items = new ArrayList<>(group.getItems().size());
                for (LayoutItem item : group.getItems()) {
                    LayoutItemImpl itemImpl;
                    if (item instanceof LayoutItemImpl) {
                        itemImpl = (LayoutItemImpl) item;
                    } else {
                        itemImpl = new LayoutItemImpl();
                        try {
                            BeanUtils.copyProperties(itemImpl, item);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            itemImpl = null;
                            this.log.error(e.getLocalizedMessage());
                        }
                    }

                    if (itemImpl != null) {
                        items.add(itemImpl);
                    }
                }
            }

            // Added layout group implementation
            LayoutGroupImpl groupImpl = new LayoutGroupImpl();
            groupImpl.setId(group.getId());
            groupImpl.setLabel(group.getLabel());
            groupImpl.setItemsImpl(items);
            groups.add(groupImpl);

            // Container
            LayoutGroupsContainer container = new LayoutGroupsContainer();
            container.setGroups(groups);

            // JSON object mapper
            ObjectMapper mapper = new ObjectMapper();
            try {
                property = mapper.writeValueAsString(container);
            } catch (JsonProcessingException e) {
                property = null;
                this.log.error(e.getLocalizedMessage());
            }

            // Page identifier
            UniversalID pageId = new UniversalID(page.getProperty("osivia.navigationId"));
            try {
                // Page document
                Document pageDocument = this.getPageDocument(portalControllerContext, pageId);
                // Repository
                AdvancedRepository repository = getRepository(portalControllerContext, pageDocument);

                // Update page
                pageDocument.getProperties().put(LAYOUT_GROUPS_PROPERTY, property);
                repository.updateDocument(pageDocument.getId().getInternalID(), (RepositoryDocument) pageDocument);
            } catch (CMSException e) {
                this.log.error(e);
            }
        }
    }


    @Override
    public List<LayoutItem> getItems(PortalControllerContext portalControllerContext, String groupId) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // Administrator indicator
        boolean admin = PageCustomizerInterceptor.isAdministrator(controllerContext);

        // Layout group
        LayoutGroup group = this.getGroup(portalControllerContext, groupId);


        // Layout items
        List<LayoutItem> items;

        if (group == null) {
            items = null;
        } else {
            // Current person
            Person person = this.getCurrentPerson(portalControllerContext);
            // Current person profiles
            List<String> profiles;
            if ((person == null) || CollectionUtils.isEmpty(person.getProfiles())) {
                profiles = null;
            } else {
                profiles = new ArrayList<>(person.getProfiles().size());
                for (Name name : person.getProfiles()) {
                    Name suffix = name.getSuffix(name.size());
                    String profile = StringUtils.substringBefore(suffix.toString(), "=");
                    if (StringUtils.isNotEmpty(profile)) {
                        profiles.add(profile);
                    }
                }
            }

            // Layout items
            items = new ArrayList<>(group.getItems().size());
            for (LayoutItem item : group.getItems()) {
                if (admin || CollectionUtils.isEmpty(item.getProfiles()) || (CollectionUtils.isNotEmpty(profiles) && CollectionUtils.containsAny(profiles, item.getProfiles()))) {
                    items.add(item);
                }
            }
        }

        return items;
    }


    @Override
    public List<LayoutItem> getCurrentItems(PortalControllerContext portalControllerContext) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);

        // Layout groups
        List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);

        // Current layout items
        List<LayoutItem> currentItems;

        if ((page == null) || CollectionUtils.isEmpty(groups)) {
            currentItems = null;
        } else {
            currentItems = new ArrayList<>(groups.size());

            // Selected layout items
            SelectedLayoutItems selectedLayoutItems = this.getSelectedLayoutItems(portalControllerContext, page);

            for (LayoutGroupImpl group : groups) {
                if (CollectionUtils.isNotEmpty(group.getItems())) {
                    LayoutItem currentItem = this.getCurrentItem(selectedLayoutItems, group);
                    currentItems.add(currentItem);
                }
            }
        }

        return currentItems;
    }


    @Override
    public LayoutItem getCurrentItem(PortalControllerContext portalControllerContext, String groupId) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);
        //  Layout group
        LayoutGroup group = this.getGroup(portalControllerContext, groupId);

        // Current layout item
        LayoutItem currentItem = null;

        if ((page != null) && (group != null)) {
            // Selected layout items
            SelectedLayoutItems selectedLayoutItems = this.getSelectedLayoutItems(portalControllerContext, page);

            if (CollectionUtils.isNotEmpty(group.getItems())) {
                currentItem = this.getCurrentItem(selectedLayoutItems, group);
            }
        }

        return currentItem;
    }


    @Override
    public void selectItem(PortalControllerContext portalControllerContext, String id) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);
        // Layout groups
        List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);

        if (StringUtils.isNotEmpty(id) && (page != null) && CollectionUtils.isNotEmpty(groups)) {
            // Related layout group
            LayoutGroupImpl group = this.getRelatedGroup(groups, id);

            if (group != null) {
                // Selected layout items
                SelectedLayoutItems selectedLayoutItems = this.getSelectedLayoutItems(portalControllerContext, page);
                selectedLayoutItems.getSelection().put(group.getId(), id);
                selectedLayoutItems.getComputedWindowIds().remove(group.getId());
            }
        }
    }


    @Override
    public boolean isSelected(PortalControllerContext portalControllerContext, String itemId) {
        // Selected layout item indicator
        boolean selected = false;

        // Current layout items
        List<LayoutItem> items = this.getCurrentItems(portalControllerContext);

        if (CollectionUtils.isNotEmpty(items)) {
            Iterator<LayoutItem> iterator = items.iterator();
            while (!selected && iterator.hasNext()) {
                LayoutItem item = iterator.next();
                selected = StringUtils.equals(itemId, item.getId());
            }
        }

        return selected;
    }
    
    
    
    @Override
    public boolean isDefined(PortalControllerContext portalControllerContext, String itemId) {
        // Selected layout item indicator
        boolean defined = false;
        
        // Layout groups
        List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);
        
        if ( CollectionUtils.isNotEmpty(groups)) {
            for( LayoutGroup group: groups) {
                List<LayoutItem> layoutItems = getItems( portalControllerContext, group.getId());
                for( LayoutItem layoutItem : layoutItems) {
                    if( layoutItem.getId().equals(itemId))  {
                        defined = true;
                    }
                }
            }

        }

        return defined;
    }


    @Override
    public void markWindowAsRendered(PortalControllerContext portalControllerContext, Window window) {
        // Layout item identifier
        String id = window.getDeclaredProperty(LINKED_ITEM_ID_WINDOW_PROPERTY);
        // Layout groups
        List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);
        // Related layout group
        LayoutGroupImpl group = this.getRelatedGroup(groups, id);

        if (group != null) {
            // Window identifier
            String windowId = window.getId().toString();
            // Selected layout items
            SelectedLayoutItems selectedLayoutItems = this.getSelectedLayoutItems(portalControllerContext, window.getPage());

            // Computed window identifiers
            List<String> computedWindowIds = selectedLayoutItems.getComputedWindowIds().computeIfAbsent(group.getId(), k -> new ArrayList<>());

            if (!computedWindowIds.contains(windowId)) {
                computedWindowIds.add(windowId);
            }
        }
    }


    @Override
    public boolean isDirty(PortalControllerContext portalControllerContext, Window window) {
        // Layout item identifier
        String id = window.getDeclaredProperty(LINKED_ITEM_ID_WINDOW_PROPERTY);
        // Layout groups
        List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);
        // Related layout group
        LayoutGroupImpl group = this.getRelatedGroup(groups, id);

        // Dirty window indicator
        boolean dirty;

        if ((group == null) || !this.isSelected(portalControllerContext, id)) {
            dirty = true;
        } else {
            // Window identifier
            String windowId = window.getId().toString();
            // Selected layout items
            SelectedLayoutItems selectedLayoutItems = this.getSelectedLayoutItems(portalControllerContext, window.getPage());

            // Computed window identifiers
            List<String> computedWindowIds = selectedLayoutItems.getComputedWindowIds().get(group.getId());

            dirty = CollectionUtils.isEmpty(computedWindowIds) || !computedWindowIds.contains(windowId);
        }

        return dirty;
    }


    /**
     * Get selected layout items.
     *
     * @param portalControllerContext portal controller context
     * @param page                    current page
     * @return selected layout items
     */
    private SelectedLayoutItems getSelectedLayoutItems(PortalControllerContext portalControllerContext, Page page) {
        // HTTP session
        HttpSession session = this.getSession(portalControllerContext);

        // Selected layout items
        SelectedLayoutItems selectedLayoutItems;

        // HTTP session attribute name
        String name = page.getId().toString(PortalObjectPath.SAFEST_FORMAT) + SELECTED_LAYOUT_ITEMS_ATTRIBUTE_SUFFIX;

        // HTTP session attribute
        Object attribute = session.getAttribute(name);

        if (attribute instanceof SelectedLayoutItems) {
            selectedLayoutItems = (SelectedLayoutItems) attribute;
        } else {
            selectedLayoutItems = new SelectedLayoutItems();
            session.setAttribute(name, selectedLayoutItems);
        }

        return selectedLayoutItems;
    }


    /**
     * Get HTTP session.
     *
     * @param portalControllerContext portal controller context
     * @return HTTP session
     */
    private HttpSession getSession(PortalControllerContext portalControllerContext) {
        // HTTP servlet request
        HttpServletRequest servletRequest = portalControllerContext.getHttpServletRequest();

        // HTTP session
        HttpSession session;
        if (servletRequest == null) {
            session = null;
        } else {
            session = servletRequest.getSession();
        }

        return session;
    }


    /**
     * Get current page.
     *
     * @param portalControllerContext portal controller context
     * @return page
     */
    private Page getCurrentPage(PortalControllerContext portalControllerContext) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        return PortalObjectUtilsInternal.getPage(controllerContext);
    }


    private UniversalID getCurrentPageId(PortalControllerContext portalControllerContext) {
        // Navigation identifier
        String navigationId;
        if (portalControllerContext.getRequest() == null) {
            navigationId = null;
        } else {
            // Window
            PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());

            navigationId = window.getProperty("osivia.navigationId");
        }

        // Page identifier
        UniversalID pageId;
        if (StringUtils.isEmpty(navigationId)) {
            pageId = null;
        } else {
            pageId = new UniversalID(navigationId);
        }

        return pageId;
    }


    /**
     * Get page document.
     *
     * @param portalControllerContext portal controller context
     * @param pageId                  page identifier
     * @return document
     */
    private Document getPageDocument(PortalControllerContext portalControllerContext, UniversalID pageId) throws CMSException {
        // CMS controller
        CMSController cmsController = new CMSController(portalControllerContext);
        // CMS context
        CMSContext cmsContext = cmsController.getCMSContext();
        // CMS session
        CMSSession cmsSession = this.cmsService.getCMSSession(cmsContext);

        // Page document
        return cmsSession.getDocument(pageId);
    }


    /**
     * Get repository.
     *
     * @param portalControllerContext portal controller context
     * @param document                related document
     * @return repository
     */
    private AdvancedRepository getRepository(PortalControllerContext portalControllerContext, Document document) throws CMSException {
        // CMS controller
        CMSController cmsController = new CMSController(portalControllerContext);
        // CMS context
        CMSContext cmsContext = cmsController.getCMSContext();

        return TestRepositoryLocator.getTemplateRepository(cmsContext, document.getId().getRepositoryName());
    }


    /**
     * Get current person.
     *
     * @param portalControllerContext portal controller context
     * @return person
     */
    private Person getCurrentPerson(PortalControllerContext portalControllerContext) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // Server invocation
        ServerInvocation invocation = controllerContext.getServerInvocation();

        return (Person) invocation.getAttribute(Scope.SESSION_SCOPE, Constants.ATTR_LOGGED_PERSON_2);
    }

    

    /**
     * Get selected layout items.
     *
     * @param portalControllerContext portal controller context
     * @param page                    current page
     * @return selected layout items
     */
    
    private List<LayoutGroupImpl> getGroupsImpl(PortalControllerContext portalControllerContext) {
        
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);
        
        if( page== null)
            return null;
        
        // HTTP session
        HttpSession session = this.getSession(portalControllerContext);

        // Selected layout items
        PageLayoutGroups layoutGroups = null;

        // HTTP session attribute name
        String name = page.getId().toString(PortalObjectPath.SAFEST_FORMAT) + LAYOUT_ITEMS_GROUPS_SUFFIX;

        // HTTP session attribute
        Object attribute = session.getAttribute(name);

        // Has page been modified ?
        if (attribute instanceof PageLayoutGroups) {
            layoutGroups = (PageLayoutGroups) attribute;
            if(layoutGroups.getTimestamp() < page.getUpdateTs())    {
                layoutGroups = null;
            }
        }  
        
        if( layoutGroups == null) {
            layoutGroups = computeLayouts(portalControllerContext, page);
            session.setAttribute(name, layoutGroups );
        }

        return layoutGroups.getGroups();
    }   
    

    /**
     * Get layout groups implementation.
     *
     * @param portalControllerContext portal controller context
     * @return layout groups
     */
    private PageLayoutGroups computeLayouts(PortalControllerContext portalControllerContext, Page page) {
        
        // Layout groups
        List<LayoutGroupImpl> groups;

     // Page identifier
        UniversalID pageId = ObjectUtils.defaultIfNull(this.getCurrentPageId(portalControllerContext), new UniversalID(page.getProperty("osivia.navigationId")));

        // Page property
        String property;
        try {
            Document pageDocument = this.getPageDocument(portalControllerContext, pageId);

            property = (String) pageDocument.getProperties().get(LAYOUT_GROUPS_PROPERTY);
        } catch (CMSException e) {
            property = null;
            this.log.error(e);
        }

        // Layout groups container
        LayoutGroupsContainer container;

        if (StringUtils.isEmpty(property)) {
            container = null;
        } else {
            // JSON object mapper
            ObjectMapper mapper = new ObjectMapper();
            try {
                container = mapper.readValue(property, LayoutGroupsContainer.class);
               
            } catch (JsonProcessingException e) {
                container = null;
                this.log.error(e.getLocalizedMessage());
            }
        }

        if ((container == null) || CollectionUtils.isEmpty(container.getGroups())) {
            groups = null;
        } else {
            groups = container.getGroups();

            for (LayoutGroupImpl group : groups) {
                // Copy items implementation to generic items
                List<LayoutItemImpl> itemsImpl = group.getItemsImpl();
                if (CollectionUtils.isNotEmpty(itemsImpl)) {
                    group.getItems().addAll(itemsImpl);
                }
            }
        }

        return new PageLayoutGroups(System.currentTimeMillis(), groups);
    }


    /**
     * Get current layout item.
     *
     * @param selectedLayoutItems selected layout items
     * @param group               current layout group
     * @return layout item
     */
    private LayoutItem getCurrentItem(SelectedLayoutItems selectedLayoutItems, LayoutGroup group) {
        // Current layout item
        LayoutItem currentItem = null;

        if (MapUtils.isNotEmpty(selectedLayoutItems.getSelection())) {
            String currentItemId = selectedLayoutItems.getSelection().get(group.getId());
            if (StringUtils.isNotEmpty(currentItemId)) {
                Iterator<? extends LayoutItem> iterator = group.getItems().iterator();
                while ((currentItem == null) && iterator.hasNext()) {
                    LayoutItem item = iterator.next();
                    if (StringUtils.equals(currentItemId, item.getId())) {
                        currentItem = item;
                    }
                }
            }
        }

        if (currentItem == null) {
            currentItem = group.getItems().get(0);
        }

        return currentItem;
    }


    /**
     * Get related layout group.
     *
     * @param groups layout groups
     * @param id     layout item identifier
     * @return layout group
     */
    private LayoutGroupImpl getRelatedGroup(List<LayoutGroupImpl> groups, String id) {
        LayoutGroupImpl relatedGroup = null;
        if (CollectionUtils.isNotEmpty(groups)) {
            Iterator<LayoutGroupImpl> groupsIterator = groups.iterator();
            while ((relatedGroup == null) && groupsIterator.hasNext()) {
                LayoutGroupImpl group = groupsIterator.next();
                if (CollectionUtils.isNotEmpty(group.getItems())) {
                    Iterator<LayoutItem> itemsIterator = group.getItems().iterator();
                    while ((relatedGroup == null) && itemsIterator.hasNext()) {
                        LayoutItem item = itemsIterator.next();
                        if (StringUtils.equals(id, item.getId())) {
                            relatedGroup = group;
                        }
                    }
                }
            }
        }

        return relatedGroup;
    }

}
