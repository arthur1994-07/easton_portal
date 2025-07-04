package com.common.easton_portal.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "oauth")
public class OAuthEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oauth")
    @SequenceGenerator(name = "oauth", sequenceName = "z_oauth", initialValue = 1, allocationSize = 1)
    private long id;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String name;
    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String clientId;
    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String clientSecret;
    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String authUrl;
    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String tokenUrl;
    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String logoutUrl;
    @Column(columnDefinition = "boolean default false")
    private boolean disable;

    @Column(columnDefinition = "boolean default false")
    private boolean defaultAdministrator;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<RoleEntity> defaultRoles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domain")
    public final List<UserEntity> users = new ArrayList<>();
}
