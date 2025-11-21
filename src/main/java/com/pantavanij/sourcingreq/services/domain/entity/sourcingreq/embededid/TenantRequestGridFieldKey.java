package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class TenantRequestGridFieldKey implements Serializable {
    @Column(name = "TenantId")
    private Integer tenantId;

//    @Column(name = "PrivilegeCode")
//    private String privilegeCode;

    @Column(name = "RequestGridFieldId")
    private Integer requestGridFieldId;

    @Column(name = "TemplateId")
    private Integer templateId;
}
