package org.osivia.portal.core.blacklist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletException;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.blacklist.IBlackListService;
import org.osivia.portal.api.blacklist.IBlackListableElement;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.portalobject.bridge.PortalObjectUtils;

import org.springframework.stereotype.Service;

@Service(IBlackListService.MBEAN_NAME)
public class BlackListService implements IBlackListService{

    /** blackList portlets **/
    Map<String,List<String>> mBlackListedLines = new ConcurrentHashMap<>();

    /** The logger. */
    protected static Log logger = LogFactory.getLog(BlackListService.class);
    
    
    @Override
    public <T> List<T> filterByBlacklist(PortalControllerContext portalCtx, String blackListPrefix, List<T> elementsToFilter, IBlackListableElement<T> idRetrieve){
    // Apply black list
    UniversalID hostID = PortalObjectUtils.getHostPortalID(portalCtx.getHttpServletRequest());
    String hostName = hostID.getRepositoryName();

     
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
     
    
    if( extendedMode == false) {
        // Get lines from file
        List<String> blackListLines;
        try {
            blackListLines = getBlacklistedLines(hostName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // Get prefix for filtering
        String blackListPrefixFilter = blackListPrefix  +".";
        
        
        // Filtering lines
        List<String> blackListByPrefix = new ArrayList<>();
        for( String blackListLine : blackListLines) {
            if( blackListLine.startsWith(blackListPrefixFilter))    {
                blackListByPrefix.add(blackListLine.substring(blackListPrefixFilter.length()));
            }
        }
        
        // filtering input element
        List<T> blackListedElements = new ArrayList<>();
        for( T app: elementsToFilter) {     
            if( blackListByPrefix.contains(idRetrieve.getId(app))) {
                blackListedElements.add(app);
            }
        }
        
        elementsToFilter = blackListedElements;

    }
    return elementsToFilter;
    }

/**
 * Get blacklisted portlets for a host
 * @param hostName
 * @return
 * @throws PortletException
 * @throws IOException 
 */
private List<String> getBlacklistedLines(String hostName) throws PortletException, IOException {
    List<String> blackListedLines = mBlackListedLines.get(hostName);
    if (blackListedLines == null) {
        try {
            String extension = hostName;
            if( extension == null)  {
                extension = "";
            }
            else    {
                extension = "-" + extension;
            }
            String serverBaseConfiguration = System.getProperty("custom.params.path");
            blackListedLines = Files.readAllLines(Paths.get(serverBaseConfiguration + "blacklist" + extension + ".txt"));
        } catch (IOException e) {
            if (e instanceof NoSuchFileException) {
                // No blacklist
                blackListedLines = new ArrayList<String>();
            } else {
                throw (e);
            }
        }
        mBlackListedLines.put(hostName, blackListedLines);
    }
    return blackListedLines;
}


}
