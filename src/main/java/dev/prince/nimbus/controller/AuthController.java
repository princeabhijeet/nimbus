package dev.prince.nimbus.controller;

import dev.prince.nimbus.dto.LoginRequestDto;
import dev.prince.nimbus.dto.LoginResponseDto;
import dev.prince.nimbus.port.in.AuthServiceInPort;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "AuthController")
public class AuthController {

    private final AuthServiceInPort authServiceInPort;

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        log.info("Received login request for provider: {}", loginRequestDto.getProvider());
        return ResponseEntity.status(HttpStatus.OK).body(this.authServiceInPort.login(loginRequestDto));
    }

    @PostMapping("/api/v1/auth/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        this.authServiceInPort.logout(request);
        return ResponseEntity.noContent().build();
    }

}
