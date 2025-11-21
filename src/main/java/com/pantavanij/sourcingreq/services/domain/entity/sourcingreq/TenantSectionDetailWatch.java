package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantSectionDetailWatchKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "TenantSectionDetailWatch")
public class TenantSectionDetailWatch {
    @EmbeddedId
    private TenantSectionDetailWatchKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tenantSectionDetailId")
    @JoinColumn(name = "TenantSectionDetailId")
    @JsonBackReference
    private TenantSectionDetail tenantSectionDetail;

    @Column(name = "FieldName")
    private String fieldName;

    @Column(name = "OriginalFieldName")
    private String originalFieldName;

    @Column(name = "UpdatedFieldName")
    private String updatedFieldName;

    @Column(name = "[Values]")
    private String values;

    @Column(name = "GroupName")
    private String groupName;
}
