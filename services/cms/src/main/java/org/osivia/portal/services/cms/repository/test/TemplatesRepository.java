package org.osivia.portal.services.cms.repository.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.ModuleRef;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.service.CMSService;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import fr.toutatice.portail.cms.producers.test.TestRepository;


public class TemplatesRepository extends UserRepositoryTestBase  {

      
      public TemplatesRepository(SharedRepositoryKey repositoryKey, String userName) {
          super(repositoryKey, null, userName);
      }
    /**
     * {@inheritDoc}
     */


    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addTemplateSpace(String id, List<String> children) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        Map<String,String> editionProperties = new ConcurrentHashMap<>();
        editionProperties.put("osivia.hideTitle", "1");
        ModuleRef edition = new ModuleRef("edition" , "top", "EditionInstance", editionProperties); 
        modules.add(edition);  
        Map<String,String> navProperties = new ConcurrentHashMap<>(); 
        navProperties.put("osivia.hideTitle", "1");        
        ModuleRef nav = new ModuleRef("nav" , "nav",  "MenuInstance", navProperties); 
        
        modules.add(nav);     
        MemoryRepositorySpace space = new MemoryRepositorySpace(this,id, id,null,children, properties, modules);
        addDocument(id, space);
    }

    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addTemplatePage(String id, String name, String parentId, String spaceId, List<String> children, List<ModuleRef> modules) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);

        
        MemoryRepositoryPage page = new MemoryRepositoryPage(this,id, name, null, parentId, spaceId, children, properties, modules);
        page.setInheritedRegions(Arrays.asList("top","nav"));

        addDocument(id, page);
    }

    
    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addTemplateNxSpace(String id, String name, String parentId, String spaceId) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "NxSpace." + id);
        properties.put(ThemeConstants.PORTAL_PROP_THEME, "cloud-ens-charte");
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        List<String> children = new ArrayList<String>();
        
        MemoryRepositoryPage page = new MemoryRepositoryPage(this,id, name, null, parentId, spaceId, children, properties, modules);
        //page.setInheritedRegions(Arrays.asList("top"));
        
        Map<String,String> navProperties = new ConcurrentHashMap<>(); 
        navProperties.put("osivia.hideTitle", "1");        
        ModuleRef nav = new ModuleRef("nav" , "nav",  "toutatice-portail-cms-nuxeo-publishMenuPortletInstance", navProperties); 
        
        modules.add(nav);     


        addDocument(id, page);
    }
    
    
    /**
     * Adds the space.
     *
     * @param id the id
     * @param properties the properties
     */
    private void addTemplatePageWithoutMenu(String id, String name, String parentId, String spaceId, List<String> children, List<ModuleRef> modules) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Space." + id);

        
        MemoryRepositoryPage page = new MemoryRepositoryPage(this,id, name, null, parentId, spaceId, children, properties, modules);
        page.setInheritedRegions(Arrays.asList("top"));

        addDocument(id, page);
    }

    protected void pageA(String id, String name, String parentId, String spaceId, List<String> children) {

        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        Map<String,String> aProperties = new ConcurrentHashMap<>();       
        ModuleRef moduleA = new ModuleRef("winA-" + id, "col-2",  "SampleInstance", aProperties);
        Map<String,String> bProperties = new ConcurrentHashMap<>();          
        ModuleRef moduleB = new ModuleRef("winB-" + id, "col-2",  "ReactPortletInstance", bProperties);


        modules.add(moduleA);
        modules.add(moduleB);
        
        Map<String,String> cProperties = new ConcurrentHashMap<>();   
        ModuleRef indexContent = new ModuleRef("index-" + id, "col-2", "IndexContentInstance", cProperties);
        modules.add(indexContent);
        
        Map<String,String> vProperties = new ConcurrentHashMap<>();    
        //vProperties.put("osivia.cms.uri","/default-domain/UserWorkspaces/a/d/m/admin/documents/facture-copie-docx");
        vProperties.put("osivia.cms.uri","/default-domain/UserWorkspaces/d/e/m/demo/documents/etat-des-lieux-osivia");
                
        ModuleRef viewContent = new ModuleRef("view-" + id, "col-2", "toutatice-portail-cms-nuxeo-viewDocumentPortletInstance", vProperties);
        modules.add(viewContent);       
        


        addTemplatePage(id, name, parentId, spaceId, children, modules);
    }
    
    
    protected void pageA1(String id, String name, String parentId, String spaceId, List<String> children) {

        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        Map<String,String> cProperties = new ConcurrentHashMap<>();    
        ModuleRef moduleC = new ModuleRef("winC-" + id, "col-2",  "ContentInstance", cProperties);

        modules.add(moduleC);

        addTemplatePage(id, name, parentId, spaceId, children, modules);
    }


    protected void pageB(String id, String name, String parentId, String spaceId, List<String> children) {

        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        Map<String,String> bProperties = new ConcurrentHashMap<>();    
        ModuleRef moduleB = new ModuleRef("winB-" + id, "col-2", "SampleInstance", bProperties);
        ModuleRef indexContent = new ModuleRef("index-" + id, "col-2", "IndexContentInstance", bProperties);
        modules.add(moduleB);
        modules.add(indexContent);

        addTemplatePage(id, name, parentId, spaceId, children, modules);
    }

    protected void pageC(String id, String name, String parentId, String spaceId, List<String> children) {

        List<ModuleRef> modules = new ArrayList<ModuleRef>();

        addTemplatePageWithoutMenu(id, name, parentId, spaceId, children, modules);
    }
    

    protected void createTemplateSpace() {
        // PortalA
        pageA("ID_PAGE_A", "pageA", "portalA", "portalA", new ArrayList<String>(Arrays.asList("ID_TEMPLATE_WORKSPACE")));
        pageA1("ID_TEMPLATE_WORKSPACE", "workspace", "ID_PAGE_A", "portalA", new ArrayList<String>());
        pageB("ID_TEMPLATE_SITE", "site", "portalA", "portalA", new ArrayList<String>());
        pageC("ID_EMPTY", "empty", "portalA", "portalA", new ArrayList<String>());
        addTemplateNxSpace("ID_TEMPLATE_NX_WORKSPACE", "nxworkspace", "portalA", "portalA");
        List<String> portalChildren = new ArrayList<String>();
        portalChildren.add("ID_PAGE_A");
        portalChildren.add("ID_TEMPLATE_SITE");
        portalChildren.add("ID_EMPTY");
        portalChildren.add("ID_TEMPLATE_NX_WORKSPACE");
        addTemplateSpace("portalA", portalChildren);
        
         
        

    }
    
    protected void createUtilsSpace() {
        
        // osivia-utils
        List<ModuleRef> modules = new ArrayList<ModuleRef>();
        List<String> children = new ArrayList<String>();
        
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        properties.put("dc:title", "Modal");
        properties.put("layout.id", "osivia-modal");
        

        MemoryRepositoryPage page = new MemoryRepositoryPage(this,"OSIVIA_PAGE_MODAL", "modal", null, "OSIVIA_PORTAL_UTILS", "OSIVIA_PORTAL_UTILS", children, properties, modules);
        addDocument("OSIVIA_PAGE_MODAL", page);
        
        List<String> portalChildren = new ArrayList<String>();
        portalChildren.add("OSIVIA_PAGE_MODAL");
        Map<String, Object> portalProperties = new ConcurrentHashMap<String, Object>();
        portalProperties.put("dc:title", "Utils" );
  
        MemoryRepositorySpace space = new MemoryRepositorySpace(this,"OSIVIA_PORTAL_UTILS", "OSIVIA_PORTAL_UTILS",null,portalChildren, portalProperties, new ArrayList<ModuleRef>());
        addDocument("OSIVIA_PORTAL_UTILS", space);
     }

@Override
    public void addEmptyPage(String id, String name, String parentId) throws CMSException {
        RepositoryDocument parent = getSharedDocument(parentId);
        addTemplatePage(id, name, parentId, parent.getSpaceId().getInternalID(), new ArrayList<String>(), new ArrayList<ModuleRef>());
    }
    


    protected void initDocuments() {
        createTemplateSpace();
        createUtilsSpace();
    }


    @Override
    public boolean supportPageEdition() {
        return true;
    }


}
