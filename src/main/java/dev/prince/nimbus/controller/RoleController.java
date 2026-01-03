package dev.prince.nimbus.controller;

import dev.prince.nimbus.dto.RoleDto;
import dev.prince.nimbus.port.in.RoleServiceInPort;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "RoleController")
public class RoleController {

    private final RoleServiceInPort roleServiceInPort;

    @GetMapping("/api/v1/roles")
    public List<RoleDto> listRoles() {
        log.info("Received request to list roles");
        return this.roleServiceInPort.listRoles();
    }

}
