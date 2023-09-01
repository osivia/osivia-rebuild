package org.osivia.portal.core.oauth2.client;


import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;




public class PortalGenericPrincipal extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;


    private final Object principal;

    public PortalGenericPrincipal() {
        super(AuthorityUtils.createAuthorityList("ROLE_PORTAL"));
        this.principal = "portal-generic-user";

    }

    @Override
    public Object getCredentials() {
        return "";
    }
    
    @Override
    public Object getPrincipal() {
        return this.principal;
    }

}
