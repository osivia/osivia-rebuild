package org.osivia.portal.ws;

import java.io.FileInputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;




/**
 * Services Rest associés au Drive
 * 
 * @author Jean-Sébastien
 */
@RestController
public class DriveRestController {
    
    /** CMS service. */
    @Autowired
    private CMSService cmsService;


    /**
     * Get content datas for the specified id
     * 
     * @param request
     * @param response
     * @param id
     * @param principal
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/Drive.content", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)

    public Map<String, Object> getContent(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "repository", required = false) String repository, @RequestParam(value = "id", required = false) String id,
            Principal principal) throws Exception {
        WSPortalControllerContext ctx = new WSPortalControllerContext(request, response, principal);
        CMSController ctrl = new CMSController(ctx);         
        Document doc = cmsService.getDocument(ctrl.getCMSContext(), new UniversalID(repository,id));
        
        Map<String, Object> contents = new LinkedHashMap<>();
        contents.put("type", doc.getType());
        contents.put("repository", doc.getId().getRepositoryName());
        contents.put("id", doc.getId().getInternalID());
        contents.put("title", doc.getTitle());
        return contents;

    }

    
    
   

}
