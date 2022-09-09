package org.osivia.portal.kernel.tomcat;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.HostConfig;

public class CustomTomcatHost extends StandardHost {

    public CustomTomcatHost() {
        super();
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        if (listener instanceof HostConfig) {
            listener = new OrderedHostConfig();
        }
        super.addLifecycleListener(listener);
    }
}