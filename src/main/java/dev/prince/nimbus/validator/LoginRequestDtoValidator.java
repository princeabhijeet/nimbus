package dev.prince.nimbus.validator;

import dev.prince.nimbus.dto.LoginRequestDto;
import dev.prince.nimbus.exception.LoginValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginRequestDtoValidator implements LoginRequestValidator {

    private static final String PROVIDER_IS_REQUIRED_MESSAGE = "Provider is required";

    @Override
    public void validate(LoginRequestDto loginRequestDto) {

        if (StringUtils.isBlank(loginRequestDto.getProvider())) {
            throw new LoginValidationException(HttpStatus.BAD_REQUEST.value(), PROVIDER_IS_REQUIRED_MESSAGE);
        }

    }

}
