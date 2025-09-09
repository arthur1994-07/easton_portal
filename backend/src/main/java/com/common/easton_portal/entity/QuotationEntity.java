package com.common.easton_portal.entity;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Blob;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "quotation")
public class QuotationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quotation")
    @SequenceGenerator(name = "quotation", sequenceName = "z_quotation", initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String quoteNum;

    @Column(columnDefinition = "TEXT")
    private String assignee;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "document")
    private Blob document;

}
