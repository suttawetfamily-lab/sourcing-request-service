package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TenantTemplate")
public class TenantTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecId", nullable = false)
    private Integer recId;

    @Column(name = "TenantId", nullable = false)
    private Integer tenantId;

    @Column(name = "Name", length = 20, nullable = false)
    private String name;

    @Column(name = "Description", length = 500)
    private String description;

    @Column(name = "Sequence")
    private Integer sequence = 1; // ค่า default จาก table

    @Column(name = "[Default]")
    private Boolean defaultFlag = false;

    @Column(name = "Active")
    private Boolean active = true;

    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

    @Column(name = "CreatedDate")
    private LocalDateTime createdDate;

    @Column(name = "UpdatedBy", length = 50)
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private LocalDateTime updatedDate;
}
