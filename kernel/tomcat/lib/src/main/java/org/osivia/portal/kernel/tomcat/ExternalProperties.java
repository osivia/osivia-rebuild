package org.osivia.portal.kernel.tomcat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils.PropertySource;

public class ExternalProperties implements PropertySource   {

    private static final String CATALINA_PROPERTIES = "conf/catalina.properties";
    private static final String CATALINA_PROPERTIES_FILE_PROPERTY = "org.osivia.portal.kernel.tomcat.ExternalPropertySource.file";
    private static final Log LOGGER = LogFactory.getLog(ExternalProperties.class);
    private Properties externalProperties;

    public ExternalProperties() {
        try {
            String catalinaBase = System.getProperty("catalina.base");
            File catalinaPropertiesFile = new File(catalinaBase, CATALINA_PROPERTIES);
            if (!catalinaPropertiesFile.exists()) {
                throw new IOException("Unable to find the file " + CATALINA_PROPERTIES + " in CATALINA_BASE (" + catalinaBase + ")");
            }
            FileInputStream catalinaFileInputStream = new FileInputStream(catalinaPropertiesFile);
            Properties catalinaProperties = new Properties();
            catalinaProperties.load(catalinaFileInputStream);
            String externalPropertiesFile = catalinaProperties.getProperty(CATALINA_PROPERTIES_FILE_PROPERTY);
            if (externalPropertiesFile == null || externalPropertiesFile.isEmpty()) {
                throw new IOException("The external property file location is not set in " + CATALINA_PROPERTIES + " (expected value for " + CATALINA_PROPERTIES_FILE_PROPERTY + ")");
            }
            String fileNames[] = externalPropertiesFile.split(",");
            externalProperties = new Properties();
            for(int i=0; i< fileNames.length; i++)  {
                FileInputStream fileInputStream = new FileInputStream(fileNames[i]);
                externalProperties.load(fileInputStream);
            }
        } catch (IOException e) {
            LOGGER.fatal("Unable to read the external property file", e);
            externalProperties = null;
        }
    }

    @Override
    public String getProperty(String string) {
        if (externalProperties != null)  {
            return externalProperties.getProperty(string);
        } else {
            // If the property is not found, we return null (and Tomcat will leave the ${propertyname} )
            // NB : Tomcat uses this PropertySource for each XML file it parses (see IntrospectionUtils source code), not only on server.xml
            return null;
        }
    }
}