package org.osivia.portal.services.cms.repository.memory.export;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.services.cms.repository.memory.FileRepository;
import org.osivia.portal.services.cms.repository.memory.IConfigurationImportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;



@Service
public class ConfigurationImportManager implements IConfigurationImportManager {
    
    /** Logger. */
    private static Log logger = LogFactory.getLog(ConfigurationImportManager.class);    
    
    
    /** The watcher thread for modifications. */

    private Thread thread;
    
    @Autowired
    private ICMSServiceLocator cmsServiceLocator;
    
    @Autowired
    private CMSService cmsService;
    
    
    
    @PreDestroy
    public void stopService() throws Exception {
        
        logger.info("stop service ConfigurationImportManager");
        
        if (thread != null) {
            thread.interrupt();
        }
    }


    /**
     * {@inheritDoc}
     */

    @PostConstruct 
    public void startService() throws Exception {
        logger.info("start service ConfigurationImportManager");
        
        // Start a watcher thread
        WatcherThread watcher = new WatcherThread(this);
        thread = new Thread(watcher);
        thread.start();  
        
        
    }

    
    public void parseFile() {


        try {
            // CMS Service not yet deployed, let's do the job on next iteration
            if( cmsServiceLocator.getCMSService() == null)
                return;
            
            PortalControllerContext portalCtx = new PortalControllerContext(cmsServiceLocator.getCMSService().getPortletContext());
            
            
            CMSContext cmsContext = new CMSContext(portalCtx);    
            cmsContext.setSuperUserMode(true);
            
            
            List<NativeRepository> repositories = cmsService.getUserRepositories(cmsContext);
            
     
            for(NativeRepository repository: repositories)  {
                if( repository instanceof FileRepository)   {
                    ((FileRepository) repository).checkAndReload();
                }
            }

        } catch (Exception e) {
            logger.error("Error during file import " + e.getMessage());
        }
    }
    
}
