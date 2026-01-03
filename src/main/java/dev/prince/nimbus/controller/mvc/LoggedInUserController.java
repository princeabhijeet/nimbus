package dev.prince.nimbus.controller.mvc;

import dev.prince.nimbus.dto.UserDto;
import dev.prince.nimbus.port.in.UseServiceInPort;
import dev.prince.nimbus.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "LoggedInUserMvcController")
public class LoggedInUserController {

    private final UseServiceInPort userServiceInPort;
    private final UserRepository userRepository;

    @GetMapping("/api/v1/mvc/user/me")
    public ResponseEntity<UserDto> getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            log.info("No authentication present when fetching logged-in user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String principalName = auth.getName();
        log.info("Fetching logged-in user for principal: {}", principalName);
        // prefer email lookup
        var userOpt = this.userRepository.findByEmail(principalName);
        if (userOpt.isEmpty()) {
            userOpt = this.userRepository.findByUsername(principalName);
        }
        if (userOpt.isEmpty()) {
            log.warn("Authenticated principal '{}' not found in user table", principalName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var user = userOpt.get();
        log.info("Found user id={} username={}", user.getId(), user.getUsername());
        UserDto dto = this.userServiceInPort.getUserById(user.getId());
        // log dto for debugging client-side rendering issues
        try {
            log.info("Returning UserDto for UI: {}", dto);
        } catch (Exception e) {
            log.info("Unable to log UserDto: {}", e.getMessage());
        }
        return ResponseEntity.ok(dto);
    }
}
