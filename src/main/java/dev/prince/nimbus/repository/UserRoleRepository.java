package dev.prince.nimbus.repository;

import dev.prince.nimbus.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UUID> {

    void deleteByUser_IdAndRole_Name(UUID userId, String roleName);

    boolean existsByUser_IdAndRole_Name(UUID userId, String roleName);

}
