package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.*;

import javax.persistence.*;
import java.io.*;

@AllArgsConstructor
@Builder
@Data
@Embeddable
@NoArgsConstructor
public class TenantEmailActivityKey implements Serializable {
    @Column(name = "TenantId")
    private Integer tenantId;

    @Column(name = "EmailActivityId")
    private Long emailActivityId;

    @Column(name = "ActivityId")
    private Integer activityId;
}
