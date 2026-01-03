package dev.prince.nimbus.port.in;

import dev.prince.nimbus.dto.LoginRequestDto;
import dev.prince.nimbus.dto.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthServiceInPort {

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    void logout(HttpServletRequest request);

}
