package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Builder
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantRequestItemReportKey implements Serializable {
    @Column(name = "TenantId")
    private Integer tenantId;

    @Column(name = "RequestItemReportId")
    private Integer requestItemReportId;

    @Column(name = "TenantSectionDetailId")
    private Long tenantSectionDetailId;

}