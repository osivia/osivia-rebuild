package org.osivia.portal.core.ha;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.osivia.portal.api.ha.IHAService;
import org.osivia.portal.api.oauth2.IOAuth2Service;
import org.osivia.portal.core.cache.services.CacheService;
import org.springframework.stereotype.Service;

@Service(IHAService.MBEAN_NAME)
public class HAService implements IHAService{

    private static final String INIT_PARAMETERS = "init-parameters";
    JChannel channel = null ;
    Long portalParametersTs = 0L;
    
    
    protected static final Log logger = LogFactory.getLog(HAService.class);
    
    @PostConstruct
    private void init()  {
         try {
            channel = new JChannel();
            
            
            channel.setReceiver(new ReceiverAdapter() {
                public void receive(Message msg) {
                    String s = new String( msg.getBuffer());
                    if( !msg.getSrc().equals(channel.getAddress()))  {
                        if( s.equals(INIT_PARAMETERS))  {
                            logger.info("receive init parameters");
                            portalParametersTs = System.currentTimeMillis();
                        }
                    }
                }
                public void viewAccepted(View view) {
                    
                }
            });
            
            
            channel.connect("osivia-cluster");     
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @PreDestroy
    private void close()  {
        if( channel != null)
            channel.close();
    }
  
    private void sendMsg( String sInfos)  {
        Message msg = new Message();
        msg.setBuffer(sInfos.getBytes());
        
        try {
            channel.send(msg);
        } catch (Exception e) {
            throw new RuntimeException( e);
        }
        
    }
    
    @Override
    public boolean isMaster() {
        View view = channel.getView();
        Address address = view.getMembers().get(0);
        if (address.equals(channel.getAddress())) {
            return true;
        }   else    {
            return false;
        }
    }

    @Override
    public void initPortalParameters() {
        portalParametersTs = System.currentTimeMillis();
        logger.info("Send init parameters");
        sendMsg(INIT_PARAMETERS);
     }

    @Override
    public boolean checkIfPortalParametersReloaded(long savedTS) {
        
        return savedTS > portalParametersTs;
    }

}
