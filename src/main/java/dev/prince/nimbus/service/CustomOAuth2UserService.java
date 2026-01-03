package dev.prince.nimbus.service;

import dev.prince.nimbus.entity.RoleEntity;
import dev.prince.nimbus.entity.UserEntity;
import dev.prince.nimbus.entity.UserRoleEntity;
import dev.prince.nimbus.exception.RoleNotFoundException;
import dev.prince.nimbus.repository.RoleRepository;
import dev.prince.nimbus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static dev.prince.nimbus.constant.NimbusConstant.ROLE_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // OAuth2 attributes
    private static final String ATTRIBUTE_EMAIL = "email";
    private static final String ATTRIBUTE_GIVEN_NAME = "given_name";
    private static final String ATTRIBUTE_FAMILY_NAME = "family_name";

    private static final String SYSTEM_OAUTH2 = "system_oauth2";
    private static final String SYSTEM_MERGE_PREFIX = "system_merge_";

    private static final String ROLE_NOT_FOUND_MESSAGE = "Role '%s' not found.";

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // fetch user attributes from the provider (GitHub/Google)
        OAuth2User oauth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();

        // permanent unique ID 'providerUid' from the provider (e.g., GitHub 'id' or Google 'sub')
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerUid = Objects.toString(attributes.get(userNameAttributeName), null);
        String username = attributes.get(userNameAttributeName).toString();
        String email = Objects.toString(attributes.get(ATTRIBUTE_EMAIL), null);

        log.info("Processing OAuth2 login for provider: {}, providerUid: {}, username: {}, email: {}", provider, providerUid, username, email);

        // check if user already exists by provider and providerUid, return existing user & do not create a new one
        Optional<UserEntity> existingUserByProvider = this.userRepository.findByProviderAndProviderUid(provider, providerUid);
        if (existingUserByProvider.isPresent()) {
            UserEntity existingUserEntity = existingUserByProvider.get();
            log.info("Returning existing user '{}' found via provider identity: {}", (null != existingUserEntity.getUsername()) ? existingUserEntity.getUsername() : existingUserEntity.getId(), providerUid);
            return oauth2User;
        }

        // check if email exists via another provider, link accounts if email is present
        if (email != null) {
            Optional<UserEntity> existingUserByEmail = this.userRepository.findByEmail(email);
            if (existingUserByEmail.isPresent()) {
                UserEntity existingUserEntity = existingUserByEmail.get();
                log.info("Merging Identity: Linking provider '{}' account to existing user '{}' found via email: {}", provider, (null != existingUserEntity.getUsername()) ? existingUserEntity.getUsername() : existingUserEntity.getId(), email);
                linkProviderToExistingUser(existingUserByEmail.get(), provider, providerUid);
                return oauth2User;
            }
        }

        // if user does not exist, register a new user
        registerNewUser(attributes, provider, providerUid, email, username);
        return oauth2User;
    }

    private void linkProviderToExistingUser(UserEntity existingUser, String provider, String providerUid) {
        existingUser.setProvider(provider);
        existingUser.setProviderUid(providerUid);
        existingUser.setUpdatedBy(SYSTEM_MERGE_PREFIX + provider);
        this.userRepository.save(existingUser);
    }

    private void registerNewUser(Map<String, Object> attributes, String provider, String providerUid, String email, String username) {
        // Minimalist approach: Names are nullable; user can update them later in Profile.
        UserEntity newUserEntity = UserEntity.builder()
                .username(username)
                .email(email)
                .firstName((String) attributes.get(ATTRIBUTE_GIVEN_NAME)) // Grab if available (Google)
                .lastName((String) attributes.get(ATTRIBUTE_FAMILY_NAME))  // Grab if available (Google)
                .provider(provider)
                .providerUid(providerUid)
                .isActive(true)
                .createdBy(SYSTEM_OAUTH2)
                .updatedBy(SYSTEM_OAUTH2)
                .build();

        // Assign default ROLE_USER from NimbusConstant
        RoleEntity defaultRole = this.roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException(HttpStatus.NOT_FOUND.value(), String.format(ROLE_NOT_FOUND_MESSAGE, ROLE_USER)));

        UserRoleEntity userRole = UserRoleEntity.builder()
                .user(newUserEntity)
                .role(defaultRole)
                .createdBy(SYSTEM_OAUTH2)
                .updatedBy(SYSTEM_OAUTH2)
                .build();

        newUserEntity.setUserRoles(Set.of(userRole));

        userRepository.save(newUserEntity);
        log.info("New user registered with username '{}' via provider '{}'", username, provider);
    }

}
