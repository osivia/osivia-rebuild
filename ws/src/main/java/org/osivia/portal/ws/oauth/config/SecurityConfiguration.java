package org.osivia.portal.ws.oauth.config;

import org.osivia.portal.ws.oauth.authentication.IPortalAuthenticationProvider;
import org.osivia.portal.ws.oauth.authentication.PortalAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    IPortalAuthenticationProvider authProvider;
//    
//    @Autowired
//    PortalUserDetailService userDetailService;    
    
      
//  @Autowired
//  public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
//      auth.userDetailsService(userDetailService);
//  }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/webjars/**", "/images/**", "/oauth/uncache_approvals", "/oauth/cache_approvals",  "/js/**",   "/html/**");
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(
      AuthenticationManagerBuilder auth) throws Exception {
  
        // Comment to return to sample users
        //auth.authenticationProvider(authProvider);
     
     }
    
    
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        
        // @formatter:off
             http.sessionManagement().sessionFixation().none().and()
            .authorizeRequests()
                .antMatchers("/login.jsp").permitAll()
                .and()
            .exceptionHandling()
                .accessDeniedPage("/login.jsp?authorization_error=true")
                .and()
            // TODO: put CSRF protection back into this endpoint
            .csrf()
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
                .disable()
             .csrf()
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/token"))
                .disable()            
            .logout()
            	.logoutUrl("/logout")
                .logoutSuccessUrl("/login.jsp")
                .and()
            .formLogin()
            	.loginProcessingUrl("/login")
                .failureUrl("/login.jsp?authentication_error=true")
                .loginPage("/login.jsp")
                ;
                
        // @formatter:on
    }
}
