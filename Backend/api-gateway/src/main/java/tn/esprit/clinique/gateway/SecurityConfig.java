package tn.esprit.clinique.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/** La Gateway rejette (401) toute requête /api/** sans JWT Keycloak valide. */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${clinique.security.enabled:true}")
    private boolean securityEnabled;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(Customizer.withDefaults());
        if (!securityEnabled) {
            return http.authorizeExchange(e -> e.anyExchange().permitAll()).build();
        }
        return http
            .authorizeExchange(e -> e
                .pathMatchers("/actuator/health", "/ws/**").permitAll()
                .anyExchange().authenticated())
            .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
            .build();
    }
}
