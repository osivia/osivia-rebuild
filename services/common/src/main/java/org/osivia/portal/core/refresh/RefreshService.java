package org.osivia.portal.core.refresh;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.oauth2.client.IOAuth2ClientService;
import org.osivia.portal.api.refresh.IRefreshService;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.tokens.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
public class RefreshService implements IRefreshService {
    
    /** Logger. */
    private static final Log logger = LogFactory.getLog(TokenService.class);

    @Autowired IOAuth2ClientService oAuth2Service;
    
    private static final String SAFRAN_HUB_URI_KEY = "safran.safran-hub.uri";
    public static final String SAFRAN_INSTANCE_NAME = "elen-safran-client-instance";

    
    
    
    private URL safranHubUrl;

    
    @PostConstruct
    public void RefreshServiceConstruct() {
        try {
            this.safranHubUrl = new URL(System.getProperty(SAFRAN_HUB_URI_KEY));
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
        }

    }
    
    
    @Override
    public void applyRefreshStrategy(PortalControllerContext portalCtx, String strategyName) throws PortalException {
        
        
           if( IRefreshService.REFRESH_STRATEGY_SAFRAN.equals(strategyName))    {
               
               // SAFRAN synchronisation command
               
               IOAuth2ClientService oAuth2Service = Locator.getService(IOAuth2ClientService.MBEAN_NAME, IOAuth2ClientService.class);
               RestTemplate template =  oAuth2Service.getPortalClientCredentialRestTemplate();
               String operationPath = "/api/v1/catalogues/"+portalCtx.getRequest().getRemoteUser()+"/sync?force=true";

               try {
                   String file = this.safranHubUrl.getFile() + operationPath;
                   URL url = new URL(this.safranHubUrl.getProtocol(), this.safranHubUrl.getHost(), this.safranHubUrl.getPort(), file);
                   URI uri = url.toURI();

                   HttpHeaders httpHeaders = new HttpHeaders();
                   httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                   HttpEntity<?> httpEntity = new HttpEntity<>(null, httpHeaders);
                   template.exchange(uri,  HttpMethod.POST, httpEntity, String.class);
                   
                   
                   // Recompute all safran windows
                   ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalCtx);
                   controllerContext.setAttribute(ControllerCommand.SESSION_SCOPE,"osivia.refresh.instance."+ SAFRAN_INSTANCE_NAME, System.currentTimeMillis());

                   
               } catch (HttpClientErrorException e) {
                   logger.warn(String.format("Http error on uri %s with code %s",operationPath, e.getMessage()));
               } catch (MalformedURLException | URISyntaxException e) {
                   logger.error(e.getMessage());
               } catch (Exception e) {
                   throw new PortalException(e);
               }
               
               
           }
    }

}
