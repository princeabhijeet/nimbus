package dev.prince.nimbus.validator;

import dev.prince.nimbus.dto.LoginRequestDto;

public interface LoginRequestValidator {

    void validate(LoginRequestDto loginRequestDto);

}
