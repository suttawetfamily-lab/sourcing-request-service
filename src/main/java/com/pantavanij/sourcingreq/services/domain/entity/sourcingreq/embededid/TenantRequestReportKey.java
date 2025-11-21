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
public class TenantRequestReportKey implements Serializable {
    @Column(name = "TenantId")
    private Integer tenantId;

    @Column(name = "RequestReportId")
    private Integer requestReportId;

    @Column(name = "TenantSectionDetailId")
    private Long tenantSectionDetailId;

}