package org.osivia.portal.services.cms.repository.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.common.xml.XMLTools;
import org.jboss.portal.core.model.content.spi.ContentProviderRegistry;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.metadata.PageMetaData;
import org.jboss.portal.core.model.portal.metadata.PortalMetaData;
import org.jboss.portal.core.model.portal.metadata.PortalObjectMetaData;
import org.jboss.portal.core.model.portal.metadata.WindowMetaData;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.locator.Locator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;


/**
 * File based repository
 */
public class FileRepository extends UserRepositoryTestBase {

    protected static final Log log = LogFactory.getLog(FileRepository.class);
    
    /** . */
    public static final int OVERWRITE_IF_EXISTS = 0;

    /** . */
    public static final int KEEP_IF_EXISTS = 1;

    public FileRepository(SharedRepositoryKey repositoryKey, String userName) {
        super(repositoryKey, null, userName);
    }



    
    
    
    
    
    protected void createPage(PortalMetaData portalMetaData, List<String> parentHierarchy, PageMetaData pageMetaData, List<String> pageList) throws CMSException {
        
        log.info("create page " + getPageName(pageMetaData, parentHierarchy));
        
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        List<String> children = new ArrayList<String>();
        
        Map<String, Object> pageProperties = configure(pageMetaData);
        
        /* get childrens */
        
        Map<String, PortalObjectMetaData> childrenMetadata = pageMetaData.getChildren();

        for (PortalObjectMetaData portalObjectMD : childrenMetadata.values())
        {
           if( portalObjectMD instanceof WindowMetaData)  {
               WindowMetaData windowMetaData = (WindowMetaData) portalObjectMD;
               // Add window as  module
               
               Map<String,String> moduleProperties = new ConcurrentHashMap<>();

               for (Map.Entry<String,String> entry : windowMetaData.getProperties().entrySet())
               {
                   moduleProperties.put(entry.getKey(), entry.getValue());
               }
               ModuleRef module = new ModuleRef(windowMetaData.getName() , windowMetaData.getRegion(), windowMetaData.getContent().getURI(), moduleProperties); 
               modules.add(module);  

               
           }
           if( portalObjectMD instanceof PageMetaData)  {
               List<String> pageHierarchy = new ArrayList<>();
               pageHierarchy.addAll(parentHierarchy);
               pageHierarchy.add( pageMetaData.getName().toUpperCase());
               
               // Add page as a child of container
               createPage(portalMetaData, pageHierarchy,(PageMetaData) portalObjectMD, pageList);
           }
        }

        
        MemoryRepositoryPage page = new MemoryRepositoryPage(this, getPageName(pageMetaData, parentHierarchy), getPageName(pageMetaData, parentHierarchy), null, getPortalName(portalMetaData), getPortalName(portalMetaData), children,
                pageProperties, modules);
        
        addDocument(getPageName(pageMetaData, parentHierarchy), page);
        
        pageList.add(getPageName(pageMetaData, parentHierarchy));
        
    } 
    
    
    protected void createSpace(PortalMetaData portalMetaData) throws CMSException {
        
        log.info("create space " + getPortalName(portalMetaData));        
        
        /* Create pages */
        
        Map<String, PortalObjectMetaData> children = portalMetaData.getChildren();
        
        List<String> pageHierarchy = new ArrayList<>();
        pageHierarchy.add( portalMetaData.getName().toUpperCase());
        
        List<String> portalChildren = new ArrayList<String>();

        for (PortalObjectMetaData portalObjectMD : children.values())
        {
           if( portalObjectMD instanceof PageMetaData)  {
               createPage(portalMetaData, pageHierarchy,(PageMetaData) portalObjectMD, portalChildren);
           }
        }

         
        /* Create portal */
        

        Map<String, Object> portalProperties = configure(portalMetaData);

         
        
        MemoryRepositorySpace space = new MemoryRepositorySpace(this, getPortalName(portalMetaData), getPortalName(portalMetaData), null, portalChildren, portalProperties,
                new ArrayList<ModuleRef>());
        addDocument(getPortalName(portalMetaData), space);       
    }


    private Map<String, Object> configure(PortalObjectMetaData metaData) {
        Map<String, Object> portalProperties = new ConcurrentHashMap<String, Object>();
        
        // Configure properties
        for (Map.Entry<String,String> entry : metaData.getProperties().entrySet())
        {
            portalProperties.put(entry.getKey(), entry.getValue());
        }
        portalProperties.put("dc:title", metaData.getName());
        return portalProperties;
    }


