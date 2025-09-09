package com.common.easton_portal.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Blob;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "setting")
public class SettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "setting")
    @SequenceGenerator(name = "setting", sequenceName = "z_setting", initialValue = 1, allocationSize = 1)
    private long id;
    @Column(name="data_key", columnDefinition="TEXT")
    @NotBlank
    private String key;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "content")
    private Blob content;
    @Column(columnDefinition = "boolean default false")
    private boolean systemType;
}
