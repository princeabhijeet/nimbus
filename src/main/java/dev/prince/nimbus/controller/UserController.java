package dev.prince.nimbus.controller;

import dev.prince.nimbus.dto.UserDto;
import dev.prince.nimbus.port.in.UseServiceInPort;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "UserController")
public class UserController {

    private final UseServiceInPort userServiceInPort;

    @PostMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Received request to create user: {}", userDto);
        return this.userServiceInPort.createUser(userDto);
    }

    @GetMapping("/api/v1/users")
    public List<UserDto> listUsers() {
        log.info("Received request to list users");
        return this.userServiceInPort.listUsers();
    }

    @GetMapping("/api/v1/users/id/{user-id}")
    public UserDto getUserById(@PathVariable ("user-id") UUID userId) {
        log.info("Received request to get user with ID: {}", userId);
        return this.userServiceInPort.getUserById(userId);
    }

    @GetMapping("/api/v1/users/username/{username}")
    public UserDto getUserByUsername(@PathVariable ("username") String username) {
        log.info("Received request to get user with username: '{}'", username);
        return this.userServiceInPort.getUserByUsername(username);
    }

    @GetMapping("/api/v1/users/email/{email}")
    public UserDto getUserByEmail(@PathVariable ("email") String email) {
        log.info("Received request to get user with email: '{}'", email);
        return this.userServiceInPort.getUserByEmail(email);
    }

    @GetMapping("/api/v1/users/id/{user-id}/is-admin")
    public boolean isAdminUser(@PathVariable("user-id") UUID userId) {
        log.info("Received request to check if user is admin with ID: {}", userId);
        return this.userServiceInPort.isAdminUser(userId);
    }

    @DeleteMapping("/api/v1/users/id/{user-id}")
    public void deleteUser(@PathVariable("user-id") UUID userId) {
        log.info("Received request to delete user with ID: {}", userId);
        this.userServiceInPort.deleteUser(userId);
    }

}
