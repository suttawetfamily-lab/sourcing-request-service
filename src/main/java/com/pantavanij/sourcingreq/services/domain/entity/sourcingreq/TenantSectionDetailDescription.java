package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "TenantSectionDetailDescription")
public class TenantSectionDetailDescription {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "RecId")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TenantSectionDetailId")
    @JsonBackReference
    private TenantSectionDetail tenantSectionDetail;

    @Column(name = "Description")
    private String description;

    @Column(name = "DescriptionLabelAction")
    private String descriptionLabelAction;

    @Column(name = "DescriptionAction")
    private String descriptionAction;

    @Column(name = "Disable")
    private Integer disable;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;
}
