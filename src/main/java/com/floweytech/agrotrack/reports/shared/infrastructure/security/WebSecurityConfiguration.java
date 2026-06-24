package com.floweytech.agrotrack.reports.shared.infrastructure.security;

import com.floweytech.agrotrack.reports.shared.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private final BearerTokenService bearerTokenService;

    public WebSecurityConfiguration(BearerTokenService bearerTokenService) {
        this.bearerTokenService = bearerTokenService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(bearerTokenService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:5173",
                "https://agrotrack-web-app.netlify.app"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Filtro que lee el JWT del header Authorization, valida la firma
     * y carga la identidad en el SecurityContext para que el resto del
     * servicio pueda usarla (igual que BearerAuthorizationRequestFilter del monolito).
     */
    @Component
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

        private final BearerTokenService bearerTokenService;

        public JwtAuthenticationFilter(BearerTokenService bearerTokenService) {
            this.bearerTokenService = bearerTokenService;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            try {
                String token = bearerTokenService.getBearerTokenFrom(request);

                if (StringUtils.hasText(token) && bearerTokenService.validateToken(token)) {
                    String username = bearerTokenService.getUsernameFromToken(token);
                    String role     = bearerTokenService.getRoleFromToken(token);

                    var authorities = role != null
                            ? List.of(new SimpleGrantedAuthority(role))
                            : List.<SimpleGrantedAuthority>of();

                    var authentication = new UsernamePasswordAuthenticationToken(
                            username, null, authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                LOGGER.warn("JWT authentication failed: {}", e.getMessage());
            }

            filterChain.doFilter(request, response);
        }
    }
}