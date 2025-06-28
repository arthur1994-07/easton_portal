package com.common.easton_portal.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permission_role")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role")
    @SequenceGenerator(name = "role", sequenceName = "z_role", initialValue = 1, allocationSize = 1)
    private long id;

    @Column(columnDefinition="TEXT")
    @NotBlank
    private String name;

    @CollectionTable(name="permission_role_right", joinColumns = @JoinColumn(name = "role_id"))
    @ElementCollection
    @Column(columnDefinition="TEXT")
    private final Set<String> rights = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    private final Set<UserEntity> users  = new HashSet<>();
}