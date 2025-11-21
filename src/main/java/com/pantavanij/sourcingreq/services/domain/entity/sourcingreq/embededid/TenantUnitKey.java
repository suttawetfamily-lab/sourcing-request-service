package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.*;
import javax.persistence.*;
import java.io.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Data
public class TenantUnitKey implements Serializable {
    @Column(name = "TenantId")
    private Integer tenantId;

    @Column(name = "UnitId")
    private Integer unitId;
}
