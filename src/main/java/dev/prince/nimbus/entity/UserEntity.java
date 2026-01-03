package dev.prince.nimbus.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "unique_social_id", columnNames = {"provider", "providerUid"}), // Contract: One unique record per social identity: Oauth2 login
        @UniqueConstraint(name = "unique_local_email", columnNames = {"email"}) // Contract: One unique record per local email: username/password login
})
public class UserEntity extends AuditEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    private String firstName;

    @Column
    private String lastName;

    @Column
    private Boolean isActive = true;

    @Column(unique = true)
    private String username;

    @Column
    private String email;

    @Column
    private String provider;

    @Column
    private String providerUid;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<UserRoleEntity> userRoles = new HashSet<>();


}
