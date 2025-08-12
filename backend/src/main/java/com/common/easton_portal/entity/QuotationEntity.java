//package com.common.easton_portal.entity;
//
//
//import lombok.*;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotBlank;
//
//@Setter
//@Getter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "quotation")
//public class QuotationEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quotation")
//    @SequenceGenerator(name = "quotation", sequenceName = "z_quotation", initialValue = 1, allocationSize = 1)
//    private long id;
//
//    @Column(columnDefinition = "TEXT")
//    @NotBlank
//    private String quoteNum;
//
//    @Column(columnDefinition = "TEXT")
//    @NotBlank
//    private String CustomerName;
//
//    @Column(columnDefinition = "TEXT")
//    private String assignee;
//
//    @Column(columnDefinition = "bigint default 0")
//    private long createdDate;
//}
