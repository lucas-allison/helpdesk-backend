package com.lucas.helpdesk.config;

import com.lucas.helpdesk.security.JWTAuthenticationFilter;
import com.lucas.helpdesk.security.JWTAuthorizationFilter;
import com.lucas.helpdesk.security.JWTUtil;
import com.lucas.helpdesk.services.exceptions.JWTAccessDeniedHandler;
import com.lucas.helpdesk.services.exceptions.JWTAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final Environment env;
    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final JWTAuthenticationEntryPoint authenticationEntryPoint;
    private final JWTAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(Environment env, JWTUtil jwtUtil, UserDetailsService userDetailsService,
                          JWTAuthenticationEntryPoint authenticationEntryPoint,
                          JWTAccessDeniedHandler accessDeniedHandler) {
        this.env = env;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            return (web) -> web.ignoring()
                    .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"));
        }
        return (web) -> {};
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        if (Arrays.asList(env.getActiveProfiles()).contains("test"))
            http.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()));

        JWTAuthenticationFilter authenticationFilter = new JWTAuthenticationFilter(
                authenticationManager,
                jwtUtil
        );
        authenticationFilter.setFilterProcessesUrl("/login");
        http.addFilter(authenticationFilter);

        http.addFilterBefore(new JWTAuthorizationFilter(
                jwtUtil,
                userDetailsService
        ), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login").permitAll()
                .anyRequest()
                .authenticated());

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}