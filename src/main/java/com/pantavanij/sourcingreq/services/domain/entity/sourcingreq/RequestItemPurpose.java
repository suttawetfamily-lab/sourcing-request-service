package com.pantavanij.sourcingreq.services.domain.entity.sourcingreq;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.RequestItemPurposeKey;
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
@Table(name = "RequestItemPurpose")
public class RequestItemPurpose {

    @EmbeddedId
    private RequestItemPurposeKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("requestItemId")
    @JoinColumn(name = "RequestItemId")
    @JsonBackReference
    private RequestItem requestItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("purposeId")
    @JoinColumn(name = "PurposeId")
    @JsonBackReference
    private Purpose purpose;
    
    @Column(name = "PurposeCode")
    private String purposeCode;
    
    @Column(name = "PurposeName")
    private String purposeName;

}
