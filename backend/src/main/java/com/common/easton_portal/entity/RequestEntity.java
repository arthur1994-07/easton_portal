package com.common.easton_portal.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "request")
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request")
    @SequenceGenerator(name = "request", sequenceName = "z_request", initialValue = 1, allocationSize = 1)
    private long id;

    @Column(columnDefinition = "bigint default 0")
    private long customerId;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String customerName;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String customerEmail;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(columnDefinition = "bigint default 0")
    private long createdDate;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String collectionName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quotation_id")
    private QuotationEntity quotation;
}
