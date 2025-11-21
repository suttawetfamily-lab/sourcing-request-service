package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TenantOrganizationTemplate")
public class TenantOrganizationTemplate {

    @EmbeddedId
    private TenantOrganizationTemplatePK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TemplateId", referencedColumnName = "RecId", insertable = false, updatable = false)
    private TenantTemplate tenantTemplate;

}
