// TenantCurrencyOrganization.java
package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "TenantCurrencyOrganization")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TenantCurrencyOrganization {

    @EmbeddedId
    private TenantCurrencyOrganizationPK id;

    // ตอนนี้ยังไม่ผูก relation ลดความเสี่ยง
}
