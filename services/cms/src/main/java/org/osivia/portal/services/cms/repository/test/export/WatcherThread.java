package org.osivia.portal.services.cms.repository.test.export;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WatcherThread implements Runnable {

    private static final long DELAY = 5000L;
    private final ConfigurationImportManager manager;
    private boolean end = false;

    private final Log logger = LogFactory.getLog(WatcherThread.class);

    public WatcherThread(ConfigurationImportManager manager) {
        super();
        this.manager = manager;

    }

    public void endThread() {
        end = true;
    }

    @Override
    public void run() {
        
        
        while (!end) {
            try {
                Thread.sleep(DELAY);

                manager.parseFile();

            } catch (Exception e) {
                logger.error("Watcher Thread :" + e.getMessage());
            }
        }
    }

}
