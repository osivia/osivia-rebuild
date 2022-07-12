package org.osivia.portal.core.ha;


import org.jgroups.JChannel;

public interface IChannelFactory {
    JChannel getMsgChannel();
    JChannel getMapChannel();
    

}
