package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "TenantCategory")
public class TenantCategory {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TypeId")
    @JsonBackReference
    private Type type;

    @Column(name = "Code")
    private String categoryCode;

    @Column(name = "Name")
    private String categoryName;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

}
