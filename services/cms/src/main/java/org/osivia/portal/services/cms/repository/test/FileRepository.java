package org.osivia.portal.services.cms.repository.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.common.xml.XMLTools;
import org.jboss.portal.core.model.content.spi.ContentProviderRegistry;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.metadata.PageMetaData;
import org.jboss.portal.core.model.portal.metadata.PortalMetaData;
import org.jboss.portal.core.model.portal.metadata.PortalObjectMetaData;
import org.jboss.portal.core.model.portal.metadata.WindowMetaData;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.SecurityConstants;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Profile;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryFolder;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.StreamableRepository;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.services.cms.repository.test.export.ExportRepositoryBean;
import org.osivia.portal.services.cms.repository.test.export.ExportRepositoryDocument;
import org.osivia.portal.services.cms.repository.test.export.FileUtils;
import org.osivia.portal.services.cms.repository.test.imports.ProfilBean;
import org.osivia.portal.services.cms.repository.test.imports.XMLSerializer;
import org.springframework.util.FileCopyUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


/**
 * File based repository
 */
/**
 * @author Jean-Sébastien
 */
public class FileRepository extends UserRepositoryTestBase implements StreamableRepository {

    protected static final Log log = LogFactory.getLog(FileRepository.class);


    /** . */
    public static final int OVERWRITE_IF_EXISTS = 0;

    /** . */
    public static final int KEEP_IF_EXISTS = 1;

    public File inputFile = null;

    
    public FileUtils fileUtils;
    
    public static String checksum = null;
    

    public FileRepository(SharedRepositoryKey repositoryKey, String userName) {
        super(repositoryKey, null, userName);
        fileUtils = new FileUtils(this);
    }

    
    public Map<String,RepositoryDocument> getDocuments()   {
        return ((TestUserStorage) getUserStorage()).getDocuments();
    }
    

