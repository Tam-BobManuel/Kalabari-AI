package com.example.nlp.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.Set;

@Configuration
public class SupabaseJwtDecoder {

    @Value("${supabase.jwt.issuer}")
    private String issuer;

    @Bean
    public JwtDecoder jwtDecoder() {
        var decoder = NimbusJwtDecoder.withJwkSetUri(issuer + "/.well-known/jwks.json").build();
        decoder.setJwtValidator(new SupabaseJwtValidator(Set.of(issuer)));
        return decoder;
    }
}
