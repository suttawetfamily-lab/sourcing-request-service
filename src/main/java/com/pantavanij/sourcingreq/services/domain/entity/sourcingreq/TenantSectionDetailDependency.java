package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantSectionDetailDependencyKey;
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
@Table(name = "TenantSectionDetailDependency")
public class TenantSectionDetailDependency {

    @EmbeddedId
    private TenantSectionDetailDependencyKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tenantSectionDetailId")
    @JoinColumn(name = "TenantSectionDetailId")
    @JsonBackReference
    private TenantSectionDetail tenantSectionDetail;

    @Column(name = "Name")
    private String name;

    @Column(name = "Value")
    private String value;

    @Column(name = "GroupName")
    private String groupName;

    @Column(name = "Action")
    private String action;
}