    @Override
    public void addEmptyPage(String id, String name, String parentId) throws CMSException {
        RepositoryDocument parent = getSharedDocument(parentId);
        addPage(id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), new ArrayList<ModuleRef>(), null);
    }

    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     * @throws CMSException
     */
    private void addPage(String id, String name, String parentId, String spaceId, List<String> children, List<ModuleRef> modules, Map propMap) throws CMSException {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Page " + id);

        if (propMap != null) {
            properties.putAll(propMap);
        }


        MemoryRepositoryPage page = new MemoryRepositoryPage(this, id, name, null, parentId, spaceId, children, properties, modules);

        addDocument(id, page);
    }


    protected void createPage(PortalMetaData portalMetaData, List<String> parentHierarchy, PageMetaData pageMetaData, List<String> pageList, Map<String, Object> portalProperties, List<String> inheritedAcls) throws CMSException {

        try {


            List<String> acls = new ArrayList<>();


            if (pageMetaData.getSecurityConstraints() != null) {
                Set<RoleSecurityBinding> pageConstraints = (Set<RoleSecurityBinding>) pageMetaData.getSecurityConstraints().getConstraints();
                for (RoleSecurityBinding constraint : pageConstraints) {
                    for (Object action : constraint.getActions()) {
                        if ("view".equals(action)) {
                            // Authenticated,unchecked
                            if (SecurityConstants.UNCHECKED_ROLE_NAME.equals(constraint.getRoleName())) {
                                acls.add("_anonymous_");
                                break;
                            } else if (SecurityConstants.AUTHENTICATED_ROLE_NAME.equals(constraint.getRoleName()))
                                acls.add("group:members");
                            else
                                acls.add("group:" + constraint.getRoleName());
                        }
                    }
                }
            }


            if (acls.isEmpty())
                acls = inheritedAcls;


            inheritedAcls = new ArrayList<>();


            String pageName = getPageName(pageMetaData, parentHierarchy);

            log.debug("create page " + pageName);

            List<ModuleRef> modules = new ArrayList<ModuleRef>();

            Map<String, Object> pageProperties = configure(pageMetaData);

            /* change defaut page properties */

            String portaldefaultObjectName = (String) portalProperties.get("portal.defaultObjectName");
            if (StringUtils.equals(portaldefaultObjectName, pageMetaData.getName())) {
                portalProperties.put("portal.defaultPageId", pageName);
            }

            String unprofiledPageName = (String) portalProperties.get("osivia.unprofiled_home_page");
            if (StringUtils.equals(unprofiledPageName, pageMetaData.getName())) {
                portalProperties.put("portal.unprofiledPageId", pageName);
            }


            /* get childrens */
            List<String> children = new ArrayList<>();


            Map<String, PortalObjectMetaData> childrenMetadata = pageMetaData.getChildren();

            for (PortalObjectMetaData portalObjectMD : childrenMetadata.values()) {
                if (portalObjectMD instanceof WindowMetaData) {
                    WindowMetaData windowMetaData = (WindowMetaData) portalObjectMD;
                    // Add window as module

                    Map<String, String> moduleProperties = new ConcurrentHashMap<>();

                    for (Map.Entry<String, String> entry : windowMetaData.getProperties().entrySet()) {
                        if (!entry.getKey().equals(ThemeConstants.PORTAL_PROP_ORDER))
                            moduleProperties.put(entry.getKey(), entry.getValue());
                    }
                    ModuleRef module = new ModuleRef(windowMetaData.getName(), windowMetaData.getRegion(), windowMetaData.getContent().getURI(), moduleProperties);
                    modules.add(module);


                }
                if (portalObjectMD instanceof PageMetaData) {
                    List<String> pageHierarchy = new ArrayList<>();
                    pageHierarchy.addAll(parentHierarchy);
                    pageHierarchy.add(pageMetaData.getName().toUpperCase());


                    // Add page as a child of container
                    createPage(portalMetaData, pageHierarchy, (PageMetaData) portalObjectMD, children, portalProperties, new ArrayList<>());

                }
            }


            MemoryRepositoryPage page = new MemoryRepositoryPage(this, getPageName(pageMetaData, parentHierarchy), getPageName(pageMetaData, parentHierarchy), null, getParentName(parentHierarchy), getPortalName(portalMetaData), children,
                    pageProperties, modules);

            addDocument(getPageName(pageMetaData, parentHierarchy), page);

            setACL(getPageName(pageMetaData, parentHierarchy), acls);

            pageList.add(getPageName(pageMetaData, parentHierarchy));

        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    protected void createSpace(PortalMetaData portalMetaData) throws CMSException {

        if (log.isDebugEnabled())
            log.debug("create space " + getPortalName(portalMetaData));


        Map<String, Object> portalProperties = configure(portalMetaData);

        portalProperties = configure(portalMetaData);

        portalProperties.put("osivia.hidden", Boolean.TRUE);


        /* Create pages */

        Map<String, PortalObjectMetaData> children = portalMetaData.getChildren();

        List<String> pageHierarchy = new ArrayList<>();
        pageHierarchy.add(portalMetaData.getName().toUpperCase());

        List<String> portalChildren = new ArrayList<String>();

        @SuppressWarnings("unchecked")
        List<String> acls = new ArrayList<>();
        List<String> inheritedAcls = new ArrayList<>();
        if (portalMetaData.getSecurityConstraints() != null) {
            Set<RoleSecurityBinding> portalConstraints = (Set<RoleSecurityBinding>) portalMetaData.getSecurityConstraints().getConstraints();

            for (RoleSecurityBinding constraint : portalConstraints) {
                for (Object action : constraint.getActions()) {
                    if ("viewrecursive".equals(action)) {
                        acls.add("group:" + constraint.getRoleName());
                        inheritedAcls.add("group:" + constraint.getRoleName());
                    }

                    if ("view".equals(action)) {
                        // Authenticated,unchecked
                        if (SecurityConstants.UNCHECKED_ROLE_NAME.equals(constraint.getRoleName())) {

                            acls.add("_anonymous_");
                            break;
                        } else if (SecurityConstants.AUTHENTICATED_ROLE_NAME.equals(constraint.getRoleName()))
                            acls.add("group:members");
                        else
                            acls.add("group:" + constraint.getRoleName());
                    }
                }
            }
        }

        // Anonymous is the less restrictive right
        if (acls.contains("_anonymous_")) {
            acls.clear();
            acls.add("_anonymous_");
        }


        for (PortalObjectMetaData portalObjectMD : children.values()) {
            if (portalObjectMD instanceof PageMetaData) {
                createPage(portalMetaData, pageHierarchy, (PageMetaData) portalObjectMD, portalChildren, portalProperties, inheritedAcls);
            }
        }


        /* Create portal */

        String encodedList = (String) portalProperties.get("osivia.profils");
        portalProperties.remove("osivia.profils");

        String encodedStyles = (String) portalProperties.get("osivia.liste_styles");
        portalProperties.remove("osivia.liste_styles");


        MemoryRepositorySpace space = new MemoryRepositorySpace(this, getPortalName(portalMetaData), getPortalName(portalMetaData), null, portalChildren, portalProperties, new ArrayList<ModuleRef>());

        // profiles
        XMLSerializer serializer = new XMLSerializer();


        List<ProfilBean> profils = serializer.decodeAll(encodedList);
        if (profils == null) {
            profils = new ArrayList<ProfilBean>();
        }

        for (ProfilBean profile : profils) {
            String roleName = profile.getRoleName();
            if (SecurityConstants.AUTHENTICATED_ROLE_NAME.equals(roleName)) {
                roleName = "members";
            }

            Profile importProfile = new Profile(profile.getName(), roleName, profile.getDefaultPageName(), profile.getNuxeoVirtualUser());
            space.getProfiles().add(importProfile);
        }

        // styles

        if (StringUtils.isNotEmpty(encodedStyles)) {
            for (String style : Arrays.asList(encodedStyles.split(","))) {
                space.getStyles().add(style);
            }
        }


        addDocument(getPortalName(portalMetaData), space);

        setACL(getPortalName(portalMetaData), acls);

    }


    private Map<String, Object> configure(PortalObjectMetaData metaData) {
        Map<String, Object> portalProperties = new ConcurrentHashMap<String, Object>();

        // Configure properties
        for (Map.Entry<String, String> entry : metaData.getProperties().entrySet()) {
            portalProperties.put(entry.getKey(), entry.getValue());
        }

        String title = null;

        try {
            if (metaData.getDisplayName() != null) {
                if (metaData.getDisplayName() != null) {
                    Value localeTitle = metaData.getDisplayName().getValue(Locale.FRENCH, true);
                    if (localeTitle != null)
                        title = localeTitle.getString();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        if (title == null)
            title = metaData.getName();
        portalProperties.put("dc:title", title);
        return portalProperties;
    }


    private String getPageName(PageMetaData pageMetaData, List<String> parentHierarchy) {
        String hierarchy = "";
        for (String curHierarchy : parentHierarchy) {
            hierarchy += curHierarchy;
            hierarchy += "_";
        }
        return hierarchy + pageMetaData.getName().toUpperCase();
    }


    private String getParentName(List<String> parentHierarchy) {
        String hierarchy = "";
        for (String curHierarchy : parentHierarchy) {
            if (hierarchy.length() > 0)
                hierarchy += "_";
            hierarchy += curHierarchy;

        }
        return hierarchy;
    }


    private String getPortalName(PortalMetaData portalMetaData) {
        return portalMetaData.getName().toUpperCase();
    }

    protected void initDocuments() {

        ((TestUserStorage) getUserStorage()).initDocuments();

        if (inputFile != null) {
            importFile(inputFile);
            inputFile = null;
        } else {
            File file = new File(System.getProperty("portal.configuration.path") + "configuration-" + getRepositoryName() + ".json");
            if (file.exists()) {
                checksum = fileUtils.getCheckSum(file);
                fileUtils.importFile(file);
            } else
                importDefaultObject();
        }
    }

    private void importFile(File importFile) {
      fileUtils.importFile(importFile);
    }

    public void checkAndReload() {
        File file = new File(System.getProperty("portal.configuration.path") + "configuration-" + getRepositoryName() + ".json");
        if( file.exists())  {
            String newChecksum = fileUtils.getCheckSum(file);
            
            if( ! StringUtils.equals(newChecksum, checksum))   {
                startInitBatch();
            }
        }
      }
   

    private void importDefaultObject() {
        try {
            DocumentBuilder builder = XMLTools.getDocumentBuilderFactory().newDocumentBuilder();
            EntityResolver entityResolver = Locator.getService("portal:service=EntityResolver", EntityResolver.class);
            builder.setEntityResolver(entityResolver);

            ContentProviderRegistry contentProvicerRegistry = Locator.getService("portal:service=ContentProviderRegistry", ContentProviderRegistry.class);


            InputStream in = IOTools.safeBufferedWrapper(this.getClass().getResourceAsStream("/default-object.xml"));

            Document doc = builder.parse(in);
            Element deploymentsElt = doc.getDocumentElement();
            List<Element> deploymentElts = XMLTools.getChildren(deploymentsElt, "deployment");
            ArrayList<Unit> units = new ArrayList<Unit>(deploymentElts.size());

            for (Element deploymentElt : deploymentElts) {
                Unit unit = new Unit();

                //
                Element parentRefElt = XMLTools.getUniqueChild(deploymentElt, "parent-ref", false);
                unit.parentRef = parentRefElt == null ? null : PortalObjectId.parse(XMLTools.asString(parentRefElt), PortalObjectPath.LEGACY_FORMAT);

                //
                Element ifExistsElt = XMLTools.getUniqueChild(deploymentElt, "if-exists", false);
                unit.ifExists = KEEP_IF_EXISTS;
                if (ifExistsElt != null) {
                    String ifExists = XMLTools.asString(ifExistsElt);
                    if ("overwrite".equals(ifExists)) {
                        unit.ifExists = OVERWRITE_IF_EXISTS;
                    } else if ("keep".equals(ifExists)) {
                        unit.ifExists = KEEP_IF_EXISTS;
                    }
                }

                // The object to create
                PortalObjectMetaData metaData = null;

                //
                Element metaDataElt = XMLTools.getUniqueChild(deploymentElt, "portal", false);

                if (metaDataElt == null) {
                    metaDataElt = XMLTools.getUniqueChild(deploymentElt, "page", false);
                    if (metaDataElt == null) {
                        metaDataElt = XMLTools.getUniqueChild(deploymentElt, "window", false);
                        if (metaDataElt == null) {
                            metaDataElt = XMLTools.getUniqueChild(deploymentElt, "context", false);
                        }
                    }
                }
                if (metaDataElt != null) {
                    metaData = PortalObjectMetaData.buildMetaData(contentProvicerRegistry, metaDataElt);
                } else {
                    log.debug("Instances element in -object.xml is not supported anymore");
                }

                //
                if (metaData != null) {
                    unit.metaData = metaData;
                    units.add(unit);
                }
            }

            // Create all objects
            for (Unit unit : units) {

                if (unit.metaData instanceof PortalMetaData) {

                    createSpace((PortalMetaData) unit.metaData);
                }
            }


        } catch (Exception e) {
            log.error(e);
        }
    }

    /** A unit of deployment in the deployment descriptor. */
    protected static class Unit {

        /** The strategy to use when the root object already exists. */
        protected int ifExists;

        /** The parent ref. */
        protected PortalObjectId parentRef;

        /** Meta data of the deployed portal object. */
        protected Object metaData;

        /** The handle of the deployed object if not null. */
        protected PortalObjectId ref;

        public String toString() {
            StringBuffer buffer = new StringBuffer("Unit[::ifExists=" + ifExists);
            buffer.append(":parentRef=").append(parentRef);
            buffer.append(":Metadata=").append(metaData).append(":ref=").append(ref).append("]");
            return buffer.toString();
        }
    }




    @Override
    public void saveTo(OutputStream out) {
       fileUtils.saveTo(out);
    }



    @Override
    public void readFrom(InputStream in) {
        try {
            File targetFile = File.createTempFile("configuration", "json");
            FileOutputStream outStream = new FileOutputStream(targetFile);
            FileCopyUtils.copy(in, outStream);

            inputFile = targetFile;
            startInitBatch();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void restore() {
        startInitBatch();
    }


}
