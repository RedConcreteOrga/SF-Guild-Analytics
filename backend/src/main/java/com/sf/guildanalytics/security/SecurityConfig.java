package com.sf.guildanalytics.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/guilds", "/api/guilds/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/players", "/api/players/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/snapshots", "/api/snapshots/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/uploads/**").permitAll()
                        .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().authenticated())
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOriginPatterns(java.util.List.of("http://localhost:*", "http://127.0.0.1:*"));
                    config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(java.util.List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }));

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
