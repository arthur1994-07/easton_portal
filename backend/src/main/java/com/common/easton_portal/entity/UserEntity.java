package com.common.easton_portal.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_account")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user")
    @SequenceGenerator(name = "user", sequenceName = "z_user", initialValue = 1, allocationSize = 1)
    private long id;

//    set up oauth

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String domainUserId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oauth_id")
    private OAuthEntity domain;

    @Column(columnDefinition = "TEXT")
    private String username;
    @Column(columnDefinition = "TEXT")
    private String accountName;
    @Column(columnDefinition = "TEXT")
    private String email;
    @Column(columnDefinition = "boolean default false")
    private boolean disable;
    @Column(columnDefinition = "boolean default false")
    private boolean administrator;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "id"))
    private final Set<RoleEntity> roles = new HashSet<>();
}
