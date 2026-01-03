package dev.prince.nimbus.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private static final String ACTUATOR_ENDPOINTS = "/actuator/**";
    private static final String SWAGGER_UI = "/swagger-ui/**";
    private static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    private static final String API_DOCS = "/api-docs/**";
    private static final String V3_API_DOCS = "/v3/api-docs/**";

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API endpoints
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(ACTUATOR_ENDPOINTS).permitAll() // Allow public access to actuator endpoints
                        .requestMatchers(SWAGGER_UI, SWAGGER_UI_HTML, API_DOCS, V3_API_DOCS).permitAll() // Allow public access to Swagger/OpenAPI endpoints
                        .anyRequest().permitAll() // Allow all requests for now (you can change this to .authenticated() when you set up OAuth2)
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(authenticationSuccessHandler())
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler("/api/v1/mvc/user/details") {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                try {
                    String principal = authentication != null && authentication.getName() != null ? authentication.getName() : "<null>";
                    String sessionId = null;
                    if (request.getSession(false) != null) {
                        sessionId = request.getSession(false).getId();
                    }
                    log.info("OAuth2 authentication success. principal={}, sessionId={}", principal, sessionId);
                } catch (Exception e) {
                    log.error("Failed to log authentication success details: {}", e.getMessage());
                }
                super.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }
}
