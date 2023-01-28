package org.osivia.portal.kernel.tomcat;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.catalina.startup.HostConfig;

/**
 * The portal.war must be deployed before other application
 * It's due to webapp spring context (main webapp, not portlet)
 * that must be deployed 
 * by PortalConfiguration before the other applications
 */
public class OrderedHostConfig extends HostConfig {

   
    private OrderedContextComparator contextComparator = new OrderedContextComparator("", ".war");

	@Override
    protected String[] filterAppPaths(String[] unfilteredAppPaths) {
        String[] files = super.filterAppPaths(unfilteredAppPaths);
        Arrays.sort(files, contextComparator);
        return files;
    }

  }