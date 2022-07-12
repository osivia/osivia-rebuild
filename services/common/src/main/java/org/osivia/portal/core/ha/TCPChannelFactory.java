package org.osivia.portal.core.ha;

import org.jgroups.JChannel;
import org.jgroups.protocols.UDP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;

public class TCPChannelFactory implements IChannelFactory {

    JChannel msgChannel, mapChannel ;
    
    public TCPChannelFactory()  {
        String jgroupsMsgCfgPath = System.getProperty("catalina.base")+"/conf/jgroups-tcp-msg.xml";
        String jgroupsDataCfgPath = System.getProperty("catalina.base")+"/conf/jgroups-tcp-data.xml";
        
        try {
            msgChannel = new JChannel(jgroupsMsgCfgPath);
            msgChannel.connect("osivia-msg");  
            
            
            mapChannel = new JChannel(jgroupsDataCfgPath);
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
