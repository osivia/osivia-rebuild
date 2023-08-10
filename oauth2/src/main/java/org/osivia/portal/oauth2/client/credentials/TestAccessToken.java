package org.osivia.portal.oauth2.client.credentials;

import java.time.Instant;
import java.util.Set;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;


public class TestAccessToken extends OAuth2AccessToken {
	

	private static final long serialVersionUID = -2266941938742662880L;

	public TestAccessToken(TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt,
			Set<String> scopes) {
		super(tokenType, tokenValue, issuedAt, expiresAt, scopes);
	}

}
