package dev.prince.nimbus.mapper;

import dev.prince.nimbus.dto.RoleDto;
import dev.prince.nimbus.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class RoleMapper {

    // get role details
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    public abstract RoleDto map(RoleEntity entity);

    public abstract List<RoleDto> map(List<RoleEntity> entities);

}
