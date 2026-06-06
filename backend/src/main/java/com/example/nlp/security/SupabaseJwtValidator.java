package com.example.nlp.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

public class SupabaseJwtValidator implements OAuth2TokenValidator<Jwt> {

    private final Set<String> allowedIssuers;

    public SupabaseJwtValidator(Set<String> allowedIssuers) {
        this.allowedIssuers = allowedIssuers;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        var issuer = token.getIssuer();
        if (issuer == null || !allowedIssuers.contains(issuer.toString())) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_issuer", "Invalid issuer: " + issuer, null)
            );
        }

        var audience = token.getAudience();
        if (audience == null || !audience.contains("authenticated")) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_audience", "Invalid audience", null)
            );
        }

        return OAuth2TokenValidatorResult.success();
    }
}
