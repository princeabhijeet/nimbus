package dev.prince.nimbus.config;

import dev.prince.nimbus.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final NimbusAuthenticationSuccessHandler successHandler;

    private static final String ACTUATOR_ENDPOINTS = "/actuator/**";
    private static final String SWAGGER_UI = "/swagger-ui/**";
    private static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    private static final String API_DOCS = "/api-docs/**";
    private static final String V3_API_DOCS = "/v3/api-docs/**";
    private static final String AUTHENTICATION_ENDPOINTS = "/api/v1/auth/**";
    private static final String VIEW_CONTROLLER_PATH_LOGIN = "/login";
    private static final String VIEW_CONTROLLER_PATH_CSS = "/css/**";
    private static final String VIEW_CONTROLLER_PATH_JS = "/js/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // enable CORS with default configuration
                .csrf(AbstractHttpConfigurer::disable) // disable CSRF for API endpoints
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(ACTUATOR_ENDPOINTS).permitAll() // allow public access to actuator endpoints
                        .requestMatchers(SWAGGER_UI, SWAGGER_UI_HTML, API_DOCS, V3_API_DOCS).permitAll() // allow public access to Swagger/OpenAPI endpoints
                        .requestMatchers(AUTHENTICATION_ENDPOINTS).permitAll() // allow public access to authentication endpoints
                        .requestMatchers(VIEW_CONTROLLER_PATH_LOGIN, VIEW_CONTROLLER_PATH_CSS, VIEW_CONTROLLER_PATH_JS).permitAll() // allow public access to log-in and static resources
                        .anyRequest().authenticated() // require authentication for all other requests
                        //.anyRequest().permitAll() // allow all requests
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(successHandler)
                );

        return http.build();
    }

}
