package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantRequestItemGridFieldKey;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "TenantRequestItemGridField")
public class TenantRequestItemGridField {

    @EmbeddedId
    private TenantRequestItemGridFieldKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tenantId")
    @JoinColumn(name = "TenantId")
    @JsonBackReference
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestItemGridFieldId")
    @JoinColumn(name = "RequestItemGridFieldId")
    @JsonBackReference
    private RequestItemGridField requestItemGridField;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("templateId")
    @JoinColumn(name = "TemplateId")
    private TenantTemplate template;

    private String privilegeCode;

    private String code;

    private String displayName;

    private Integer sequence;

    private Integer width;

    private String type;

    private boolean visible;

    private String tooltip;

    private String childField;

    private String align;

    private String createdBy;

    private Timestamp createdDate;

    private String updatedBy;

    private Timestamp updatedDate;


}
