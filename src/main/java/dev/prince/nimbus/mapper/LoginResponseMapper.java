package dev.prince.nimbus.mapper;

import dev.prince.nimbus.dto.LoginResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class LoginResponseMapper {

    // login
    @Mapping(target = "authorizationRequestUrl", source = "authorizationRequestUrl")
    public abstract LoginResponseDto map(String authorizationRequestUrl);

}
