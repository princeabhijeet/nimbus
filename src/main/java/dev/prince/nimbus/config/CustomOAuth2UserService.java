package dev.prince.nimbus.config;

import dev.prince.nimbus.entity.UserEntity;
import dev.prince.nimbus.entity.UserRoleEntity;
import dev.prince.nimbus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;

    private static final String OAUTH_SESSION_EMAIL = "oauth_login_email";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // Log provider attributes for debugging
        try {
            log.info("OAuth2 provider attributes: {}", oauth2User.getAttributes());
        } catch (Exception ignored) { }

        // Try to extract email from attributes
        Object emailObj = oauth2User.getAttributes().get("email");
        String email = emailObj != null ? emailObj.toString() : null;
        if (email == null) {
            // check HTTP session for pre-validated email put there by LoginController
            try {
                RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    Object sessEmail = attrs.getAttribute(OAUTH_SESSION_EMAIL, RequestAttributes.SCOPE_SESSION);
                    if (sessEmail != null) {
                        email = sessEmail.toString();
                        log.info("Found pre-validated email from session: {}", email);
                    }
                }
            } catch (Exception e) {
                log.error("Unable to read session for pre-validated email: {}", e.getMessage());
            }
        }

        if (email == null) {
            // fallback to login (username) - but prefer email
            Object login = oauth2User.getAttributes().get("login");
            email = login != null ? login.toString() : null;
            if (email != null) {
                log.info("Falling back to provider 'login' attribute: {}", email);
            }
        }

        if (email == null) {
            log.warn("No email available from provider or session; denying OAuth2 login");
            throw new OAuth2AuthenticationException("Email not provided by OAuth2 provider and no prevalidated email in session");
        }

        log.debug("Resolving local user for email: {}", email);
        var userOpt = this.userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.info("No local user found for email: {}", email);
            // User not registered locally - deny login
            throw new OAuth2AuthenticationException("User not registered: " + email);
        }

        // remove session-stored prevalidated email now that we've matched it
        try {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                attrs.removeAttribute(OAUTH_SESSION_EMAIL, RequestAttributes.SCOPE_SESSION);
                log.info("Removed oauth session email attribute after successful match");
            }
        } catch (Exception ex) {
            log.info("Unable to remove oauth session email attribute: {}", ex.getMessage());
        }

        UserEntity user = userOpt.get();
        // build authorities from roles
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getUserRoles() != null) {
            for (UserRoleEntity ur : user.getUserRoles()) {
                if (ur.getRole() != null && ur.getRole().getName() != null) {
                    authorities.add(new SimpleGrantedAuthority(ur.getRole().getName()));
                }
            }
        }

        // return a DefaultOAuth2User with user's authorities and attributes from provider
        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(authorities, oauth2User.getAttributes(), "login");
    }
}
