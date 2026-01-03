package dev.prince.nimbus.service;

import dev.prince.nimbus.dto.UserDto;
import dev.prince.nimbus.entity.UserEntity;
import dev.prince.nimbus.entity.UserRoleEntity;
import dev.prince.nimbus.entity.RoleEntity;
import dev.prince.nimbus.exception.UserNotFoundException;
import dev.prince.nimbus.mapper.UserMapper;
import dev.prince.nimbus.port.in.UseServiceInPort;
import dev.prince.nimbus.repository.UserRepository;
import dev.prince.nimbus.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static dev.prince.nimbus.constant.NimbusConstant.ROLE_ADMIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UseServiceInPort {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final List<UserValidator> validators;

    private static final String USER_NOT_FOUND_BY_ID_MESSAGE = "User not found with id: %s";
    private static final String USER_NOT_FOUND_BY_USERNAME_MESSAGE = "User not found with username: %s";
    private static final String USER_NOT_FOUND_BY_EMAIL_MESSAGE = "User not found with email: %s";

    @Transactional
    @Override
    public UserDto createUser(UserDto userDTO) {
        // validate & map
        this.validators.forEach(validator -> validator.validate(userDTO));
        final UserEntity userEntity = this.userMapper.map(userDTO);

        // owning side assignment
        if (Objects.nonNull(userEntity.getUserRoles())) {
            userEntity.getUserRoles().forEach(role -> {
                if (null == role.getUser()) {
                    role.setUser(userEntity);
                }
            });
        }

        // persist & return
        UserEntity savedUserEntity = this.userRepository.save(userEntity);
        log.info("User created with ID: {} and username '{}'", savedUserEntity.getId(), savedUserEntity.getUsername());
        return this.userMapper.map(savedUserEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> listUsers() {
        List<UserEntity> userEntities = this.userRepository.findAll();
        return this.userMapper.map(userEntities);
    }

    @Override
    public UserDto getUserById(UUID userId) {
        Optional<UserEntity> optionalUserEntity = this.userRepository.findById(userId);
        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException(HttpStatus.NOT_FOUND.value(), String.format(USER_NOT_FOUND_BY_ID_MESSAGE, userId));
        }
        UserEntity userEntity = optionalUserEntity.get();
        return this.userMapper.map(userEntity);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        Optional<UserEntity> optionalUserEntity = this.userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException(HttpStatus.NOT_FOUND.value(), String.format(USER_NOT_FOUND_BY_USERNAME_MESSAGE, username));
        }
        UserEntity userEntity = optionalUserEntity.get();
        return this.userMapper.map(userEntity);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        Optional<UserEntity> optionalUserEntity = this.userRepository.findByEmail(email);
        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException(HttpStatus.NOT_FOUND.value(), String.format(USER_NOT_FOUND_BY_EMAIL_MESSAGE, email));
        }
        UserEntity userEntity = optionalUserEntity.get();
        return this.userMapper.map(userEntity);
    }

    @Override
    public boolean existsById(UUID id) {
        return this.userRepository.existsById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public boolean isAdminUser(UUID userId) {
        Optional<UserEntity> optionalUserEntity = this.userRepository.findById(userId);
        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException(HttpStatus.NOT_FOUND.value(), String.format(USER_NOT_FOUND_BY_ID_MESSAGE, userId));
        }
        UserEntity userEntity = optionalUserEntity.get();
        if (userEntity.getUserRoles() == null) {
            return Boolean.FALSE;
        }
        return userEntity.getUserRoles().stream()
                .map(UserRoleEntity::getRole)
                .filter(Objects::nonNull)
                .map(RoleEntity::getName)
                .anyMatch(ROLE_ADMIN::equalsIgnoreCase);
    }

    @Transactional
    @Override
    public void deleteUser(UUID userId) {
        Optional<UserEntity> optionalUserEntity = this.userRepository.findById(userId);
        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException(HttpStatus.NOT_FOUND.value(), String.format(USER_NOT_FOUND_BY_ID_MESSAGE, userId));
        }
        UserEntity userEntity = optionalUserEntity.get();
        this.userRepository.delete(userEntity);
        log.info("User deleted with ID: {} and username '{}'", userEntity.getId(), userEntity.getUsername());
    }

}
