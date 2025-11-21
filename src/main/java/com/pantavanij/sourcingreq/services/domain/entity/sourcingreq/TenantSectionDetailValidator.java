package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.TenantSectionDetailValidatorKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "TenantSectionDetailValidator")
//@IdClass(TenantSectionDetailValidatorPK.class)
public class TenantSectionDetailValidator {

    @EmbeddedId
    private TenantSectionDetailValidatorKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tenantSectionDetailId")
    @JoinColumn(name = "TenantSectionDetailId")
    @JsonBackReference
    private TenantSectionDetail tenantSectionDetail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("validatorId")
    @JoinColumn(name = "ValidatorId")
    @JsonBackReference
    private Validator validator;

    @Column(name = "Name")
    private String name;

    @Column(name = "Value")
    private String value;
    
    @Column(name = "sequence")
    private Integer sequence;
}
