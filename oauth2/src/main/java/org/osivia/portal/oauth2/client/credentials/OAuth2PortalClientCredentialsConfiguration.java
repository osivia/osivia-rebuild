package org.osivia.portal.oauth2.client.credentials;

import java.io.IOException;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Configuration
public class OAuth2PortalClientCredentialsConfiguration {

    private static String OAUTH2_ACCESS_CLIENT_SECRET_KEY = "portal.security.oauth2.client.client-secret";
    private static String OAUTH2_ACCESS_CLIENT_ID_KEY = "portal.security.oauth2.client.client-id";
    private static String OAUTH2_ACCESS_TOKEN_URI_KEY = "portal.security.oauth2.client.token-uri";
    private static String OAUTH2_SKIP_AUTHENTICATION = "portal.security.oauth2.skip";
    private static String OAUTH2_EXPIRATION_DELAY = "portal.security.oauth2.tokens.expiration";

    private final int minExpirationDelay;

    private final Log log;

    public OAuth2PortalClientCredentialsConfiguration() {
        this.log = LogFactory.getLog(this.getClass());

        // force expiration delay (tests only)
        minExpirationDelay = Integer.getInteger(OAUTH2_EXPIRATION_DELAY, -1);
    }

    @Bean
    OAuth2AuthorizedClientManager authorizeClientManager(ClientRegistrationRepository clients, OAuth2AuthorizedClientRepository authorizedClients) {

        DefaultOAuth2AuthorizedClientManager manager = new DefaultOAuth2AuthorizedClientManager(clients, authorizedClients);

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();
        manager.setAuthorizedClientProvider(authorizedClientProvider);
        return manager;
    }

    @Component
    public class OAuth2AuthorizedClientInterceptor implements ClientHttpRequestInterceptor {

        OAuth2AuthorizedClientManager manager;

        public OAuth2AuthorizedClientInterceptor(OAuth2AuthorizedClientManager manager) {
            this.manager = manager;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

            if ( ! "true".equals(System.getProperty(OAUTH2_SKIP_AUTHENTICATION))) {

                Authentication principal = new PortalGenericPrincipal();

                RequestAttributes attrs = RequestContextHolder.getRequestAttributes();

                HttpServletRequest servletRequest = (HttpServletRequest) attrs.getAttribute(Constants.PORTLET_ATTR_HTTP_REQUEST,
                        RequestAttributes.SCOPE_REQUEST);
                HttpServletResponse servletResponse = (HttpServletResponse) attrs.getAttribute(Constants.PORTLET_ATTR_HTTP_RESPONSE,
                        RequestAttributes.SCOPE_REQUEST);

                OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("portal-client").principal(principal)
                        .attribute(HttpServletRequest.class.getName(), servletRequest).attribute(HttpServletResponse.class.getName(), servletResponse).build();

                OAuth2AuthorizedClient authorizedClient = this.manager.authorize(authorizeRequest);

                HttpHeaders headers = request.getHeaders();
                headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());

                /* Test mode */
                
                if (minExpirationDelay != -1) {

                    if (!(authorizedClient.getAccessToken() instanceof TestAccessToken)) {

                        log.info("generate new token");

                        OAuth2AccessToken existingToken = authorizedClient.getAccessToken();

                        // + 60.000 because of test in {@link ClientCredentialsOAuth2AuthorizedClientProvider }
                        Instant expiration = Instant.ofEpochMilli(System.currentTimeMillis() + minExpirationDelay + 60000);

                        TestAccessToken expiringAccessToken = new TestAccessToken(existingToken.getTokenType(), existingToken.getTokenValue(),
                                existingToken.getIssuedAt(), expiration, existingToken.getScopes());
                        OAuth2AuthorizedClient expiringAuthorizedClient = new OAuth2AuthorizedClient(authorizedClient.getClientRegistration(),
                                authorizedClient.getPrincipalName(), expiringAccessToken);

                        authorizedClientRepository.saveAuthorizedClient(expiringAuthorizedClient, principal, servletRequest, servletResponse);

                    }
                }

                log.info("token " + authorizedClient.getAccessToken().getTokenValue());
            }

            return execution.execute(request, body);
        }
    }

    @Bean
    public RestTemplate rest(OAuth2AuthorizedClientInterceptor interceptor) {
        RestTemplate rest = new RestTemplate();
        rest.getInterceptors().add(interceptor);
        return rest;
    }

    @Bean
    public OAuth2AuthorizedClientRepository getAuth2ClientRepository() {
        return new OAuth2SharedAuthorizedClientRepository();
    }

    @Autowired
    OAuth2AuthorizedClientRepository authorizedClientRepository;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.getClientRegistration());
    }

    private ClientRegistration getClientRegistration() {
        return ClientRegistration.withRegistrationId("portal-client").clientId(System.getProperty(OAUTH2_ACCESS_CLIENT_ID_KEY))
                .clientSecret(System.getProperty(OAUTH2_ACCESS_CLIENT_SECRET_KEY)).authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri(System.getProperty(OAUTH2_ACCESS_TOKEN_URI_KEY))
                .build();
    }

}
