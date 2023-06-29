package org.osivia.portal.services.cms.repository.memory.export;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WatcherThread implements Runnable {

    private static final long DELAY = 5000L;
    private final ConfigurationImportManager manager;

    private final Log logger = LogFactory.getLog(WatcherThread.class);

    public WatcherThread(ConfigurationImportManager manager) {
        super();
        this.manager = manager;

    }



    @Override
    public void run() {
        
        logger.info("JSON watcher thread started");
        
        
        boolean end = false;
        while (!end) {
            try {
                Thread.sleep(DELAY);

                manager.parseFile();
            } catch (InterruptedException e) {
                end = true;
                logger.info("JSON watcher thread stopped");
            }  catch (Exception e) {
                logger.error("Watcher Thread :" + e.getMessage());
            }
        }
    }

}
