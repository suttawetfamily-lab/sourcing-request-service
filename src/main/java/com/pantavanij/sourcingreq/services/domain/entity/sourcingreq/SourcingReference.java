package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SourcingReference")
public class SourcingReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ถ้า RecId เป็น identity/autoincrement
    @Column(name = "RecId")
    private Long recId;

    @Column(name = "Type", nullable = false, length = 50)
    private String type;

    @Column(name = "TenantId", nullable = false)
    private Integer tenantId;

    @Column(name = "TemplateId")
    private Integer templateId;

    @Column(name = "Prefix", length = 20)
    private String prefix;

    @Column(name = "Postfix", length = 20)
    private String postfix;

    @Column(name = "Sequence", nullable = false)
    private Integer sequence;

    @Column(name = "SyncAllTemplates", nullable = false)
    private Boolean syncAllTemplates = false;

    @Column(name = "CreatedBy", length = 255)
    private String createdBy;

    @Column(name = "CreatedDate")
    private Timestamp createdDate;

    @Column(name = "UpdatedBy", length = 255)
    private String updatedBy;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;
}
