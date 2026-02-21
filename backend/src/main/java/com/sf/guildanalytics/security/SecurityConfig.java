package com.sf.guildanalytics.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt with strength 12 — good balance between security and performance.
        // Argon2 would be stronger but requires spring-security-crypto >= 5.8 with argon2-jvm dependency.
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF: Disabled because this API is stateless (JWT-based, no session cookies).
            // If you switch to HttpOnly session cookies, re-enable CSRF.
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless sessions — no server-side session state
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // SECURITY: HTTP Security Headers
            .headers(headers -> headers
                // Prevent clickjacking attacks
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                // Prevent MIME-type sniffing
                .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::disable)
                // HSTS: force HTTPS for 1 year (only effective when served over HTTPS)
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                    .preload(true)
                )
                // Referrer Policy: don't leak URL info to third parties
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
                // Content Security Policy: restrict resource loading
                // Adjust script-src/style-src to match your frontend's actual sources
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        "default-src 'self'; " +
                        "script-src 'self'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data:; " +
                        "font-src 'self'; " +
                        "connect-src 'self'; " +
                        "frame-ancestors 'none'"
                    )
                )
            )

            // Route authorization
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/guilds", "/api/guilds/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/players", "/api/players/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/snapshots", "/api/snapshots/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/uploads/**").permitAll()
                .requestMatchers("/api/users/**").authenticated()
                .anyRequest().authenticated()
            )

            // CORS configuration
            // SECURITY: In production, replace localhost patterns with your actual domain
            .cors(cors -> cors.configurationSource(request -> {
                var config = new org.springframework.web.cors.CorsConfiguration();
                config.setAllowedOriginPatterns(java.util.List.of(
                    "http://localhost:*",
                    "http://127.0.0.1:*",
                    "https://sf.r3dconcrete.de"
                ));
                config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "X-Requested-With"));
                config.setExposedHeaders(java.util.List.of("Authorization"));
                config.setAllowCredentials(true);
                config.setMaxAge(3600L);
                return config;
            }));

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
