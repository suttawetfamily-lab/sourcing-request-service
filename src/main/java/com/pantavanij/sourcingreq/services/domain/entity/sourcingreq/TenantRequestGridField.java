package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantRequestGridFieldKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "TenantRequestGridField")
public class TenantRequestGridField {

    @EmbeddedId
    private TenantRequestGridFieldKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tenantId")
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestGridFieldId")
    @JoinColumn(name = "RequestGridFieldId")
    @JsonBackReference
    private RequestGridField requestGridField;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("templateId")
    @JoinColumn(name = "TemplateId")
    private TenantTemplate template;

    private String privilegeCode;

    private String code;

    private String displayName;

    private Integer sequence;

    private boolean searchable;

    private String sorting;

    private Integer width;

    private String type;

    private boolean visible;

    private String createdBy;

    private Timestamp createdDate;

    private String updatedBy;

    private Timestamp updatedDate;

}
