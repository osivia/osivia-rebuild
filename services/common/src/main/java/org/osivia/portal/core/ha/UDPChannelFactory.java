package org.osivia.portal.core.ha;

import org.jgroups.JChannel;
import org.jgroups.protocols.UDP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;

public class UDPChannelFactory implements IChannelFactory {

    JChannel msgChannel, mapChannel ;
    
    public UDPChannelFactory()  {
        String jgroupsCfgPath = System.getProperty("catalina.base")+"/conf/jgroups-udp.xml";
        
        try {
            msgChannel = new JChannel(jgroupsCfgPath);
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
            
        }   catch( Exception e) {
            e.printStackTrace();
        }
    }       
    
    
    @Override
    public JChannel getMsgChannel() {
        return msgChannel;
    }
    
    
    @Override
    public JChannel getMapChannel() {
        return mapChannel;
    }

}
