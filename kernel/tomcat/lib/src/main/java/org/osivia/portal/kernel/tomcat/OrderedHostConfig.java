package org.osivia.portal.kernel.tomcat;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.catalina.startup.HostConfig;

/**
 * The portal.war must be deployed before other application
 * It's due to webapp spring context that must be deployed 
 * by PortalConfiguration before the other applications
 */
public class OrderedHostConfig extends HostConfig {

    /** The portl webapp name */
    private static final String PORTAL_WAR = "portail.war";
    private static final String ANNUAIRE_WAR = "toutatice-annuaire-custom.war";

    @Override
    protected String[] filterAppPaths(String[] unfilteredAppPaths) {
        String[] files = super.filterAppPaths(unfilteredAppPaths);
        Arrays.sort(files, compare());
        return files;
    }

    private Comparator<String> compare() {
        return (o1, o2) -> {
        	
        	return o1.compareTo(o2);
        	
        	// Moved to TomcatServletContainerContext
 /*       	
            if( PORTAL_WAR.equals(o1))
                return -10;
            else if( PORTAL_WAR.equals(o2))
                return 10;
            else if( ANNUAIRE_WAR.equals(o1))
                return -9;
            else if( ANNUAIRE_WAR.equals(o2))
                return 9;            
            else
                return o1.compareTo(o2);
                */
    };
    }
}