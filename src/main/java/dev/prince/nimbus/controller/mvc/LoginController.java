package dev.prince.nimbus.controller.mvc;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "LoginMvcController")
public class LoginController {

    private static final String EXISTS_KEY = "exists";
    private static final String OAUTH_SESSION_EMAIL = "oauth_login_email";

    // This endpoint is called from MVC when user submits email for GitHub login
    @PostMapping("/api/v1/mvc/auth/validate-email")
    public Map<String, Object> validateEmail(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String email = payload.get("email");
        log.info("Validating email for login (via REST UserController): {}", email);

        // Build base URL from current request so we can call the app's own REST API
        String base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        // Build an encoded URI for the email path segment to handle characters like '@'
        URI uri = UriComponentsBuilder.fromUriString(base)
                .path("/api/v1/users/email/{email}")
                .buildAndExpand(email)
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(uri, String.class);
            // Consider not only the 2xx status but also that the body contains JSON (non-empty) to determine existence
            if (resp.getStatusCode().is2xxSuccessful()) {
                String body = resp.getBody();
                boolean exists = body != null && !body.trim().isEmpty() && !"null".equalsIgnoreCase(body.trim());

                if (exists) {
                    // store pre-validated email into session to be used during OAuth callback
                    try {
                        request.getSession(true).setAttribute(OAUTH_SESSION_EMAIL, email);
                    } catch (Exception e) {
                        log.warn("Unable to store oauth session email: {}", e.getMessage());
                    }
                } else {
                    // remove any previously set session email
                    try {
                        var s = request.getSession(false);
                        if (s != null) s.removeAttribute(OAUTH_SESSION_EMAIL);
                    } catch (Exception ignored) { }
                }

                return Map.of(EXISTS_KEY, exists);
            }
            return Map.of(EXISTS_KEY, false);
        } catch (HttpClientErrorException.NotFound nf) {
            // ensure session attribute removed for not-found
            try { var s = request.getSession(false); if (s != null) s.removeAttribute(OAUTH_SESSION_EMAIL);} catch (Exception ignored) {}
            return Map.of(EXISTS_KEY, false);
        } catch (Exception ex) {
            log.warn("Error while calling user API for email {}: {}", email, ex.getMessage());
            try { var s = request.getSession(false); if (s != null) s.removeAttribute(OAUTH_SESSION_EMAIL);} catch (Exception ignored) {}
            return Map.of(EXISTS_KEY, false);
        }
    }

}
