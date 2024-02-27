package org.osivia.portal.core.components;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletException;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.components.IComponentService;
import org.osivia.portal.api.components.IComponentElement;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.portalobject.bridge.PortalObjectUtils;
import org.springframework.stereotype.Service;

@Service(IComponentService.MBEAN_NAME)
public class ComponentService implements IComponentService{

    /** blackList portlets **/
    Map<String,List<String>> mComponents = new ConcurrentHashMap<>();

    /** The logger. */
    protected static Log logger = LogFactory.getLog(ComponentService.class);
    
    
    @Override
    public <T> List<T> filterByComponentList(PortalControllerContext portalCtx, String componentPrefix, List<T> elementsToFilter, IComponentElement<T> idRetrieve){
        
    // Apply black list
    UniversalID hostID = PortalObjectUtils.getHostPortalID(portalCtx.getHttpServletRequest());
    String hostName;
    
    if( hostID != null) {
        hostName = hostID.getRepositoryName();
    }   else    {
        hostName= null;
    }

     
    boolean extendedMode = false;
    
    Cookie[] cookies = portalCtx.getHttpServletRequest().getCookies();
    if( cookies != null)    {
        for(int i=0;i < cookies.length; i++)    {
            Cookie cookie= cookies[i];
            if( cookie.getName().equals("osivia.admin.extendedMode")) {
                extendedMode = BooleanUtils.toBoolean(cookie.getValue());
            }
        }
    }
     
    
    if( extendedMode == false && hostName != null) {
        // Get components from properties
        List<String> componentLines;
        try {
            componentLines = getComponents(hostName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // Get prefix for filtering
        String componentPrefixFilter = componentPrefix  +".";
        
        
        // Select component by prefix
        List<String> componentsByPrefix = new ArrayList<>();
        for( String componentLine : componentLines) {
            if( componentLine.startsWith(componentPrefixFilter))    {
                componentsByPrefix.add(componentLine.substring(componentPrefixFilter.length()));
            }
        }
        
        // Filter input element
        List<T> components = new ArrayList<>();
        for( T app: elementsToFilter) {     
            if( componentsByPrefix.contains(idRetrieve.getId(app))) {
                components.add(app);
            }
        }
        
        elementsToFilter = components;
    }
    
    
    return elementsToFilter;
    }

/**
 * Get Components for a host
 * @param hostName
 * @return
 * @throws PortletException
 * @throws IOException 
 */
private List<String> getComponents(String hostName) throws PortletException, IOException {
    List<String> componentsLines = mComponents.get(hostName);
    if (componentsLines == null) {
        
            componentsLines = new ArrayList<>();
        
            // Default components 
            Properties properties = System.getProperties();
            String prefix = "osivia.portal.components";
            
            for(Entry<Object, Object> property : properties.entrySet()) {
                String key = property.getKey().toString();
                if( key.startsWith(prefix)) {
                    componentsLines.add( property.getValue().toString());
                }
            };
                      
            // Specific components
            if( hostName != null) {
               String hostPrefix = "osivia.cms.repository."+hostName+".components";
                
                for(Entry<Object, Object> property : properties.entrySet()) {
                    String key = property.getKey().toString();
                    if( key.startsWith(hostPrefix)) {
                        componentsLines.add( property.getValue().toString());
                    }
                };
            }
 
        mComponents.put(hostName, componentsLines);
    }
    return componentsLines;
}


}
