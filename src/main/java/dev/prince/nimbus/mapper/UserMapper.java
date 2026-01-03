package dev.prince.nimbus.mapper;

import dev.prince.nimbus.dto.UserDto;
import dev.prince.nimbus.dto.UserRoleDto;
import dev.prince.nimbus.entity.UserEntity;
import dev.prince.nimbus.entity.UserRoleEntity;
import dev.prince.nimbus.exception.RoleNotFoundException;
import dev.prince.nimbus.repository.RoleRepository;
import dev.prince.nimbus.entity.RoleEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class UserMapper {

    private static final String ROLE_NOT_FOUND_MESSAGE = "Role not found with id: %s";

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // create new user
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", source = "dto.firstName")
    @Mapping(target = "lastName", source = "dto.lastName")
    @Mapping(target = "username", source = "dto.username")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "userRoles", source = "dto.userRoles")
    @Mapping(target = "createdBy", constant = "system")
    @Mapping(target = "updatedBy", constant = "system")
    public abstract UserEntity map(UserDto dto);

    public abstract List<UserEntity> mapEntities(List<UserDto> dtos);

    // create new user: UserRole mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // set in service layer
    @Mapping(target = "role", source = "dto.role.id", qualifiedByName = "roleIdToRole")
    @Mapping(target = "createdBy", constant = "system")
    @Mapping(target = "updatedBy", constant = "system")
    public abstract UserRoleEntity map(UserRoleDto dto);

    // create new user: UserRole mappings
    @Named("roleIdToRole")
    protected RoleEntity roleIdToRole(Long roleId) {
        if (null == roleId) {
            return null;
        }
        return this.roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(HttpStatus.NOT_FOUND.value(), String.format(ROLE_NOT_FOUND_MESSAGE, roleId)));
    }

    // ######################################################################################

    // get user details
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "firstName", source = "entity.firstName")
    @Mapping(target = "lastName", source = "entity.lastName")
    @Mapping(target = "username", source = "entity.username")
    @Mapping(target = "email", source = "entity.email")
    @Mapping(target = "isActive", source = "entity.isActive")
    @Mapping(target = "userRoles", source = "entity.userRoles")
    public abstract UserDto map(UserEntity entity);

    public abstract List<UserDto> map(List<UserEntity> entities);

    // get user details: UserRoleEntity to UserRoleDto
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "user", ignore = true) // set in @AfterMapping 'afterMapUserEntity'
    @Mapping(target = "role", source = "entity.role")
    public abstract UserRoleDto map(UserRoleEntity entity);

    // ######################################################################################

    // get user details: after mapping to set back-reference from UserRoleDto to UserDto
    @AfterMapping
    protected void afterMapUserEntity(UserEntity source, @MappingTarget UserDto target) {
        if (null == target) {
            return;
        }
        if (null != target.getUserRoles()) {
            for (UserRoleDto userRoleDto : target.getUserRoles()) {
                userRoleDto.setUser(target);
            }
        }
    }

}
