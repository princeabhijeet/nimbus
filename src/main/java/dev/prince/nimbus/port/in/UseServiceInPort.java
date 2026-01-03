package dev.prince.nimbus.port.in;

import dev.prince.nimbus.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UseServiceInPort {

    UserDto createUser(UserDto userDto);

    List<UserDto> listUsers();

    UserDto getUserById(UUID userId);

    UserDto getUserByUsername(String username);

    UserDto getUserByEmail(String email);

    boolean existsById(UUID id);

    boolean existsByUsername(String email);

    boolean existsByEmail(String email);

    boolean isAdminUser(UUID userId);

    void deleteUser(UUID userId);

}
