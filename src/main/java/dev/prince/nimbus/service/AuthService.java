package dev.prince.nimbus.service;


import dev.prince.nimbus.dto.LoginRequestDto;
import dev.prince.nimbus.dto.LoginResponseDto;
import dev.prince.nimbus.exception.ClientRegistrationNotFoundException;
import dev.prince.nimbus.mapper.LoginResponseMapper;
import dev.prince.nimbus.port.in.AuthServiceInPort;
import dev.prince.nimbus.validator.LoginRequestValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Objects;

import static dev.prince.nimbus.constant.NimbusConstant.AUTHORIZATION_ENDPOINT_FORMAT;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceInPort {

    private final LoginResponseMapper loginResponseMapper;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final List<LoginRequestValidator> validators;

    private static final String CLIENT_REGISTRATION_NOT_FOUND_MESSAGE = "No client registration found for provider: %s";

    // login via OAuth2: via GitHub or Google
    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        this.validators.forEach(validator -> validator.validate(loginRequestDto));
        String provider = loginRequestDto.getProvider().toLowerCase();

        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(provider);

        if (Objects.isNull(clientRegistration)) {
            log.error("No client registration found for provider: {}", provider);
            throw new ClientRegistrationNotFoundException(HttpStatus.BAD_REQUEST.value(), String.format(CLIENT_REGISTRATION_NOT_FOUND_MESSAGE, provider));
        }

        String authorizationRequestUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(AUTHORIZATION_ENDPOINT_FORMAT)
                .buildAndExpand(provider)
                .toUriString();

        log.info("Authorization Request URL: {}", authorizationRequestUrl);
        return this.loginResponseMapper.map(authorizationRequestUrl);
    }

    @Override
    public void logout(HttpServletRequest request) {

        // get the current session if it exists, do not create a new one
        // false == if user has already logged out or the session has expired, ddon't create new empty session
        HttpSession session = request.getSession(Boolean.FALSE);

        // session.invalidate():
        // delete session key in Redis container database as we are using Spring Session Redis
        // and also remove session data from the local server memory
        if (null != session) {
            session.invalidate();
            log.info("User session invalidated successfully.");
        }

        // completely clear the security context for the current thread
        // even for remainder of this specific request's execution, user is not considered authenticated in the current thread.
        SecurityContextHolder.clearContext();
        log.info("Security context cleared successfully.");
    }

}
