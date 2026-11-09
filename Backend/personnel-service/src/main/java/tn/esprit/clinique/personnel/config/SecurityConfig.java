package tn.esprit.clinique.personnel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Resource server OAuth2 : chaque requête doit porter un JWT émis par Keycloak (realm « clinique »).
 * Les rôles du realm (realm_access.roles) deviennent des autorités ROLE_xxx utilisables avec @PreAuthorize.
 * clinique.security.enabled=false (défaut en local) désactive le contrôle pour faciliter le développement.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${clinique.security.enabled:true}")
    private boolean securityEnabled;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        if (!securityEnabled) {
            http.authorizeHttpRequests(a -> a.anyRequest().permitAll());
            return http.build();
        }
        http.authorizeHttpRequests(a -> a
                .requestMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**", "/error").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(keycloakConverter())));
        return http.build();
    }

    private Converter<Jwt, AbstractAuthenticationToken> keycloakConverter() {
        return jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            @SuppressWarnings("unchecked")
            Collection<String> roles = realmAccess == null ? List.of() : (Collection<String>) realmAccess.getOrDefault("roles", List.of());
            var authorities = roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
            return new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString("preferred_username"));
        };
    }
}
