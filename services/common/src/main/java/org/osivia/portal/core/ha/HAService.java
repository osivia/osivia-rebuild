package org.osivia.portal.core.ha;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;
import org.jgroups.protocols.UDP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;
import org.osivia.portal.api.ha.ClusterMap;
import org.osivia.portal.api.ha.IHAService;
import org.springframework.stereotype.Service;

@Service(IHAService.MBEAN_NAME)
public class HAService implements IHAService{

    private static final String INIT_PARAMETERS = "init-parameters";
    JChannel msgChannel = null ;
    JChannel mapChannel = null ;
    Long portalParametersTs = 0L;
    ReplicatedHashMap<String, ClusterMap> rmh = null;
    
    
    protected static final Log logger = LogFactory.getLog(HAService.class);
    
    @PostConstruct
    private void init()  {
        String jgroupsCfgPath = System.getProperty("catalina.base")+"/conf/jgroups.xml";
         try {
             msgChannel = new JChannel(jgroupsCfgPath);
            
             msgChannel.setReceiver(new ReceiverAdapter() {
                public void receive(Message msg) {
                    String s = new String( msg.getBuffer());
                    if( !msg.getSrc().equals(msgChannel.getAddress()))  {
                        if( s.equals(INIT_PARAMETERS))  {
                            logger.info("receive init parameters");
                            portalParametersTs = System.currentTimeMillis();
                        }
                    }
                }
                public void viewAccepted(View view) {
                    
                }
            });
            
              
            msgChannel.connect("osivia-msg");     
            
            
            mapChannel = new JChannel(jgroupsCfgPath);
            
            // Add 1 to map channel
            ProtocolStack stack = mapChannel.getProtocolStack();
            Protocol transport = stack.getTransport();
            if (transport instanceof UDP) {
                int mcast_port = ((UDP) transport).getMulticastPort();
                ((UDP) transport).setMulticastPort(mcast_port+ 1);
            }
            
            mapChannel.connect("osivia-map");
            


            
            rmh=new ReplicatedHashMap<>(mapChannel);
            rmh.setBlockingUpdates(true);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @PreDestroy
    private void close()  {
        if( msgChannel != null)
            msgChannel.close();
        if( mapChannel != null)
            mapChannel.close();
    }
  
    private void sendMsg( String sInfos)  {
        Message msg = new Message();
        msg.setBuffer(sInfos.getBytes());
        
        try {
            msgChannel.send(msg);
        } catch (Exception e) {
            throw new RuntimeException( e);
        }
        
    }
    
    @Override
    public boolean isMaster() {
        View view = msgChannel.getView();
        Address address = view.getMembers().get(0);
        if (address.equals(msgChannel.getAddress())) {
            return true;
        }   else    {
            return false;
        }
    }

    @Override
    //TODO REIMPLEMENT
    public void initPortalParameters() {
        portalParametersTs = System.currentTimeMillis();
        logger.info("Send init parameters");
        sendMsg(INIT_PARAMETERS);
     }

    @Override
    public boolean checkIfPortalParametersReloaded(long savedTS) {
        return savedTS > portalParametersTs;
    }



    @Override
    public void unshareMap(String objectId) {
        rmh.remove(objectId);
   }


    @Override
    public void shareMap(String objectId, Map<String, String> object) {
        rmh.put(objectId, new ClusterMap(object));
        
    }

    @Override
    public ClusterMap getSharedMap(String objectId) {
        ClusterMap res = rmh.get(objectId);
        return res;
    }


}
