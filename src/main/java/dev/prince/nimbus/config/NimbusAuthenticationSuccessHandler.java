package dev.prince.nimbus.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class NimbusAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${nimbus.frontend.url}")
    private String homePageUrl ;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("Successfully authenticated user: {}. Redirecting to: {}", authentication.getName(), homePageUrl);

        // Clear authentication attributes to clean up the session
        clearAuthenticationAttributes(request);

        // Redirect to the UI (Thymeleaf or React)
        getRedirectStrategy().sendRedirect(request, response, homePageUrl);
    }

}
