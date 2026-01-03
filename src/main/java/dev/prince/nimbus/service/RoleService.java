package dev.prince.nimbus.service;

import dev.prince.nimbus.dto.RoleDto;
import dev.prince.nimbus.entity.RoleEntity;
import dev.prince.nimbus.mapper.RoleMapper;
import dev.prince.nimbus.port.in.RoleServiceInPort;
import dev.prince.nimbus.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService implements RoleServiceInPort {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    @Override
    public List<RoleDto> listRoles() {
        List<RoleEntity> roleEntities = this.roleRepository.findAll();
        return this.roleMapper.map(roleEntities);
    }

}
