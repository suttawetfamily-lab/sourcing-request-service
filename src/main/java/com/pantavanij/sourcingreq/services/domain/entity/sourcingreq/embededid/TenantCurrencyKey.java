package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.*;
import javax.persistence.*;
import java.io.*;

@Builder
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantCurrencyKey implements Serializable {
    @Column(name = "TenantId")
    private Integer tenantId;

    @Column(name = "CurrencyId")
    private Integer currencyId;
}
