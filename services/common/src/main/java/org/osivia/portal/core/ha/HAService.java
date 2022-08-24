package org.osivia.portal.core.ha;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.UpdateInformations;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.ha.ClusterMap;
import org.osivia.portal.api.ha.IHAService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Service(IHAService.MBEAN_NAME)
public class HAService implements IHAService{

    private static final String INIT_PARAMETERS = "init-parameters/";
    private static final String CMS_NOTIFICATION = "cms-notification/";
    
    
    
    JChannel msgChannel = null ;
    JChannel mapChannel = null ;
    Long portalParametersTs = 0L;
    ReplicatedHashMap<String, ClusterMap> rmh = null;
    
    
    @Autowired
    private ICMSServiceLocator cmsServiceLocator;
    
    @Autowired
    private CMSService cmsService;
    
    protected static final Log logger = LogFactory.getLog(HAService.class);
    
    
    
    
    @PostConstruct
    private void init()  {
        
        IChannelFactory channelFactory;
        
        if( StringUtils.equalsIgnoreCase(System.getProperty("osivia.portal.cluster.protocol"), "tcp"))   {
            channelFactory =  new TCPChannelFactory();
        }
        else    {
            channelFactory = new UDPChannelFactory();
        }
        
         try {
             msgChannel = channelFactory.getMsgChannel();
             msgChannel.setReceiver(new ReceiverAdapter() {
                public void receive(Message msg) {
                    String s = new String( msg.getBuffer());
                    
                    
                    if( !msg.getSrc().equals(msgChannel.getAddress()))  {
                        if( s.startsWith(INIT_PARAMETERS))  {
                            if( logger.isDebugEnabled())    {
                                logger.debug("receive init parameters");
                            }
                            portalParametersTs = System.currentTimeMillis();
                        }
                        
                        if( s.startsWith(CMS_NOTIFICATION))  {
                            if( logger.isDebugEnabled())    {
                                logger.debug("receive cms notification");
                            }
                            try {
                                CMSEventBean cmsEventInfos = new ObjectMapper().readValue(s.substring(CMS_NOTIFICATION.length()), CMSEventBean.class);
                                UpdateInformations informations = cmsEventInfos.toUpdate();
                                
                                // CMS Service not yet deployed
                                if( cmsServiceLocator.getCMSService() == null)
                                    return;
                                
                                PortalControllerContext portalCtx = new PortalControllerContext(cmsServiceLocator.getCMSService().getPortletContext());
                                CMSContext cmsContext = new CMSContext(portalCtx);    
                                cmsContext.setSuperUserMode(true);
                                
                                CMSSession session = cmsService.getCMSSession(cmsContext);
                                session.handleUpdate(informations);
                                
                                
                            } catch (Exception e) {
                                logger.error(s, e);
                            }
                            
                        }
                    }
                    
                    
                }
                public void viewAccepted(View view) {
                    
                }
            });
            
              
               
           mapChannel = channelFactory.getMapChannel();
            
            
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
  
    private void sendMsg( String name, String value)  {
        Message msg = new Message();
        String buf = name;
        if( value != null)
            buf = buf+value;
        
        msg.setBuffer(buf.getBytes());
        
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
    public void initPortalParameters() {
        portalParametersTs = System.currentTimeMillis();
        logger.info("Send init parameters");
        sendMsg(INIT_PARAMETERS, null);
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

    @Override
    public void notifyCMSEvent(UpdateInformations updateInfos) {
        try {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String value = ow.writeValueAsString(CMSEventBean.fromUpdate(updateInfos) );
        
        sendMsg(CMS_NOTIFICATION, value);
        
        
        } catch (Exception e) {
            throw new RuntimeException( e);
        }
        
    }


}