    private String getPageName(PageMetaData pageMetaData,  List<String> parentHierarchy) {
        String hierarchy = "";
        for( String curHierarchy : parentHierarchy) {
             hierarchy += curHierarchy;
            hierarchy += "_";
        }
        return  hierarchy + pageMetaData.getName().toUpperCase();
    }
    
    private String getPortalName(PortalMetaData portalMetaData) {
        return  portalMetaData.getName().toUpperCase();
    }

    protected void initDocuments() {

        
        try {
            DocumentBuilder builder = XMLTools.getDocumentBuilderFactory().newDocumentBuilder();
            EntityResolver entityResolver = Locator.getService("portal:service=EntityResolver", EntityResolver.class);
            builder.setEntityResolver(entityResolver);
            
            ContentProviderRegistry contentProvicerRegistry = Locator.getService("portal:service=ContentProviderRegistry", ContentProviderRegistry.class);
   
            PortalObjectContainer portalObjectContainer = Locator.getService("portal:container=PortalObject", PortalObjectContainer.class);
            
            
            InputStream in = IOTools.safeBufferedWrapper(this.getClass().getResourceAsStream("/default-object.xml"));
            
            Document doc = builder.parse(in);
            Element deploymentsElt = doc.getDocumentElement();
            List<Element> deploymentElts = XMLTools.getChildren(deploymentsElt, "deployment");
            ArrayList<Unit> units = new ArrayList<Unit>(deploymentElts.size());
            
            for (Element deploymentElt : deploymentElts)
            {
               Unit unit = new Unit();

               //
               Element parentRefElt = XMLTools.getUniqueChild(deploymentElt, "parent-ref", false);
               unit.parentRef = parentRefElt == null ? null : PortalObjectId.parse(XMLTools.asString(parentRefElt), PortalObjectPath.LEGACY_FORMAT);

               //
               Element ifExistsElt = XMLTools.getUniqueChild(deploymentElt, "if-exists", false);
               unit.ifExists = KEEP_IF_EXISTS;
               if (ifExistsElt != null)
               {
                  String ifExists = XMLTools.asString(ifExistsElt);
                  if ("overwrite".equals(ifExists))
                  {
                     unit.ifExists = OVERWRITE_IF_EXISTS;
                  }
                  else if ("keep".equals(ifExists))
                  {
                     unit.ifExists = KEEP_IF_EXISTS;
                  }
               }

               // The object to create
               PortalObjectMetaData metaData = null;

               //
               Element metaDataElt = XMLTools.getUniqueChild(deploymentElt, "portal", false);
               
               if( metaDataElt == null)
               {
                  metaDataElt = XMLTools.getUniqueChild(deploymentElt, "page", false);
                  if (metaDataElt == null)
                  {
                     metaDataElt = XMLTools.getUniqueChild(deploymentElt, "window", false);
                     if (metaDataElt == null)
                     {
                        metaDataElt = XMLTools.getUniqueChild(deploymentElt, "context", false);
                     }
                  }
               }
               if (metaDataElt != null)
               {
                  metaData = PortalObjectMetaData.buildMetaData(contentProvicerRegistry, metaDataElt);
               }
               else
               {
                  log.debug("Instances element in -object.xml is not supported anymore");
               }

               //
               if (metaData != null)
               {
                  unit.metaData = metaData;
                  units.add(unit);
               }
            }
            
            // Create all objects
            for (Unit unit : units)
            {

               if (unit.metaData instanceof PortalMetaData)
               {
                   
                   createSpace( (PortalMetaData) unit.metaData);
               }
            }           
            
            
        } catch(Exception e)    {
            log.error(e);
        }
        
    }
    
    /** A unit of deployment in the deployment descriptor. */
    protected static class Unit
    {
       /** The strategy to use when the root object already exists. */
       protected int ifExists;

       /** The parent ref. */
       protected PortalObjectId parentRef;

       /** Meta data of the deployed portal object. */
       protected Object metaData;

       /** The handle of the deployed object if not null. */
       protected PortalObjectId ref;

       public String toString()
       {
          StringBuffer buffer = new StringBuffer("Unit[::ifExists=" + ifExists);
          buffer.append(":parentRef=").append(parentRef);
          buffer.append(":Metadata=").append(metaData).append(":ref=").append(ref).append("]");
          return buffer.toString();
       }
    }





}
