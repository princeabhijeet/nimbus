package dev.prince.nimbus.validator;

import dev.prince.nimbus.dto.UserDto;
import dev.prince.nimbus.exception.UserValidationException;
import dev.prince.nimbus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserDtoValidator implements UserValidator {

    private final UserRepository userRepository;

    private static final String FIRST_NAME_BLANK_MESSAGE = "First name cannot be blank";
    private static final String FIRST_NAME_LENGTH_MESSAGE = "First name must be between 2 and 50 characters";
    private static final String USERNAME_BLANK_MESSAGE = "Username cannot be blank";
    private static final String USERNAME_FORMAT_MESSAGE = "Username format is invalid";
    private static final String EMAIL_BLANK_MESSAGE = "Email cannot be blank";
    private static final String EMAIL_FORMAT_MESSAGE = "Email format is invalid";
    private static final String ROLE_BLANK_MESSAGE = "User must have at least one role assigned";
    private static final String USERNAME_EXISTS_MESSAGE = "Username already exists";
    private static final String EMAIL_EXISTS_MESSAGE = "Email already exists";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[\\w.-]{3,20}$");

    private static final int FIRST_NAME_MIN_LENGTH = 2;
    private static final int FIRST_NAME_MAX_LENGTH = 50;

    @Override
    public void validate(UserDto userDto) {

        if (StringUtils.isBlank(userDto.getFirstName())) {
            throw new UserValidationException(HttpStatus.BAD_REQUEST.value(), FIRST_NAME_BLANK_MESSAGE);
        }

        if (userDto.getFirstName().length() < FIRST_NAME_MIN_LENGTH || userDto.getFirstName().length() > FIRST_NAME_MAX_LENGTH) {
            throw new UserValidationException(HttpStatus.BAD_REQUEST.value(), FIRST_NAME_LENGTH_MESSAGE);
        }

        if (StringUtils.isBlank(userDto.getUsername())) {
            throw new UserValidationException(HttpStatus.BAD_REQUEST.value(), USERNAME_BLANK_MESSAGE);
        }

        if (!USERNAME_PATTERN.matcher(userDto.getUsername()).matches()) {
            throw new UserValidationException(HttpStatus.BAD_REQUEST.value(), USERNAME_FORMAT_MESSAGE);
        }

        if (StringUtils.isBlank(userDto.getEmail())) {
            throw new UserValidationException(HttpStatus.BAD_REQUEST.value(), EMAIL_BLANK_MESSAGE);
        }

        if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
            throw new UserValidationException(HttpStatus.BAD_REQUEST.value(), EMAIL_FORMAT_MESSAGE);
        }

        if (CollectionUtils.isEmpty(userDto.getUserRoles())) {
            throw new UserValidationException(HttpStatus.BAD_REQUEST.value(), ROLE_BLANK_MESSAGE);
        }

        boolean existsByUsername = this.userRepository.existsByUsername(userDto.getUsername());
        if (existsByUsername) {
            throw new UserValidationException(HttpStatus.CONFLICT.value(), USERNAME_EXISTS_MESSAGE);
        }

        boolean existsByEmail = this.userRepository.existsByEmail(userDto.getEmail());
        if (existsByEmail) {
            throw new UserValidationException(HttpStatus.CONFLICT.value(), EMAIL_EXISTS_MESSAGE);
        }

    }

}
